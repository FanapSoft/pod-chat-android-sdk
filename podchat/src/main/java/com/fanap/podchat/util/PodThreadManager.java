package com.fanap.podchat.util;

import android.os.AsyncTask;
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

    public synchronized void doWithUI(Runnable actionOnUI, Runnable actionOnError, Runnable... task) {


        new PodAsyncTask(actionOnUI, actionOnError).execute(task);


    }


    static class PodAsyncTask extends AsyncTask<Runnable, Void, Boolean> {

        Runnable taskOnUI;
        Runnable taskOnError;

        PodAsyncTask(Runnable taskOnUI, Runnable taskOnError) {
            this.taskOnUI = taskOnUI;
            this.taskOnError = taskOnError;
        }

        @Override
        protected Boolean doInBackground(Runnable... runnables) {

            try {
                for (Runnable task :
                        runnables) {
                    task.run();
                }
                return true;
            } catch (Exception exc) {
                exc.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {
                taskOnUI.run();
            } else taskOnError.run();

        }


    }


    public interface IComplete {

        void onComplete();

        void onError(String error);

    }


}
