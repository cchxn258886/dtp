package com.example.dtp.convert;



import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.entity.ThreadPoolStats;
import com.example.dtp.entity.TpMainFields;
import com.example.dtp.support.ExecutorAdapter;
import com.example.dtp.support.ExecutorWrapper;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/4/26 11:54 上午
 */
public class ExecutorConverter {
    private ExecutorConverter() {
    }

    ;

    public static TpMainFields toTpMainFields(ExecutorWrapper executorWrapper) {
        TpMainFields tpMainFields = new TpMainFields();
        tpMainFields.setThreadPoolName(executorWrapper.getThreadPoolName());
        ExecutorAdapter<?> executorAdapter = executorWrapper.getExecutorAdapter();
        tpMainFields.setCorePoolSize(executorAdapter.getCorePoolSize());
        tpMainFields.setMaxPoolSize(executorAdapter.getMaximumPoolSize());
        tpMainFields.setKeepAliveTime(executorAdapter.getKeepAliveTime(TimeUnit.SECONDS));
        tpMainFields.setQueueType(executorAdapter.getQueueType());
        tpMainFields.setQueueCapacity(executorAdapter.getQueueCapacity());
        tpMainFields.setAllowCoreThreadTimeout(executorAdapter.allowsCoreThreadTimeOut());
        tpMainFields.setRejectType(executorAdapter.getRejectHandlerType());
        return tpMainFields;
    }


    public static ThreadPoolStats toMetrics(ExecutorWrapper wrapper) {
        ExecutorAdapter<?> executor = wrapper.getExecutorAdapter();
        if (Objects.isNull(executor)) {
            return null;
        }
        ThreadPoolStats threadPoolStats = convertCommon(executor);
        if (executor instanceof DynamicThreadPoolExecutor) {
            DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) executor;
            threadPoolStats.setRunTimeoutCount(dynamicThreadPoolExecutor.getRunTimeout());
            threadPoolStats.setQueueTimeoutCount(dynamicThreadPoolExecutor.getQueueTimeout());
            threadPoolStats.setDynamicThreadPool(true);
        } else {
            threadPoolStats.setDynamicThreadPool(false);
        }
        return threadPoolStats;
    }


    private static ThreadPoolStats convertCommon(ExecutorAdapter<?> executor) {
        return ThreadPoolStats.builder()
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .poolSize(executor.getPoolSize())
                .activeCount(executor.getActiveCount())
                .largestPoolSize(executor.getLargestPoolSize())
                .queueType(executor.getQueueType())
                .queueCapacity(executor.getQueueCapacity())
                .queueSize(executor.getQueueSize())
                .queueRemainingCapacity(executor.getQueueRemainingCapacity())
                .taskCount(executor.getTaskCount())
                .completedTaskCount(executor.getCompletedTaskCount())
                .waitTaskCount(executor.getQueueSize())
                .rejectCount(executor.getRejectTaskCount())
                .rejectHandlerName(executor.getRejectHandlerType())
                .build();
    }
}
