package com.example.dtp.support;


import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.NamedThreadFactory;
import com.example.dtp.common.constant.DynamicThreadPoolConstant;
import com.example.dtp.common.queue.VariableLinkedBlockingQueue;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.enums.QueueTypeEnum;
import com.example.dtp.reject.RejectHandlerGetter;
import lombok.Builder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @Author chenl
 * @Date 2023/5/4 4:40 下午
 * builder
 */
public class ThreadPoolBuilder {
    private String threadPoolName = "dynamicTp";
    private int corePoolSize = 1;
    private int maximumPoolSize = DynamicThreadPoolConstant.AVAILABLE_PROCESSORS;
    private long keepAliveTime = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue = new VariableLinkedBlockingQueue<>(this.queueCapacity);
    private int queueCapacity = 1024;
    private int maxFreeMemory = 256;
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    private ThreadFactory threadFactory = new NamedThreadFactory("dynamicTp");
    private boolean allowCoreThreadTimeOut = false;
    private boolean dynamic = true;
    private boolean waitForTasksToCompleteOnShutdown = false;
    private int awaitTerminationSeconds = 0;
    private boolean ioIntensive = false;
    private boolean ordered = false;
    private boolean scheduled = false;
    private boolean preStartAllCoreThreads = false;
    private boolean rejectEnhanced = true;


    private long runTimeout = 0;


    private long queueTimeout = 0;


    private final List<TaskWrapper> taskWrappers = new ArrayList<>();

    private List<NotifyItem> notifyItems = NotifyItem.getAllNotifyItems();

    private List<String> platformIds = new ArrayList<>();

    /**
     * If enable notify.
     */
    private boolean notifyEnabled = true;

    public static ThreadPoolBuilder newBuilder() {
        return new ThreadPoolBuilder();
    }


    private ThreadPoolBuilder() {
    }

    ;

    public static ThreadPoolBuilder newThreadPoolBuilder() {
        return new ThreadPoolBuilder();
    }

    public ThreadPoolBuilder threadPoolName(String name) {
        this.threadPoolName = threadPoolName;
        return this;
    }

