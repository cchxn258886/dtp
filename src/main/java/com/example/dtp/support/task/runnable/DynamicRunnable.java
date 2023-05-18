package com.example.dtp.support.task.runnable;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.common.ApplicationContextHolder;
import com.example.dtp.common.constant.DynamicThreadPoolConstant;
import com.example.dtp.common.timer.HashedWheelTimer;
import com.example.dtp.common.timer.QueueTimeoutTimerTask;
import com.example.dtp.common.timer.RunTimeoutTimerTask;
import com.example.dtp.common.timer.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/4/24 11:54 上午
 */
@Slf4j
public class DynamicRunnable implements Runnable {

    private final Runnable runnable;
    private final String taskName;
    private final String traceId;
    private Timeout runTimeoutTimer;
    private Timeout queueTimeoutTimer;

    public DynamicRunnable(Runnable runnable, String taskName) {
        this.runnable = runnable;
        this.taskName = taskName;
        this.traceId = MDC.get(DynamicThreadPoolConstant.TRACE_ID);
    }

    public void startQueueTimeoutTask(DynamicThreadPoolExecutor threadPoolExecutor) {
        long queueTimeout = threadPoolExecutor.getQueueTimeout();
        if (queueTimeout <= 0) {
            return;
        }

        HashedWheelTimer hashedWheelTimer = ApplicationContextHolder.getBean(HashedWheelTimer.class);
        QueueTimeoutTimerTask queueTimeoutTimerTask = new QueueTimeoutTimerTask(threadPoolExecutor, this);
        queueTimeoutTimer = hashedWheelTimer.newTimeout(queueTimeoutTimerTask, queueTimeout, TimeUnit.MILLISECONDS);
    }


    public void cancelQueueTimeoutTask() {
        if (queueTimeoutTimer != null) {
            queueTimeoutTimer.cancel();
        }
    }

    public void startRunTimeoutTask(DynamicThreadPoolExecutor executor, Thread thread) {
        long runTimeout = executor.getRunTimeout();
        if (runTimeout <= 0) {
            return;
        }
        HashedWheelTimer hashedWheelTimer = ApplicationContextHolder.getBean(HashedWheelTimer.class);
        RunTimeoutTimerTask runTimeoutTimerTask = new RunTimeoutTimerTask(executor, this, thread);
        runTimeoutTimer = hashedWheelTimer.newTimeout(runTimeoutTimerTask, runTimeout, TimeUnit.MILLISECONDS);
    }

    public void cancelRunTimeoutTask() {
        if (runTimeoutTimer != null) {
            runTimeoutTimer.cancel();
        }
    }


    public String getTaskName() {
        return taskName;
    }

    public String getTraceId() {
        return traceId;
    }


    public Timeout getQueueTimeoutTimer() {
        return queueTimeoutTimer;
    }


    @Override
    public void run() {
        runnable.run();
    }
}
