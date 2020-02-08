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
import com.fanap.podchat.util.FileUtils;

import java.io.File;

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
            File destFolder,
            String url,
            String fileName,
            String hashCode,
            long id,
            Context context,
            IDownloaderError iDownloader,
            long bytesAvailable) {

        File downloadTempFile = new File(destFolder, fileName);

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

                        File downloadedFile = new File(destFolder, fileName + "." + extension);

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

}
