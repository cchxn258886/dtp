package com.example.dtp.support;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @Author chenl
 * @Date 2023/4/23 5:11 下午
 */
public interface ExecutorAdapter<E extends Executor> extends Executor {
    /**
     * get the original executor
     * return the original executor
     */
    E getOriginal();

    @Override
    default void execute(Runnable runnable) {
        getOriginal().execute(runnable);
    }

    default void execute(Runnable runnable, long startTimeout) {
        getOriginal().execute(runnable);
    }

    /**
     * get the core pool size
     * return the core pool size
     */
    int getCorePoolSize();

    /**
     * 见名知意
     */
    void setCorePoolSize(int corePoolSize);

    /**
     *
     */
    int getMaximumPoolSize();

    void setMaximumPoolSize(int maximumPoolSize);

    /**
     *
     */
    int getPoolSize();

    /**
     * 获取活动的线程数
     */
    int getActiveCount();


    default int getLargestPoolSize() {
        return -1;
    }

    default long getTaskCount() {
        return -1;
    }

    default long getCompletedTaskCount() {
        return -1;
    }

    ;

    default BlockingQueue<Runnable> getQueue() {
        return new UnsupportedBlockingQueue();
    }

    /**
     * get Type name
     */
    default String getQueueType() {
        return getQueue().getClass().getSimpleName();
    }

    default int getQueueSize() {
        return getQueue().size();
    }

    /**
     * 获取队列剩余容量
     */
    default int getQueueRemainingCapacity() {
        return getQueue().remainingCapacity();
    }

    /**
     * 获取队列容量
     */
    default int getQueueCapacity() {
        int capacity = getQueueSize() + getQueueRemainingCapacity();
        return capacity < 0 ? Integer.MAX_VALUE : capacity;
    }

    /**
     * 正在刷新队列容量
     */
    default void onRefreshQueueCapacity(int capacity) {
        //do no thing
    }

    //需要子类去实现 改成接口会比较
    default RejectedExecutionHandler getRejectedExecutionHandler() {
        //unsupport
        return null;
    }

    //需要子类去实现 改成接口会比较
    default void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        //unsupport
    }

    default String getRejectHandlerType() {
        return Optional.ofNullable(getRejectedExecutionHandler()).
                map((item) -> item.getClass().getSimpleName())
                .orElse("unknown");
    }

    /**
     * 获取被拒绝的任务的计数
     */
    default long getRejectTaskCount() {
        return -1;
    }

    default boolean allowsCoreThreadTimeOut() {
        return false;
    }

    default void allowsCoreThreadTimeOut(boolean value) {

    }

    default void setAllowCoreThreadTimeOut(boolean value) {

    }

    default long getKeepAliveTime(TimeUnit unit) {
        return -1;
    }

    default void setKeepAliveTime(long time, TimeUnit unit) {

    }
}

class UnsupportedBlockingQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {

    @Override
    public Iterator<Runnable> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void put(Runnable runnable) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable take() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super Runnable> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable peek() {
        throw new UnsupportedOperationException();
    }
}