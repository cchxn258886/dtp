package com.example.dtp.support;

import com.alibaba.ttl.TtlRunnable;

/**
 * @Author chenl
 * @Date 2023/5/15 11:33 上午
 */
public class TtlTaskWrapper implements TaskWrapper {
    private static final String NAME = "ttl";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Runnable wrapper(Runnable runnable) {
        return TtlRunnable.get(runnable);
    }
}
