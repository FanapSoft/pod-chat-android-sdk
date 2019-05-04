package com.fanap.podchat.networking;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

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
    private Handler handler;
    private int numWriteToCalls;
    private int ignoreFirstNumberOfWriteToCalls = 2;

    public ProgressRequestBody(final File file, String mimType, String uniqueId, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        this.uniqueId = uniqueId;
        this.mimType = mimType;
        handler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public MediaType contentType() {
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

        numWriteToCalls++;

        try {
            int read;
            float lastProgressPercentUpdate = 0.0f;
            while ((read = in.read(buffer)) != -1) {

                uploaded += read;
                sink.write(buffer, 0, read);


                // when using HttpLoggingInterceptor it calls writeTo and passes data into a local buffer just for logging purposes.
                // the second call to write to is the progress we actually want to track
                if (numWriteToCalls > ignoreFirstNumberOfWriteToCalls) {
                    float progress = ((float) uploaded / (float) fileLength) * 100f;
                    //prevent publishing too many updates, which slows upload, by checking if the upload has progressed by at least 1 percent
                    if (progress - lastProgressPercentUpdate > 1 || progress == 100f) {
                        // publish progress
                        // update progress on UI thread
                        handler.post(new ProgressUpdater(uniqueId, uploaded, fileLength));
                        lastProgressPercentUpdate = progress;
                    }
                }
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

            mListener.onProgress(uniqueId, (int) (100 * mUploaded / mTotal), (int) mUploaded, (int) (mTotal - mUploaded));
        }
    }

    public interface UploadCallbacks {

        default void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onError() {
        }

        default void onFinish() {
        }
    }

}
