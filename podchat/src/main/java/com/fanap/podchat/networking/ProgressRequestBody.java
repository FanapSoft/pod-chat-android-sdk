package com.fanap.podchat.networking;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.fanap.podchat.ProgressHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {

    private File mFile;
    private String mPath;
    private String mimType;
    private UploadCallbacks mListener;
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private String uniqueId;

    public ProgressRequestBody(final File file, String mimType, String uniqueId, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        this.uniqueId = uniqueId;
        mimType = mimType;
    }

    @Nullable
    @Override
    public MediaType contentType() {
//         return MediaType.parse("image/*");
        return MediaType.parse(mimType);
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(uniqueId, uploaded, fileLength));

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        private String uniqueId;

        public ProgressUpdater(String uniqueId, long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
            this.uniqueId = uniqueId;
        }

        @Override
        public void run() {

            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
            mListener.onProgress(uniqueId, (int) (100 * mUploaded / mTotal), (int) mUploaded, (int) (mTotal - mUploaded));
        }
    }

    public interface UploadCallbacks {
        @Deprecated
        default void onProgressUpdate(int percentage) {
        }

        default void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onError() {
        }

        default void onFinish() {
        }
    }

}