    public ThreadPoolBuilder corePoolSize(Integer corePoolSize) {
        if (corePoolSize <= 0) {
            corePoolSize = 1;
        }
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0) {
            maximumPoolSize = 1;
        }
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        if (keepAliveTime > 0) {
            this.keepAliveTime = keepAliveTime;
        }
        return this;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        if (timeUnit != null) {
            this.timeUnit = timeUnit;
        }
        return this;
    }

    public ThreadPoolBuilder workQueue(String queueName, Integer capacity, Boolean fair, Integer maxFreeMemory) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    fair != null && fair, maxFreeMemory != null ? maxFreeMemory : this.maxFreeMemory);
        }
        return this;
    }

    public ThreadPoolBuilder workQueue(String queueName, Integer capacity, Boolean fair) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    fair != null && fair, maxFreeMemory);
        }
        return this;
    }

    public ThreadPoolBuilder workQueue(String queueName, Integer capacity) {
        if (StringUtils.isNotBlank(queueName)) {
            workQueue = QueueTypeEnum.buildLbq(queueName, capacity != null ? capacity : this.queueCapacity,
                    false, maxFreeMemory);
        }
        return this;
    }

    public ThreadPoolBuilder queueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public ThreadPoolBuilder maxFreeMemory(int maxFreeMemory) {
        this.maxFreeMemory = maxFreeMemory;
        return this;
    }

    public ThreadPoolBuilder rejectedExecutionHandler(String rejectedName) {
        if (StringUtils.isNotBlank(rejectedName)) {
            rejectedExecutionHandler = RejectHandlerGetter.buildRejectedHandler(rejectedName);
        }
        return this;
    }

    public ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (Objects.nonNull(rejectedExecutionHandler)) {
            rejectedExecutionHandler = handler;
        }
        return this;
    }

    public ThreadPoolBuilder threadFactory(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            threadFactory = new NamedThreadFactory(prefix);
        }
        return this;
    }

    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    public ThreadPoolBuilder dynamic(boolean dynamic) {
        this.dynamic = dynamic;
        return this;
    }

    public ThreadPoolBuilder awaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
        return this;
    }

    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }

    public ThreadPoolBuilder ioIntensive(boolean ioIntensive) {
        this.ioIntensive = ioIntensive;
        return this;
    }

    public ThreadPoolBuilder ordered(boolean ordered) {
        this.ordered = ordered;
        return this;
    }

    public ThreadPoolBuilder scheduled(boolean scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    public ThreadPoolBuilder preStartAllCoreThreads(boolean preStartAllCoreThreads) {
        this.preStartAllCoreThreads = preStartAllCoreThreads;
        return this;
    }

    public ThreadPoolBuilder rejectEnhanced(boolean rejectEnhanced) {
        this.rejectEnhanced = rejectEnhanced;
        return this;
    }

    public ThreadPoolBuilder runTimeout(long runTimeout) {
        this.runTimeout = runTimeout;
        return this;
    }

    public ThreadPoolBuilder queueTimeout(long queueTimeout) {
        this.queueTimeout = queueTimeout;
        return this;
    }

    public ThreadPoolBuilder taskWrappers(List<TaskWrapper> taskWrappers) {
        this.taskWrappers.addAll(taskWrappers);
        return this;
    }

    public ThreadPoolBuilder taskWrapper(TaskWrapper taskWrapper) {
        this.taskWrappers.add(taskWrapper);
        return this;
    }

    public ThreadPoolBuilder notifyItems(List<NotifyItem> notifyItemList) {
        if (CollectionUtils.isNotEmpty(notifyItemList)) {
            notifyItems = notifyItemList;
        }
        return this;
    }

    public ThreadPoolBuilder platformIds(List<String> platformIds) {
        if (CollectionUtils.isNotEmpty(platformIds)) {
            this.platformIds = platformIds;
        }
        return this;
    }

    public ThreadPoolBuilder notifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
        return this;
    }

    public ThreadPoolExecutor build() {
        if (dynamic) {
            return buildDtpExecutor(this);
        } else {
            return buildCommonExecutor(this);
        }
    }

    public DynamicThreadPoolExecutor buildDynamic() {
        return buildDtpExecutor(this);
    }

    public ThreadPoolExecutor buildCommon() {
        return buildCommonExecutor(this);
    }

    public ExecutorService buildWithTtl() {
        if (dynamic) {
            taskWrappers.add(TtlRunnable::get);
            return buildDtpExecutor(this);
        } else {
            return TtlExecutors.getTtlExecutorService(buildCommonExecutor(this));
        }
    }

    private DynamicThreadPoolExecutor buildDtpExecutor(ThreadPoolBuilder builder) {
        Assert.notNull(builder.threadPoolName, "The thread pool name must not be null.");
        DynamicThreadPoolExecutor dtpExecutor = createInternal(builder);
        dtpExecutor.setThreadPoolName(builder.threadPoolName);
        dtpExecutor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
        dtpExecutor.setWaitForTasksToCompleteOnShutdown(builder.waitForTasksToCompleteOnShutdown);
        dtpExecutor.setAwaitTerminationSeconds(builder.awaitTerminationSeconds);
        dtpExecutor.setPreStartAllCoreThreads(builder.preStartAllCoreThreads);
        dtpExecutor.setRejectEnhanced(builder.rejectEnhanced);
        dtpExecutor.setRunTimeout(builder.runTimeout);
        dtpExecutor.setQueueTimeout(builder.queueTimeout);
        dtpExecutor.setTaskWrappers(builder.taskWrappers);
        dtpExecutor.setNotifyItems(builder.notifyItems);
        dtpExecutor.setPlatFormIds(builder.platformIds);
        dtpExecutor.setNotifyEnabled(builder.notifyEnabled);
        dtpExecutor.setRejectHandler(builder.rejectedExecutionHandler);
        return dtpExecutor;
    }

    private DynamicThreadPoolExecutor createInternal(ThreadPoolBuilder builder) {
        DynamicThreadPoolExecutor dtpExecutor;
//        if (ioIntensive) {
//            TaskQueue taskQueue = new TaskQueue(builder.queueCapacity);
//            dtpExecutor = new EagerDtpExecutor(
//                    builder.corePoolSize,
//                    builder.maximumPoolSize,
//                    builder.keepAliveTime,
//                    builder.timeUnit,
//                    taskQueue,
//                    builder.threadFactory,
//                    builder.rejectedExecutionHandler);
//            taskQueue.setExecutor((EagerDtpExecutor) dtpExecutor);
//        } else if (ordered) {
//            dtpExecutor = new OrderedDtpExecutor(
//                    builder.corePoolSize,
//                    builder.maximumPoolSize,
//                    builder.keepAliveTime,
//                    builder.timeUnit,
//                    builder.workQueue,
//                    builder.threadFactory,
//                    builder.rejectedExecutionHandler);
//        } else if (scheduled) {
//            dtpExecutor = new ScheduledDtpExecutor(
//                    builder.corePoolSize,
//                    builder.maximumPoolSize,
//                    builder.keepAliveTime,
//                    builder.timeUnit,
//                    builder.workQueue,
//                    builder.threadFactory,
//                    builder.rejectedExecutionHandler);
//        } else {
        dtpExecutor = new DynamicThreadPoolExecutor(
                builder.corePoolSize,
                builder.maximumPoolSize,
                builder.keepAliveTime,
                builder.timeUnit,
                builder.workQueue,
                builder.threadFactory,
                builder.rejectedExecutionHandler);
        //}
        return dtpExecutor;
    }

    private ThreadPoolExecutor buildCommonExecutor(ThreadPoolBuilder builder) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                builder.corePoolSize,
                builder.maximumPoolSize,
                builder.keepAliveTime,
                builder.timeUnit,
                builder.workQueue,
                builder.threadFactory,
                builder.rejectedExecutionHandler
        );
        executor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
        return executor;
    }
}