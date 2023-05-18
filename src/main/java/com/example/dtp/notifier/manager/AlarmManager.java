package com.example.dtp.notifier.manager;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.common.constant.DynamicTpConstant;
import com.example.dtp.common.pattern.filter.InvokerChain;
import com.example.dtp.entity.AlarmInfo;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.enums.QueueTypeEnum;
import com.example.dtp.enums.RejectedTypeEnum;
import com.example.dtp.notifier.alarm.AlarmCounter;
import com.example.dtp.notifier.alarm.AlarmLimiter;
import com.example.dtp.notifier.content.AlarmCtx;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.example.dtp.support.ExecutorAdapter;
import com.example.dtp.support.ExecutorWrapper;
import com.example.dtp.support.TaskWrappers;
import com.example.dtp.support.ThreadPoolBuilder;
import com.example.dtp.support.task.runnable.DynamicRunnable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * @Author chenl
 * @Date 2023/5/15 10:54 上午
 */
@Slf4j
public class AlarmManager {
    private static final ExecutorService ALARM_EXECUTOR = ThreadPoolBuilder.newBuilder()
            .threadFactory("dtp-alarm").corePoolSize(1).maximumPoolSize(2)
            .workQueue(QueueTypeEnum.LINKED_BLOCKING_DEQUE.getName(), 2000)
            .rejectedExecutionHandler(RejectedTypeEnum.DISCARD_OLDEST_POLICY.getName())
            .taskWrappers(TaskWrappers.getInstance().getByNames(new HashSet<String>(Arrays.asList("MDC"))))
            .buildDynamic();

    private static final InvokerChain<BaseNotifyCtx> ALARM_INVOKER_CHAIN;

    static {
        ALARM_INVOKER_CHAIN = NotifyFilterBuilder.getAlarmInvokerChain();
    }

    private AlarmManager() {
    }

    public static void initAlarm(String poolName, List<NotifyItem> notifyItemList) {
        notifyItemList.forEach((item) -> {
            initAlarm(poolName, item);
        });
    }

    public static void initAlarm(String poolName, NotifyItem notifyItem) {
        AlarmLimiter.initAlarmLimiter(poolName, notifyItem);
        AlarmCounter.init(poolName, notifyItem.getType());
    }

    public static void doAlarmAsync(DynamicThreadPoolExecutor executor, NotifyItemEnum notifyItemEnum) {
        AlarmCounter.incAlarmCounter(executor.getThreadPoolName(), notifyItemEnum.getValue());
        ALARM_EXECUTOR.execute(() -> {
            doAlarm(ExecutorWrapper.of(executor), notifyItemEnum);
        });
    }

    public static void doAlarmAsync(DynamicThreadPoolExecutor executor, NotifyItemEnum notifyItemEnum, Runnable currRunnable) {
        MDC.put(DynamicTpConstant.TRACE_ID, ((DynamicRunnable) currRunnable).getTraceId());
        AlarmCounter.incAlarmCounter(executor.getThreadPoolName(), notifyItemEnum.getValue());
        ALARM_EXECUTOR.execute(() -> {
            doAlarm(ExecutorWrapper.of(executor), notifyItemEnum);
        });
    }

    public static void doAlarmAsync(DynamicThreadPoolExecutor executor, List<NotifyItemEnum> notifyItemEnums) {
        doAlarmSync(ExecutorWrapper.of(executor), notifyItemEnums);
    }

    public static void doAlarmSync(ExecutorWrapper executorWrapper, List<NotifyItemEnum> notifyItemEnums) {
        ALARM_EXECUTOR.execute(() -> {
            notifyItemEnums.forEach((item) -> {
                doAlarm(executorWrapper, item);
            });
        });
    }

    public static void doAlarm(ExecutorWrapper executorWrapper, NotifyItemEnum notifyItemEnum) {
        NotifyHelper.getNotifyItem(executorWrapper, notifyItemEnum).ifPresent(notifyItem -> {
            AlarmCtx alarmCtx = new AlarmCtx(executorWrapper, notifyItem);
            ALARM_INVOKER_CHAIN.proceed(alarmCtx);
        });
    }

    public static boolean checkThreshold(ExecutorWrapper executorWrapper, NotifyItemEnum itemEnum, NotifyItem notifyItem) {
        switch (itemEnum) {
            case CAPACITY:
                return checkCapacity(executorWrapper, notifyItem);
            case LIVENESS:
                return checkLiveness(executorWrapper, notifyItem);
            case REJECT:
            case CHANGE:
            case QUEUE_TIMEOUT:
                return checkWithAlarmInfo(executorWrapper, notifyItem);
            default:
                log.error("Unsupported alarm type, type: {}", itemEnum);
                return false;
        }
    }


    private static boolean checkLiveness(ExecutorWrapper executorWrapper, NotifyItem notifyItem) {
        ExecutorAdapter<?> executorAdapter = executorWrapper.getExecutorAdapter();
        int maximumPoolSize = executorAdapter.getMaximumPoolSize();
        int activeCount = executorAdapter.getActiveCount();
        double aDouble = (double) (activeCount / maximumPoolSize);
        double div = aDouble * 100;
        return div >= notifyItem.getThreshold();
    }

    private static boolean checkCapacity(ExecutorWrapper executorWrapper, NotifyItem notifyItem) {
        ExecutorAdapter<?> executorAdapter = executorWrapper.getExecutorAdapter();
        if (executorAdapter.getQueueSize() <= 0) {
            return false;
        }
        int queueSize = executorAdapter.getQueueSize();
        int queueCapacity = executorAdapter.getQueueCapacity();
        double i = (double) queueSize / queueCapacity;
        return 100 * i >= notifyItem.getThreshold();
    }

    private static boolean checkWithAlarmInfo(ExecutorWrapper executorWrapper, NotifyItem notifyItem) {
        AlarmInfo alarmInfo = AlarmCounter.getAlarmInfo(executorWrapper.getThreadPoolName(), notifyItem.getType());
        return alarmInfo.getCount() >= notifyItem.getThreshold();
    }

    public static void destroy() {
        ALARM_EXECUTOR.shutdown();
    }

}
