package com.example.dtp.reject;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.common.constant.DynamicThreadPoolConstant;
import com.example.dtp.support.task.runnable.DynamicRunnable;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author chenl
 * @Date 2023/4/24 4:20 下午
 */
public interface RejectedAware {
    /**
     * 代理模式 在reject之前包装执行一些东西
     */
    default void beforeReject(Runnable runnable, ThreadPoolExecutor threadPoolExecutor, Logger log) {
        if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
            DynamicRunnable dtpRunnable = (DynamicRunnable) runnable;
            DynamicThreadPoolExecutor executor = (DynamicThreadPoolExecutor) threadPoolExecutor;
            executor.incRejectCount(1);
            //源代码有个notify
            log.warn("DynamicTp execute, thread pool is exhausted, tpName: {}, taskName: {}, traceId: {}, " +
                            "poolSize: {} (active: {}, core: {}, max: {}, largest: {}), " +
                            "task: {} (completed: {}), queueCapacity: {}, (currSize: {}, remaining: {}), " +
                            "executorStatus: (isShutdown: {}, isTerminated: {}, isTerminating: {})",
                    executor.getThreadPoolName(), dtpRunnable.getTaskName(), MDC.get(DynamicThreadPoolConstant.TRACE_ID), executor.getPoolSize(),
                    executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(),
                    executor.getLargestPoolSize(), executor.getTaskCount(), executor.getCompletedTaskCount(),
                    executor.getQueueCapacity(), executor.getQueue().size(), executor.getQueue().remainingCapacity(),
                    executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
        }
    }

}
