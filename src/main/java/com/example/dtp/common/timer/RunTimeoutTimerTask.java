package com.example.dtp.common.timer;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.notifier.manager.AlarmManager;
import com.example.dtp.support.task.runnable.DynamicRunnable;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author chenl
 * @Date 2023/4/25 4:21 下午
 */
@Slf4j
public class RunTimeoutTimerTask implements TimerTask {
    private final DynamicThreadPoolExecutor executor;
    private final DynamicRunnable runnable;
    private final Thread thread;

    public RunTimeoutTimerTask(DynamicThreadPoolExecutor executor,
                               DynamicRunnable runnable, Thread thread) {
        this.executor = executor;
        this.runnable = runnable;
        this.thread = thread;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        executor.incRunTimeoutCount(1);
        //消息通知
        AlarmManager.doAlarmAsync(executor, NotifyItemEnum.RUN_TIMEOUT, runnable);
        log.warn("DynamicTp execute, run timeout, tpName: {}, taskName: {}, traceId: {}, stackTrace: {}",
                executor.getThreadPoolName(), runnable.getTaskName(),
                runnable.getTraceId(), traceToString(thread.getStackTrace()));
    }

    public String traceToString(StackTraceElement[] trace) {
        StringBuilder builder = new StringBuilder(512);
        builder.append("\n");
        for (StackTraceElement traceElement : trace) {
            builder.append("\tat ").append(traceElement).append("\n");
        }
        return builder.toString();
    }
}
