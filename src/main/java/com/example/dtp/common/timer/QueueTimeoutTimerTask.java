package com.example.dtp.common.timer;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.notifier.manager.AlarmManager;
import com.example.dtp.support.task.runnable.DynamicRunnable;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author chenl
 * @Date 2023/4/25 2:08 下午
 * 定时任务用来处理队列超时
 */
@Slf4j
public class QueueTimeoutTimerTask implements TimerTask {
    private final DynamicThreadPoolExecutor dynamicThreadPoolExecutor;
    private final DynamicRunnable dynamicRunnable;


    public QueueTimeoutTimerTask(DynamicThreadPoolExecutor dynamicThreadPoolExecutor,
                                 DynamicRunnable dynamicRunnable) {
        this.dynamicThreadPoolExecutor = dynamicThreadPoolExecutor;
        this.dynamicRunnable = dynamicRunnable;

    }

    @Override
    public void run(Timeout timeout) throws Exception {
        dynamicThreadPoolExecutor.incQueueTimeoutCount(1);
        //消息notify
        AlarmManager.doAlarmAsync(dynamicThreadPoolExecutor, NotifyItemEnum.QUEUE_TIMEOUT, dynamicRunnable);
        log.warn("DynamicTp execute, queue timeout, tpName: {}, taskName: {}, traceId: {}",
                dynamicThreadPoolExecutor.getThreadPoolName(),
                dynamicRunnable.getTaskName(), dynamicRunnable.getTraceId());
    }

}
