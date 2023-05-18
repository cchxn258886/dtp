package com.example.dtp.entity;


import com.example.dtp.enums.QueueTypeEnum;
import com.example.dtp.enums.RejectedTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * @Author chenl
 * @Date 2023/4/26 5:41 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DtpExecutorProps extends TpExecutorProps {
    /**
     *
     */
    private String executorType;
    /**
     * 自己内部实现的队列
     */
    private String queueType = QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE.getName();
    /**
     * 阻塞队列大小
     */
    private int queueCapacity = 1024;

    /**
     * 公平锁 默认非公平
     */
    private boolean fair = false;
    /**
     * 最大空闲内存 单位M
     */
    private int maxFreeMemory = 16;
    /**
     * 拒绝策略 默认直接抛弃
     */
    private String rejectHandlerType = RejectedTypeEnum.ABORT_POLICY.getName();
    /**
     * 是否允许核心线程超时
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "dtp";
    /**
     * 等待调度的任务完成之后在关闭线程池
     * 不中断正在运行中的任务和执行所有任务在队列中
     */
    private boolean waitForTasksToCompleteOnShutdown = false;
    /**
     * 等待剩余任务完成执行的秒数
     */
    private int awaitTerminationSeconds = 0;
    /**
     * 准备开始的所有核心线程
     */
    private boolean preStartAllCoreThreads = false;
    /**
     * 拒绝策略的包装
     */
    private boolean rejectEnhanced = true;
    /**
     * 任务执行超时 单位 ms
     */
    private long runTimeout = 0;
    /**
     * 队列超时时间
     */
    private long queueTimeout = 0;

    /**
     * 任务包装类名
     */
    private Set<String> taskWrapperNames;

    /**
     * 检查核心参数
     *
     * @return boolean return true means params is inValid
     */
    public boolean coreParamIsInValid() {
        return this.getCorePoolSize() < 0
                || this.getMaximumPoolSize() <= 0
                || this.getMaximumPoolSize() < this.getCorePoolSize()
                || this.getKeepAliveTime() < 0;
    }
}
