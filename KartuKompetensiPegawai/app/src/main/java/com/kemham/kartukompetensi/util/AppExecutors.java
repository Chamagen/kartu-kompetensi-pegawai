package com.kemham.kartukompetensi.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Global executor pools for the whole application.
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;
    private static volatile AppExecutors instance;

    private final ExecutorService diskIO;
    private final ExecutorService networkIO;
    private final Executor mainThread;
    private final ScheduledExecutorService scheduledExecutor;

    private AppExecutors(ExecutorService diskIO, ExecutorService networkIO, 
                        Executor mainThread, ScheduledExecutorService scheduledExecutor) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
        this.scheduledExecutor = scheduledExecutor;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    instance = new AppExecutors(
                        Executors.newSingleThreadExecutor(), // diskIO
                        Executors.newFixedThreadPool(THREAD_COUNT), // networkIO
                        new MainThreadExecutor(), // mainThread
                        Executors.newSingleThreadScheduledExecutor() // scheduledExecutor
                    );
                }
            }
        }
        return instance;
    }

    /**
     * Executor for disk IO operations.
     */
    public ExecutorService diskIO() {
        return diskIO;
    }

    /**
     * Executor for network operations.
     */
    public ExecutorService networkIO() {
        return networkIO;
    }

    /**
     * Executor for main thread operations.
     */
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * Executor for scheduled operations.
     */
    public ScheduledExecutorService scheduled() {
        return scheduledExecutor;
    }

    /**
     * Executes the given task on the disk IO thread.
     */
    public void executeDiskIO(Runnable runnable) {
        diskIO.execute(runnable);
    }

    /**
     * Executes the given task on the network IO thread.
     */
    public void executeNetworkIO(Runnable runnable) {
        networkIO.execute(runnable);
    }

    /**
     * Executes the given task on the main thread.
     */
    public void executeMainThread(Runnable runnable) {
        mainThread.execute(runnable);
    }

    /**
     * Schedules the given task to run after the specified delay.
     */
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        scheduledExecutor.schedule(runnable, delay, unit);
    }

    /**
     * Schedules the given task to run periodically.
     */
    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, 
                                  long period, TimeUnit unit) {
        scheduledExecutor.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * Executes tasks sequentially, ensuring order of execution.
     */
    public void executeSequentially(Runnable... tasks) {
        for (Runnable task : tasks) {
            diskIO.execute(task);
        }
    }

    /**
     * Executes a task after all given tasks complete.
     */
    public void executeAfterAll(Runnable afterTask, Runnable... tasks) {
        Runnable wrappedAfterTask = () -> {
            try {
                for (Runnable task : tasks) {
                    task.run();
                }
                mainThread.execute(afterTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        diskIO.execute(wrappedAfterTask);
    }

    /**
     * Shuts down all executor services.
     */
    public void shutdown() {
        diskIO.shutdown();
        networkIO.shutdown();
        scheduledExecutor.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks.
     */
    public void shutdownNow() {
        diskIO.shutdownNow();
        networkIO.shutdownNow();
        scheduledExecutor.shutdownNow();
    }

    /**
     * Main thread executor that wraps Handler.
     */
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    /**
     * Posts a task to the main thread with a delay.
     */
    public void postToMainThreadDelayed(Runnable runnable, long delayMillis) {
        MainThreadExecutor executor = (MainThreadExecutor) mainThread;
        executor.mainThreadHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * Removes pending posts of the given runnable from the main thread queue.
     */
    public void removeMainThreadCallbacks(Runnable runnable) {
        MainThreadExecutor executor = (MainThreadExecutor) mainThread;
        executor.mainThreadHandler.removeCallbacks(runnable);
    }
}
