package com.example.dtp.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/4/26 10:01 上午
 */
public class ThreadPoolExecutorAdapter implements ExecutorAdapter<ThreadPoolExecutor> {
    private final ThreadPoolExecutor executor;

    public ThreadPoolExecutorAdapter(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }

    @Override
    public ThreadPoolExecutor getOriginal() {
        return this.executor;
    }

    @Override
    public int getCorePoolSize() {
        return this.executor.getCorePoolSize();
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        this.executor.setCorePoolSize(corePoolSize);
    }

    @Override
    public int getMaximumPoolSize() {
        return this.executor.getMaximumPoolSize();
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.executor.setMaximumPoolSize(maximumPoolSize);
    }

    @Override
    public int getPoolSize() {
        return this.executor.getPoolSize();
    }

    @Override
    public int getActiveCount() {
        return this.executor.getActiveCount();
    }

    @Override
    public int getLargestPoolSize() {
        return this.executor.getLargestPoolSize();
    }

    @Override
    public long getTaskCount() {
        return this.executor.getTaskCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return this.executor.getCompletedTaskCount();
    }

    @Override
    public BlockingQueue<Runnable> getQueue() {
        return this.executor.getQueue();
    }

    @Override
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.executor.getRejectedExecutionHandler();
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        this.executor.setRejectedExecutionHandler(handler);
    }

    @Override
    public boolean allowsCoreThreadTimeOut() {
        return this.executor.allowsCoreThreadTimeOut();
    }

    @Override
    public void allowsCoreThreadTimeOut(boolean value) {
        this.executor.allowCoreThreadTimeOut(value);
    }

    @Override
    public long getKeepAliveTime(TimeUnit unit) {
        return this.executor.getKeepAliveTime(unit);
    }

    @Override
    public void setKeepAliveTime(long time, TimeUnit unit) {
        this.executor.setKeepAliveTime(time, unit);
    }
}
