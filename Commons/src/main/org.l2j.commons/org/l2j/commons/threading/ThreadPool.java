package org.l2j.commons.threading;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static java.util.Objects.isNull;

public class ThreadPool {
    private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;

    private ScheduledThreadPoolExecutor scheduledExecutor;
    private ThreadPoolExecutor executor;

    private boolean shutdown;

    private ThreadPool() {

    }

    public static void init(int threadPoolSize, int scheduledPoolSize) {
        synchronized (ThreadPool.class) {

            var instance = getInstance();
            if(isNull(instance.scheduledExecutor)) {
                instance.initThreadPools(threadPoolSize, scheduledPoolSize);
            }
        }
    }

    private void initThreadPools(int threadPoolSize, int scheduledPoolSize) {
        RejectedExecutionHandler rejectedHandler = new RejectedExecutionHandlerImpl();

        executor = new ThreadPoolExecutor(threadPoolSize, Integer.MAX_VALUE, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new PriorityThreadFactory("ThreadPoolExecutor", Thread.NORM_PRIORITY), rejectedHandler);
        scheduledExecutor = new ScheduledThreadPoolExecutor(scheduledPoolSize, new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY), rejectedHandler);
        scheduledExecutor.setRemoveOnCancelPolicy(true);

        schedulePurge();
    }

    private void schedulePurge() {
        scheduleAtFixedRate(() -> { scheduledExecutor.purge(); executor.purge();  }, 300000L, 300000L);
    }

    private static long validate(long delay)
    {
        long validatedDelay = Math.max(0, Math.min(ThreadPool.MAX_DELAY, delay));
        if (delay > validatedDelay)
            return -1;

        return validatedDelay;
    }

    public boolean isShutdown()
    {
        return shutdown;
    }

    public static ScheduledFuture<?> schedule(Runnable r, Duration delay) {
        return schedule(r, delay.toSeconds(), TimeUnit.SECONDS);
    }

    public static ScheduledFuture<?> schedule(Runnable r, long delay){
        return schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {

        delay = validate(delay);
        if(delay == -1)
            return null;

        return getInstance().scheduledExecutor.schedule(r, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
        initial = validate(initial);
        if(initial == -1)
            return null;

        delay = validate(delay);
        if(delay == -1)
            return getInstance().scheduledExecutor.schedule(r, initial, TimeUnit.MILLISECONDS);

        return getInstance().scheduledExecutor.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay) {
        return scheduleAtFixedDelay(r, initial, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay, TimeUnit unit) {
        initial = validate(initial);
        if(initial == -1)
            return null;

        delay = validate(delay);
        if(delay == -1)
            return getInstance().scheduledExecutor.schedule(r, initial, unit);

        return getInstance().scheduledExecutor.scheduleWithFixedDelay(r, initial, delay, unit);
    }

    public static void execute(Runnable r) {
        getInstance().executor.execute(r);
    }

    public void shutdown() throws InterruptedException
    {
        shutdown = true;
        try
        {
            scheduledExecutor.shutdown();
            scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }
        finally
        {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public CharSequence getStats()
    {
        StringBuilder list = new StringBuilder();

        list.append("ScheduledThreadPool\n");
        treadPoolStats(list, scheduledExecutor);
        list.append("ThreadPoolExecutor\n");
        treadPoolStats(list, executor);

        return list;
    }

    private void treadPoolStats(StringBuilder list, ThreadPoolExecutor scheduledExecutor) {
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(scheduledExecutor.getActiveCount()).append("\n");
        list.append("\tgetCorePoolSize: ..... ").append(scheduledExecutor.getCorePoolSize()).append("\n");
        list.append("\tgetPoolSize: ......... ").append(scheduledExecutor.getPoolSize()).append("\n");
        list.append("\tgetLargestPoolSize: .. ").append(scheduledExecutor.getLargestPoolSize()).append("\n");
        list.append("\tgetMaximumPoolSize: .. ").append(scheduledExecutor.getMaximumPoolSize()).append("\n");
        list.append("\tgetCompletedTaskCount: ").append(scheduledExecutor.getCompletedTaskCount()).append("\n");
        list.append("\tgetQueuedTaskCount: .. ").append(scheduledExecutor.getQueue().size()).append("\n");
        list.append("\tgetTaskCount: ........ ").append(scheduledExecutor.getTaskCount()).append("\n");
    }

    public static ThreadPool getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ThreadPool INSTANCE = new ThreadPool();
    }
}