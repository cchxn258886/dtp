package com.example.dtp.common.timer;


/**
 * @Author chenl
 * @Date 2023/4/24 2:08 下午
 */
public interface Timeout {
    Timer timer();

    TimerTask task();

    boolean isExpired();

    boolean isCancelled();

    boolean cancel();
}
