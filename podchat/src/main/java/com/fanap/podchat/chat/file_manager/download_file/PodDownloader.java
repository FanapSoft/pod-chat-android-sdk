package com.fanap.podchat.chat.file_manager.download_file;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.util.Log;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.networking.ProgressResponseBody;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PodDownloader {

    private static String TAG = "CHAT_SDK";


    public interface IDownloaderError {

        void errorOnWritingToFile();

        void errorOnDownloadingFile(int errorCode);

        void errorUnknownException(String cause);

    }


    public static long download(
            ProgressHandler.IDownloadFile progressHandler,
            String uniqueId,
            File destinationFolder,
            String url,
            String fileName,
            String hashCode,
            long id,
            Context context,
            IDownloaderError iDownloader,
            long bytesAvailable) {

        File downloadTempFile = new File(destinationFolder, fileName);


        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);

        DownloadManager.Request dlManagerRequest = new DownloadManager.Request(uri);

        dlManagerRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        dlManagerRequest.setDestinationUri(Uri.fromFile(downloadTempFile));

        long downloadId = downloadManager.enqueue(dlManagerRequest);


        new Thread(() -> {

            int bytesDownloaded = 0;

            while (true) {

                try {
                    DownloadManager.Query dlManagerQuery = new DownloadManager.Query();
                    dlManagerQuery.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(dlManagerQuery);
                    cursor.moveToFirst();


                    int newBytesDownloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                    int totalBytes = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));


                    if (totalBytes > bytesAvailable) {

                        progressHandler.onLowFreeSpace(uniqueId, url);

                        break;
                    }


                    final int dl_progress = (int) ((newBytesDownloaded * 100L) / totalBytes);

                    if (totalBytes != -1 && bytesDownloaded != newBytesDownloaded) {

                        progressHandler.onProgressUpdate(uniqueId, newBytesDownloaded, totalBytes);
                        progressHandler.onProgressUpdate(uniqueId, dl_progress);
                        bytesDownloaded = newBytesDownloaded;

                    }

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {

                        String mime = downloadManager.getMimeTypeForDownloadedFile(downloadId);

                        String extension = FileUtils.getExtensionFromMimType(mime);

                        File downloadedFile = new File(destinationFolder, fileName + "." + extension);

                        boolean savingSuccess = downloadTempFile.renameTo(downloadedFile);

                        if (savingSuccess) {

                            ChatResponse<ResultDownloadFile> response = generateDownloadResult(hashCode, id, downloadedFile);

                            progressHandler.onFileReady(response);

//                            if (!cache) {
//                                downloadedFile.delete();
//                            }

                        } else {

                            iDownloader.errorOnWritingToFile();
                        }

                        break;
                    }

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {

                        int errorCode = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));

                        Log.e(TAG, "Error in downloading file! DownloadManager error code: " + errorCode);

                        iDownloader.errorOnDownloadingFile(errorCode);

                        break;
                    }


                } catch (CursorIndexOutOfBoundsException e) {
                    Log.i(TAG, "Download Canceled by client");
                    break;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    iDownloader.errorUnknownException(exc.getMessage());
                    break;
                }
            }

        }).start();


        return downloadId;
    }


    public static ChatResponse<ResultDownloadFile> generateDownloadResult(String hashCode, long id, File cacheFile) {
        ResultDownloadFile result = new ResultDownloadFile();

        result.setFile(cacheFile);

        result.setUri(Uri.fromFile(cacheFile));

        result.setHashCode(hashCode);

        result.setId(id);

        ChatResponse<ResultDownloadFile> response = new ChatResponse<>();

        response.setResult(result);

        return response;
    }


    public static Call download(ProgressHandler.IDownloadFile progressHandler,
                                String fileServer,
                                String url,
                                String fileName,
                                File destinationFolder,
                                IDownloaderError downloaderErrorInterface,
                                String hashCode,
                                long fileId) {

        Retrofit retrofit =
                ProgressResponseBody.getDownloadRetrofit(fileServer, progressHandler);


        FileApi api = retrofit.create(FileApi.class);

        Call<ResponseBody> call = api.download(url);

        final String[] downloadTempPath = new String[1];

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                new Thread(() -> {


                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    File downloadTempFile = null;


                    try {

                        if (!destinationFolder.exists()) {
                            boolean createFolder = destinationFolder.mkdirs();
                        }
                        downloadTempFile = new File(destinationFolder, fileName);
                        if (!downloadTempFile.exists()) {
                            boolean fileCreationResult = downloadTempFile.createNewFile();

                            if (!fileCreationResult) {
                                downloaderErrorInterface.errorOnWritingToFile();
                                return;
                            }
                        }

                        //keep path for cancel handling

                        downloadTempPath[0] = downloadTempFile.getPath();

                        if (response.body() != null && response.isSuccessful()) {

                            byte[] byteReader = new byte[4096];

                            inputStream = response.body().byteStream();

                            outputStream = new BufferedOutputStream(new FileOutputStream(downloadTempFile));

                            while (true) {

                                int read = inputStream.read(byteReader);

                                if (read == -1) {

                                    //download finished
                                    Log.i(TAG, "File has been downloaded");

                                    MediaType mediaType = response.body().contentType();
                                    String type = null;
                                    String subType = "";
                                    if (mediaType != null) {
                                        type = mediaType.type();
                                        subType = mediaType.subtype();
                                    }


                                    File downloadedFile = new File(destinationFolder, fileName + "." + subType);

                                    boolean savingSuccess = downloadTempFile.renameTo(downloadedFile);

                                    if (savingSuccess) {

                                        ChatResponse<ResultDownloadFile> chatResponse = generateDownloadResult(hashCode, fileId, downloadedFile);

                                        progressHandler.onFileReady(chatResponse);

//

                                    } else {

                                        downloaderErrorInterface.errorOnWritingToFile();
                                    }

                                    break;

                                }

                                outputStream.write(byteReader, 0, read);

                                outputStream.flush();

                            }


                        } else {

                            if (response.errorBody() != null) {
                                downloaderErrorInterface.errorUnknownException(response.errorBody().string());
                            } else {
                                downloaderErrorInterface.errorUnknownException(response.message());
                            }
                        }
                    } catch (Exception e) {
                        if (call.isCanceled()) {

                            handleCancelDownload(downloadTempFile);

                            return;
                        }
                        Log.e(TAG, e.getMessage());
                        downloaderErrorInterface.errorUnknownException(e.getMessage());
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.getMessage());
                            downloaderErrorInterface.errorUnknownException(e.getMessage());
                        }


                    }

                }).start();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (call.isCanceled()) {
                    handleCancelDownload(new File(downloadTempPath[0]));
                    return;
                }

                Log.d(TAG, "ERROR " + t.getMessage());
                call.cancel();
                downloaderErrorInterface.errorUnknownException(t.getMessage());
            }
        });

        return call;

    }

    private static void handleCancelDownload(File downloadTempFile) {

        Log.i(TAG, "Download Cancelled by User");

        try {
            if (downloadTempFile != null && downloadTempFile.exists()) {
                boolean deleteFile = downloadTempFile.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        }


    }


}
