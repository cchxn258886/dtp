package com.example.dtp.support;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.entity.NotifyItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author chenl
 * @Date 2023/4/25 5:52 下午
 */
@Data
@Slf4j
public class ExecutorWrapper {
    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 线程池别名
     */
    private String threadPoolAliasName;

    /**
     * executor
     */
    private ExecutorAdapter<?> executorAdapter;

    /**
     * notify items
     */
    private List<NotifyItem> notifyItems;

    /**
     * notify platforms ids
     */
    private List<String> platformIds;

    /**
     * enable notify
     */
    private boolean notifyEnable = true;

    public ExecutorWrapper() {

    }

    public ExecutorWrapper(DynamicThreadPoolExecutor executor) {
        this.threadPoolName = executor.getThreadPoolName();
        this.threadPoolAliasName = executor.getThreadPoolAliasName();
        this.executorAdapter = executor;
        this.notifyItems = executor.getNotifyItems();
        this.notifyEnable = executor.isNotifyEnabled();
        this.platformIds = executor.getPlatFormIds();
    }

    public ExecutorWrapper(String threadPoolName, Executor executor) {
        this.threadPoolName = threadPoolName;
        if (executor instanceof ThreadPoolExecutor) {
            this.executorAdapter = new ThreadPoolExecutorAdapter((ThreadPoolExecutor) executor);
        } else if (executor instanceof ExecutorAdapter<?>) {
            this.executorAdapter = (ExecutorAdapter<?>) executor;
        } else {
            throw new IllegalArgumentException("unsupported executor type!");
        }
        this.notifyItems = NotifyItem.getSimpleNotifyItems();

    }


    public static ExecutorWrapper of(DynamicThreadPoolExecutor executor) {
        return new ExecutorWrapper(executor);
    }

    /**
     * 从注释来看应该是一个cacheNotify的东西
     */
//    public ExecutorWrapper capture(){
//        ExecutorWrapper executorWrapper = new ExecutorWrapper();
//        BeanUtils.copyProperties(this,executorWrapper);
//        new Cap
//    }
    public boolean isDtpExecutor() {
        return this.executorAdapter instanceof DynamicThreadPoolExecutor;
    }

    public boolean isThreadPoolExecutor() {
        return this.executorAdapter instanceof ThreadPoolExecutorAdapter;
    }
}
