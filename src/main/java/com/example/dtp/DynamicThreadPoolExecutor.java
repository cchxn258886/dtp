package com.example.dtp;



import com.example.dtp.common.constant.DynamicThreadPoolConstant;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.reject.RejectHandlerGetter;
import com.example.dtp.support.ExecutorAdapter;
import com.example.dtp.support.TaskWrapper;
import com.example.dtp.support.task.runnable.DynamicRunnable;
import com.example.dtp.support.task.runnable.NamedRunnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author chenl
 * @Date 2023/4/23 2:06 下午
 * 动态线程池
 * LongAdder 比AtomicLong 性能更好。
 * 通过类似1.8之前的concurrentHashMap
 * segmentLock improve performance
 */
@Slf4j
public class DynamicThreadPoolExecutor extends ThreadPoolExecutor implements ExecutorAdapter<ThreadPoolExecutor> {
    /**
     * 线程池名
     */
    private String threadPoolName;
    /**
     * 别名
     */
    private String threadPoolAliasName;

    private boolean notifyEnabled = true;

    /**
     * notify items see
     */
    private List<NotifyItem> notifyItems;

    /**
     * notify platforms ids
     */
    private List<String> platFormIds;
    /**
     * task wrappers do something enhance
     */
    private List<TaskWrapper> taskWrappers = new ArrayList<TaskWrapper>();

    /**
     * if pre start all core threads
     */
    private boolean preStartAllCoreThreads;

    /**
     * rejectHandler type
     */
    private String rejectHandlerType;

    /**
     * if enhance reject
     */
    private boolean rejectEnhanced = true;

    /**
     * task execute timeout unit just for static
     */
    private long runTimeout;

    /**
     * task queue wait timeout
     */
    private long queueTimeout;

    /**
     * total reject count
     */
    private final LongAdder rejectCount = new LongAdder();

    /**
     * count run timeout tasks
     */
    private final LongAdder runTimeoutCount = new LongAdder();
    /**
     * count queue wait timeout
     */
    private final LongAdder queueTimeoutCount = new LongAdder();


    /**
     * 类似jvm hook 线程等待任务完成在关闭的时候
     */
    protected boolean waitForTasksToCompleteOnShutdown = false;

    /**
     * 等待threadpool shutdown的时间 用来关闭还在running的thread
     */
    protected int awaitTerminationSeconds = 0;

    public DynamicThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit timeUnit,
                                     BlockingQueue<Runnable> blockingQueue,
                                     ThreadFactory threadFactory,
                                     RejectedExecutionHandler rejectedExecutionHandler
    ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, blockingQueue, threadFactory, rejectedExecutionHandler);
    }

    public DynamicThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit timeUnit,
                                     BlockingQueue<Runnable> blockingQueue,
                                     ThreadFactory threadFactory
    ) {
        this(corePoolSize, maximumPoolSize, keepAliveTime,
                timeUnit, blockingQueue, threadFactory, new AbortPolicy());
    }

    public DynamicThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit timeUnit,
                                     BlockingQueue<Runnable> blockingQueue
    ) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                blockingQueue, Executors.defaultThreadFactory(), new AbortPolicy());
    }

    @Override
    public ThreadPoolExecutor getOriginal() {
        return this;
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override
    public void execute(Runnable task) {
        DynamicRunnable dynamicRunnable = (DynamicRunnable) wrapTasks(task);
        dynamicRunnable.startQueueTimeoutTask(this);
        super.execute(dynamicRunnable);
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        DynamicRunnable runnable = (DynamicRunnable) r;
        runnable.cancelQueueTimeoutTask();
        runnable.startRunTimeoutTask(this, t);
    }

    protected Runnable wrapTasks(Runnable command) {
        if (CollectionUtils.isNotEmpty(taskWrappers)) {
            for (TaskWrapper taskWrapper : taskWrappers) {
                command = taskWrapper.wrapper(command);
            }
        }
        String taskName = (command instanceof NamedRunnable) ? ((NamedRunnable) command).getName() : null;
        command = new DynamicRunnable(command, taskName);
        return command;
    }

    private void clearContext() {
        MDC.remove(DynamicThreadPoolConstant.TRACE_ID);
    }

    private void tryPrintError(Runnable r, Throwable t) {
        if (Objects.nonNull(t)) {
            log.error("thread :{} throw exception {}", Thread.currentThread(), t.getMessage(), t);
            return;
        }
        if (r instanceof FutureTask) {
            try {
                Future<?> future = (Future<?>) r;
                future.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("thread {} throw exception {}", Thread.currentThread(), e.getMessage(), e);
            }
        }
    }

    public void setRejectHandler(RejectedExecutionHandler handler) {
        this.rejectHandlerType = handler.getClass().getSimpleName();
        if (!isRejectEnhanced()) {
            setRejectedExecutionHandler(handler);
            return;
        }
        setRejectedExecutionHandler(RejectHandlerGetter.getProxy(handler));

    }

    public String getThreadPoolName() {
        return this.threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public String getThreadPoolAliasName() {
        return threadPoolAliasName;
    }

    public void setThreadPoolAliasName(String threadPoolAliasName) {
        this.threadPoolAliasName = threadPoolAliasName;
    }

    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }

    public void setNotifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }

    public List<NotifyItem> getNotifyItems() {
        return notifyItems;
    }

    public void setNotifyItems(List<NotifyItem> notifyItems) {
        this.notifyItems = notifyItems;
    }

    public List<String> getPlatFormIds() {
        return platFormIds;
    }

    public void setPlatFormIds(List<String> platFormIds) {
        this.platFormIds = platFormIds;
    }

    public List<TaskWrapper> getTaskWrappers() {
        return taskWrappers;
    }

    public void setTaskWrappers(List<TaskWrapper> taskWrappers) {
        this.taskWrappers = taskWrappers;
    }

    public boolean isPreStartAllCoreThreads() {
        return preStartAllCoreThreads;
    }

    public void setPreStartAllCoreThreads(boolean preStartAllCoreThreads) {
        this.preStartAllCoreThreads = preStartAllCoreThreads;
    }

    @Override
    public String getRejectHandlerType() {
        return rejectHandlerType;
    }

    public void setRejectHandlerType(String rejectHandlerType) {
        this.rejectHandlerType = rejectHandlerType;
    }

    public boolean isRejectEnhanced() {
        return rejectEnhanced;
    }

    public void setRejectEnhanced(boolean rejectEnhanced) {
        this.rejectEnhanced = rejectEnhanced;
    }

    public long getRunTimeout() {
        return runTimeout;
    }

    public void setRunTimeout(long runTimeout) {
        this.runTimeout = runTimeout;
    }

    public long getQueueTimeout() {
        return queueTimeout;
    }

    public void setQueueTimeout(long queueTimeout) {
        this.queueTimeout = queueTimeout;
    }

    public void incRejectCount(int count) {
        rejectCount.add(count);
    }

    public LongAdder getRejectCount() {
        return rejectCount;
    }

    public void incRunTimeoutCount(int count) {
        runTimeoutCount.add(count);
    }

    public LongAdder getRunTimeoutCount() {
        return runTimeoutCount;
    }

    public void incQueueTimeoutCount(int count) {
        queueTimeoutCount.add(count);
    }

    public LongAdder getQueueTimeoutCount() {
        return queueTimeoutCount;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }


    /**
     * In order for the field can be assigned by reflection.
     *
     * @param allowCoreThreadTimeOut allowCoreThreadTimeOut
     */
    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        allowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }

}
