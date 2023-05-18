package com.example.dtp;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author chenl
 * @Date 2023/4/25 5:26 下午
 */
@Slf4j
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup threadGroup;

    private final String namePrefix;
    /**
     * is daemon thread
     */
    private final boolean daemon;

    /**
     * thread priority
     */
    private final Integer priority;

    private final AtomicInteger seq = new AtomicInteger(1);

    public NamedThreadFactory(String namePrefix, boolean daemon, int priority) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.priority = priority;
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false, Thread.NORM_PRIORITY);

    }

    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this(namePrefix, daemon, Thread.NORM_PRIORITY);
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = namePrefix + "-" + seq.getAndIncrement();
        Thread t = new Thread(threadGroup, r, name);
        t.setDaemon(daemon);
        t.setPriority(priority);
        return t;
    }

    public String getNamePrefix() {
        return namePrefix;
    }
}
