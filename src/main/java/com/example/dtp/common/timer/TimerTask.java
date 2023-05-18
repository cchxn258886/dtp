package com.example.dtp.common.timer;

/**
 * @Author chenl
 * @Date 2023/4/24 2:18 下午
 */
@FunctionalInterface
public interface TimerTask {
    void run(Timeout timeout) throws Exception;
}
