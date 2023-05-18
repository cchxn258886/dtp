package com.example.dtp.support;

import com.jjn.distribution.infrastructure.dynamictp.support.task.runnable.MdcRunnable;

/**
 * @Author chenl
 * @Date 2023/5/15 11:35 上午
 */
public class MdCTaskWrapper implements TaskWrapper {
    private static final String NAME = "mdc";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Runnable wrapper(Runnable runnable) {
        return MdcRunnable.get(runnable);
    }
}
