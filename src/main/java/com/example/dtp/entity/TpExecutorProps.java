package com.example.dtp.entity;


import com.example.dtp.common.constant.DynamicTpConst;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/4/26 5:38 下午
 */

@Data
public class TpExecutorProps {
    /**
     * Name of ThreadPool.
     */
    private String threadPoolName;

    /**
     * Simple Alias Name of  ThreadPool. Use for notify.
     */
    private String threadPoolAliasName;

    /**
     * CoreSize of ThreadPool.
     */
    private int corePoolSize = 1;

    /**
     * MaxSize of ThreadPool.
     */
    private int maximumPoolSize = DynamicTpConst.AVAILABLE_PROCESSORS;

    /**
     * When the number of threads is greater than the core,
     * this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     */
    private long keepAliveTime = 60;

    /**
     * Timeout unit.
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * Notify items, see {@link NotifyItemEnum}
     */
    private List<NotifyItem> notifyItems;

    /**
     * Notify platform id
     */
    private List<String> platformIds;

    /**
     * If enable notify.
     */
    private boolean notifyEnabled = true;
}
