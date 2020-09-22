package com.fanap.podchat.chat.thread;

import android.os.Build;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.util.Util;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.sentry.core.Sentry;
import rx.Observable;

public class ThreadManager {


    public interface ILastMessageChanged {

        void onThreadExistInCache(Thread thread);

        void threadNotFoundInCache();

    }

    public interface IThreadInfoCompleter {

        void onThreadInfoReceived(ChatMessage chatMessage);

    }


    public static Observable<List<Thread>> filterIsNew(boolean isNew, List<Thread> allThreads) {

        if (isNew)
            return Observable.from(allThreads)
                    .filter(t -> t.getUnreadCount() > 0)
                    .toList();

        return Observable.from(allThreads).toList();

    }

    public static Observable<List<Thread>> getByIds(List<Integer> ids, List<Thread> allThreads) {

        try {
            if (Util.isNotNullOrEmpty(ids))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains(Math.toIntExact(t.getId())))
                            .toList();
                } else {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains((int) t.getId()))
                            .toList();
                }
        } catch (Exception e) {
            Sentry.captureException(e);
            return Observable.from(allThreads).toList();
        }

        return Observable.from(allThreads).toList();

    }

    public static Observable<List<Thread>> getByName(String name, List<Thread> allThreads) {

        try {
            if (Util.isNotNullOrEmpty(name)) {
                return Observable.from(allThreads)
                        .filter(t -> t.getTitle().contains(name))
                        .toList();
            } else {
                return Observable.from(allThreads).toList();
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            return Observable.from(allThreads).toList();
        }

    }


    public static int compareThreads(Thread thread1, Thread thread2) {

        if (thread1.isPin() && thread2.isPin())
            return Long.compare(thread1.getTime(), thread2.getTime());

        return Boolean.compare(thread2.isPin(), thread1.isPin());
    }

    public static List<Thread> sortThreads(List<Thread> unsorted) {
        List<Thread> sorted = new ArrayList<>(unsorted);
        Collections.sort(sorted, ThreadManager::compareThreads);
        return sorted;
    }


    public static class ThreadResponse {
        private List<Thread> threadList;
        private long contentCount;
        private String source;

        public ThreadResponse(List<Thread> threadList, long contentCount, String source) {
            this.threadList = threadList;
            this.contentCount = contentCount;
            this.source = source;
        }

        public List<Thread> getThreadList() {
            return threadList;
        }

        public long getContentCount() {
            return contentCount;
        }
    }

}
