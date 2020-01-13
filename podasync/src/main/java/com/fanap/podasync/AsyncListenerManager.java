package com.fanap.podasync;

import java.util.ArrayList;
import java.util.List;

class AsyncListenerManager {
    private final List<AsyncListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<AsyncListener> mCopiedListeners;


    public AsyncListenerManager() {
    }

    public void addListener(AsyncListener listener, boolean log) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(List<AsyncListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (AsyncListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                mListeners.add(listener);
                mSyncNeeded = true;
            }
        }
    }

    public void removeListener(AsyncListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }


    public void removeListeners(List<AsyncListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (AsyncListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                if (mListeners.remove(listener)) {
                    mSyncNeeded = true;
                }
            }
        }
    }


    public void clearListeners() {
        synchronized (mListeners) {
            if (mListeners.size() == 0) {
                return;
            }

            mListeners.clear();
            mSyncNeeded = true;
        }
    }

    public List<AsyncListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<AsyncListener> copiedListeners = new ArrayList<>(mListeners.size());

            for (AsyncListener listener : mListeners) {
                copiedListeners.add(listener);
            }

            // Synchronize.
            mCopiedListeners = copiedListeners;
            mSyncNeeded = false;

            return copiedListeners;
        }
    }


    public void callOnTextMessage(String message) {
        for (AsyncListener listener : getSynchronizedListeners()) {
            try {
                listener.onReceivedMessage(message);

            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
//                Logger.e(t, t.getMessage());
            }
        }
    }

    private void callHandleCallbackError(AsyncListener listener, Throwable cause) {
        try {
            listener.handleCallbackError(cause);
        } catch (Throwable t) {
        }
    }

    public void callOnStateChanged(String message) {
        for (AsyncListener listener : getSynchronizedListeners()) {
            try {
                listener.onStateChanged(message);
            } catch (Throwable throwable) {
                callHandleCallbackError(listener, throwable);
//                Logger.e(throwable, throwable.getMessage());
            }
        }
    }

    public void callOnDisconnected(String message) {
        for (AsyncListener listener : getSynchronizedListeners()) {
            try {
                listener.onDisconnected(message);
            } catch (Throwable throwable) {
                callHandleCallbackError(listener, throwable);
//                Logger.e(throwable, throwable.getMessage());
            }
        }
    }

    public void callOnError(String message) {
        for (AsyncListener listener : getSynchronizedListeners()) {
            try {
                listener.onError(message);
            } catch (Throwable throwable) {
                callHandleCallbackError(listener, throwable);
//                Logger.e(throwable, throwable.getMessage());
            }
        }
    }
}
