package com.fanap.podchat.util;

import java.util.ArrayList;
import java.util.List;

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


}
