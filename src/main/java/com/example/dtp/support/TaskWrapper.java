package com.example.dtp.support;

/**
 * @Author chenl
 * @Date 2023/4/23 3:41 下午
 */
@FunctionalInterface
public interface TaskWrapper {
    /**
     * task wrapper name or config
     */
    default String name() {
        return "";
    }

    /**
     * enhance the given runnable
     */
    Runnable wrapper(Runnable runnable);
}
