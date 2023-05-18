package com.example.dtp.support.task.runnable;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author chenl
 * @Date 2023/4/24 11:38 上午
 */
public class NamedRunnable implements Runnable {
    private final Runnable runnable;
    private final String name;

    public NamedRunnable(Runnable runnable, String name) {
        this.runnable = runnable;
        this.name = name;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

    public String getName() {
        return name;
    }

    public static NamedRunnable of(Runnable runnable, String name) {
        if (StringUtils.isBlank(name)) {
            name = runnable.getClass().getSimpleName();
        }
        return new NamedRunnable(runnable, name);
    }
}
