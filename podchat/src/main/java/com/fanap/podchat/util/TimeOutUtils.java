package com.fanap.podchat.util;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;


public class TimeOutUtils {

    public static Object setTimeout(Runnable runnable, long delay) {
        return new TimeoutEvent(runnable, delay);
    }

    public static void clearTimeout(@NonNull Object timeoutEvent) {
        if (timeoutEvent instanceof TimeoutEvent) {
            ((TimeoutEvent) timeoutEvent).cancelTimeout();
        }
    }

    private static class TimeoutEvent {
        private static final Handler handler = new Handler(Looper.getMainLooper());
        private volatile Runnable runnable;

        private TimeoutEvent(Runnable task, long delay) {
            runnable = task;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, delay);
        }

        private void cancelTimeout() {
            runnable = null;
        }
    }
}
