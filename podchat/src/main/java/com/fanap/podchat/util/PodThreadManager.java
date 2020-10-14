package com.fanap.podchat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PodThreadManager {


    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    private final List<Runnable> tasks = new ArrayList<>();

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

    public synchronized PodThreadManager addNewTask(Runnable task) {
        tasks.add(task);
        return this;
    }

    public synchronized void runTasksSynced() {


        for (Runnable task :
                tasks) {
            Future<?> future = singleExecutor.submit(task);
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tasks.clear();

    }

    public synchronized void runTasksASync() {

        for (Runnable task :
                tasks) {
            executor.execute(task);
        }
        tasks.clear();


    }

    public synchronized void doThisSafe(Runnable task) {
        singleExecutor.execute(task);
    }

    public synchronized void doThisSafe(Runnable task, IComplete callback) {


        Future<?> f = singleExecutor.submit(task);

        try {
            f.get();
            callback.onComplete();
        } catch (ExecutionException e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            callback.onError(e.getMessage());
        }

    }

    public synchronized void doThisAndGo(Runnable task) {
        executor.execute(task);
    }

    public synchronized void doWithUI(Runnable actionOnUI, Runnable actionOnError, Runnable task) {


        try {
            Future<?> f = singleExecutor.submit(task);
            f.get();
            actionOnUI.run();
        } catch (ExecutionException e) {
            e.printStackTrace();
            actionOnError.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
            actionOnError.run();
        }
    }

    public interface IComplete {

        void onComplete();

        void onError(String error);

    }


}
