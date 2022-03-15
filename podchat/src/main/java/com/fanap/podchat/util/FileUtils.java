package com.fanap.podchat.util;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Random;


public class FileUtils {

    private FileUtils() {
    } //private constructor to enforce Singleton pattern

    /**
     * TAG for log messages.
     */
    private static final String TAG = "CHAT_SDK_FILES";
    private static final boolean DEBUG = false; // Set to true to enable logging


    public static final String Media = "Podchat";


    public static final String FILES = Media + "/Files";
    public static final String VOICES = Media + "/Voices";
    public static final String VIDEOS = Media + "/Videos";
    public static final String SOUNDS = Media + "/Sounds";
    public static final String PICTURES = Media + "/Pictures";
    public static final String LOGS = Media + "/Files/LOGS";


    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    private static final String HIDDEN_PREFIX = ".";

    private static File downloadDirectory;


    public static void saveLogs() {

        Log.w(TAG, "Logcat save");
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            process = Runtime.getRuntime().exec("logcat -f " + "/storage/emulated/0/" + "PodChatLog.txt");
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Logcat save error: " + e.getMessage());

        }

    }

    public static void appendLog(String text) throws java.io.IOException {


        File logFile = getLogFile();

        if (logFile == null || !logFile.exists()) {
            throw new IOException("Create Log file failed!");
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "Appending  to log failed! cause: " + e.getMessage());
        } catch (Exception ex) {
            Log.e(TAG, "Logger exception: " + ex.getMessage());
        }
    }

    public static void shareLogs(Context context) {

        try {

            File file = getLogFile();

            if (file == null) {
                Log.e(TAG, "No Log file found!");
                return;
            }

            Uri uri = FileProvider.getUriForFile(context, context
                    .getApplicationContext()
                    .getPackageName() + ".provider", file);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(sharingIntent, "share file with"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static File getLogFile() throws IOException {


        File dire = getLogsDirectory();
        File logFile;
        boolean resultDire;


       if(dire!=null){
           if (!dire.exists()) {
               resultDire = dire.mkdirs();
           } else {
               resultDire = true;
           }

           if (resultDire) {
               logFile = new File(dire, "PodChatLog.txt");
               if (!logFile.exists()) {
                   boolean resultFile = logFile.createNewFile();
                   if (resultFile) return logFile;
               } else return logFile;
           }

       }

        return null;
    }


    public static void setDownloadDirectory(File cacheDire) {
        downloadDirectory = cacheDire;
    }

    public static File getDownloadDirectory() {
        return downloadDirectory;
    }

    public static File getOrCreateDownloadDirectory(String path) {
        return new File(downloadDirectory, path);
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */


    public static boolean isImage(String mimType) {

        return mimType.equals("image/jpeg") || mimType.equals("image/bmp") || mimType.equals("image/gif")
                || mimType.equals("image/jpg") || mimType.equals("image/png");
    }

    public static boolean isGif(String mimType) {

        return mimType.equals("image/gif");
    }

    public static File findFileInFolder(File filesFolder, String fileName) {

        File[] filesInFolder = filesFolder.listFiles();

        if (filesInFolder != null) {
            for (File file :
                    filesInFolder) {
                if (file.isFile()) {
                    String name = file.getName();
                    int lastDot = name.lastIndexOf(".");
                    if (lastDot > 0) {
                        name = name.substring(0, lastDot);
                        if (name.equals(fileName)) {
                            return file;
                        }
                    }
                }
            }

        }


        return null;
    }


    public static void copy(File src, File dst) throws IOException {

        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }


    public static boolean clearDirectory(String path) {

        File directory;

        if (downloadDirectory == null)
            directory = getOrCreateDirectory(path);
        else {
            directory = new File(downloadDirectory, path);
        }

        if (directory != null && directory.exists()) {

            if (directory.isDirectory()) {

                return clearFolder(directory);

            } else {

                return directory.delete();
            }

        } else return false;

    }

    private static boolean clearFolder(File directory) {

        boolean successClearFolder = true;

        int totalFiles = 0;

        int deletedFiles = 0;

        File[] subFolders = directory.listFiles();

        for (File file :
                subFolders) {

            if (!file.isDirectory()) {

                totalFiles++;

                boolean success = file.delete();

                if (success) {

                    deletedFiles++;
                }


            } else successClearFolder = clearFolder(file);

        }

        return totalFiles > 0 && totalFiles == deletedFiles && successClearFolder;

    }

    public static long getCacheSize(Context context) {

        File databaseFile = context.getDatabasePath("cache.db");

        if (databaseFile.exists()) return databaseFile.length();

        return 0;
    }


    public static long getStorageSize(String path) {


        File directory;

        if (downloadDirectory == null)
            directory = getOrCreateDirectory(path);
        else {
            directory = new File(downloadDirectory, path);
        }

        if (directory != null && directory.exists()) {

            if (directory.isDirectory()) {

                return getFolderSize(directory);

            }

            return directory.length();
        }
        return 0;
    }

    public static long getFolderSize(File folder) {

        long totalSize = 0;

        File[] subFolders = folder.listFiles();

        for (File file :
                subFolders) {

            if (!file.isDirectory()) {

                totalSize += file.length();

            } else totalSize += getFolderSize(file);

        }

        return totalSize;
    }


    public static long getFreeSpace() {


        StatFs stat;

        if (downloadDirectory != null)
            stat = new StatFs(downloadDirectory.getPath());
        else
            stat = new StatFs(Environment.getExternalStorageDirectory().getPath());


        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }

//        long megAvailable = bytesAvailable / (1024 * 1024);


        return bytesAvailable;
    }


    public static File getMediaFolder() {

        return new File(Environment.getExternalStorageDirectory(), "Podchat");


    }


    public static File getOrCreateCustomDirectory(File directory, String path) {

        File destFolder = new File(directory, path);

        boolean createDir = true;

        if (!destFolder.exists())
            createDir = destFolder.mkdir();


        if (createDir) return destFolder;
        else return null;


    }


    public static File getOrCreateDirectory(String path) {

        File directory = Environment.getExternalStorageDirectory();

        File destFolder = new File(directory, path);

        boolean createDir;

        if (!destFolder.exists()) {

            createDir = destFolder.mkdirs();

        } else {

            createDir = true;

        }

        if (!createDir) return null;

        return destFolder;
    }


//    public static File getDownloadCacheDirectory(String path){
//
//        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//        File destFolder = new File(directory, path);
//
//        boolean createDir;
//
//        if (!destFolder.exists()) {
//
//            createDir = destFolder.mkdirs();
//
//        } else {
//
//            createDir = true;
//
//        }
//
//        if (!createDir) return null;
//
//        return destFolder;
//
//    }

    public static File getPublicFilesDirectory() {


        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    @Nullable
    public static String getExtension(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }


    public static String getExtensionFromMimType(@Nullable String mimType) {


        String extension = "";

        if (!Util.isNullOrEmpty(mimType))
            extension = mimType.split("/")[1];


        return extension;

    }


    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(@Nullable String url) {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://");
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @return uri
     */
    @Nullable
    public static Uri getUri(@Nullable File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @return
     */
    @Nullable
    public static File getPathWithoutFilename(@Nullable File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
//    @Nullable
//    public static String getMimeType(File file) {
//
//        String extension = getExtension(file.getName());
//
//        if (extension.length() > 0)
//            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
//
//        return "application/octet-stream";
//    }

//    @Nullable
//    public static String getMimeType(Uri uri, Context context) {
//
//        return context.getContentResolver().getType(uri);
//
//    }

    @Nullable
    public static String getMimeType(Uri uri,Context context) {

        String mimeType = null;

        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    /**
     * @return The MIME type for the give Uri.
     */
//    @Nullable
//    public static String getMimeType(@NonNull Context context, @NonNull Uri uri) {
//        File file = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            file = new File(Objects.requireNonNull(getPath(context, uri)));
//        }
//        return getMimeType(file);
//    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {@link LocalStorageProvider}.
     * @author paulburke
     */
    public static boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, @NonNull Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(@NonNull final Context context, @NonNull final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File getFile(@NonNull Context context, @Nullable Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
//    @Nullable
//    public static Bitmap getThumbnail(@NonNull Context context, @NonNull File file) {
//        return getThumbnail(context, getUri(file), getMimeType(file.),context);
//    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
//    @Nullable
//    public static Bitmap getThumbnail(@NonNull Context context, @NonNull Uri uri) {
//        return getThumbnail(context, uri, getMimeType(context, uri));
//    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    private static Bitmap getThumbnail(@NonNull Context context, @NonNull Uri uri, @NonNull String mimeType) {
        if (DEBUG)
            Log.d(TAG, "Attempting to get thumbnail");

        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
            return null;
        }

        Bitmap bm = null;
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG)
                        Log.d(TAG, "Got thumb ID: " + id);

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);
                    } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.e(TAG, "getThumbnail", e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return bm;
    }

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    @NonNull
    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    public static File saveBitmap(Bitmap bitmap, String name) throws Exception {

        File destinationFolder = getPicturesDirectory();

        if (destinationFolder != null && !destinationFolder.exists()) {
            boolean r = destinationFolder.mkdirs();
            if (!r) throw new Exception("Couldn't create path");
        }

        OutputStream fOut = null;
//        Integer counter = 0;
        int counter = randomNumber(1, 1000);
        // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        File file = new File(destinationFolder, name + counter + ".jpg");

        try {

            if (!file.exists()) {
                boolean re = file.createNewFile();
                if (!re) throw new Exception("Couldn't create file");

            }

            fOut = new FileOutputStream(file);

            // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

        } catch (java.io.IOException e) {

            Log.e(TAG, "Error Saving Bitmap: " + e.getMessage());

            return null;
        }
        return file;
    }

    private static File getPicturesDirectory() {
        return FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getOrCreateDirectory(FileUtils.PICTURES);
    }

    private static File getFilesDirectory() {
        return FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getOrCreateDirectory(FileUtils.FILES);
    }

    private static File getLogsDirectory() {
        return FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.LOGS) : FileUtils.getOrCreateDirectory(FileUtils.LOGS);
    }


    public static int randomNumber(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }


}