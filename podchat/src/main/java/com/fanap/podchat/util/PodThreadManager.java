package com.fanap.podchat.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.fanap.podchat.chat.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PodThreadManager {

    private final List<Runnable> tasks = new ArrayList<>();

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

    public synchronized PodThreadManager addNewTask(Runnable task) {
        tasks.add(task);
        return this;
    }

    public synchronized void runTasksSynced() {

        Thread motherThread = new Thread(() -> {

            synchronized (tasks) {

                for (Runnable task :
                        tasks) {

                    try {
                        Thread worker = new Thread(task);
                        worker.setName("worker-thread");
                        worker.start();
                        worker.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                tasks.clear();
            }

        });

        motherThread.setName("mother-thread");
        motherThread.start();

    }

    public synchronized void runTasksASync() {

        Thread motherThread = new Thread(() -> {

            synchronized (tasks) {

                for (Runnable task :
                        tasks) {

                    try {
                        Thread worker = new Thread(task);
                        worker.setName("worker-thread");
                        worker.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                tasks.clear();

            }

        });

        motherThread.setName("mother-thread");
        motherThread.start();


    }

    public synchronized void doThisSafe(Runnable task) {

        try {
            Thread oneTaskThread = new Thread(task);
            oneTaskThread.setName("Pod-One-Task-Thread");
            oneTaskThread.start();
            oneTaskThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void doThisSafe(Runnable task, IComplete callback) {


        Thread backgroundThread = new Thread(() -> {

            try {
                Thread oneTaskThread = new Thread(task);
                oneTaskThread.setName("Pod-Block-Thread");
                oneTaskThread.start();
                oneTaskThread.join();
                callback.onComplete();
            } catch (InterruptedException e) {
                callback.onError(e.getMessage());
            }


        });
        backgroundThread.setName("Pod-Background-Thread");
        backgroundThread.start();


    }

    public synchronized void doThisAndGo(Runnable task) {


        Thread backgroundThread = new Thread(() -> {

            try {
                Thread oneTaskThread = new Thread(task);
                oneTaskThread.setName("Pod-Block-Thread");
                oneTaskThread.start();
                oneTaskThread.join();
            } catch (InterruptedException e) {
                Log.e(Chat.TAG, Objects.requireNonNull(e.getMessage()));
            }


        });
        backgroundThread.setName("Pod-Background-Thread");
        backgroundThread.start();


    }

    public synchronized void doWithUI(Runnable task, Runnable actionOnUI, Runnable actionOnError) {


        Thread backgroundThread = new Thread(() -> {

            try {
                Thread oneTaskThread = new Thread(task);
                oneTaskThread.setName("Pod-Block-Thread");
                oneTaskThread.start();
                oneTaskThread.join();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(actionOnUI);
            } catch (InterruptedException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(actionOnError);
            }


        });
        backgroundThread.setName("Pod-Background-Thread");
        backgroundThread.start();


    }

    public interface IComplete {

        void onComplete();

        void onError(String error);

    }


}
