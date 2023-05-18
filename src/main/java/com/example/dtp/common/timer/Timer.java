package com.example.dtp.common.timer;

import java.util.Set;

import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/4/24 2:09 下午
 */
public interface Timer {
    /**
     * copy from dubbo
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    Set<Timeout> stop();

    boolean isStop();
}
