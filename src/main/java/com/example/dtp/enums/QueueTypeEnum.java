package com.example.dtp.enums;


import com.example.dtp.common.queue.VariableLinkedBlockingQueue;
import com.example.dtp.ex.DynamicException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @Author chenl
 * @Date 2023/4/26 5:46 下午
 */
@Slf4j
@Getter
public enum QueueTypeEnum {
    /**
     * BlockingQueue type.
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    PRIORITY_BLOCKING_QUEUE(3, "PriorityBlockingQueue"),

    DELAY_QUEUE(4, "DelayQueue"),

    SYNCHRONOUS_QUEUE(5, "SynchronousQueue"),

    LINKED_TRANSFER_QUEUE(6, "LinkedTransferQueue"),

    LINKED_BLOCKING_DEQUE(7, "LinkedBlockingDeque"),

    VARIABLE_LINKED_BLOCKING_QUEUE(8, "VariableLinkedBlockingQueue"),

    MEMORY_SAFE_LINKED_BLOCKING_QUEUE(9, "MemorySafeLinkedBlockingQueue");

    private final Integer code;
    private final String name;

    QueueTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BlockingQueue<Runnable> buildLbq(String name, int capacity) {
        return buildLbq(name, capacity, false, 256);
    }

    public static BlockingQueue<Runnable> buildLbq(String name, int capacity, boolean fair, int maxFreeMemory) {
        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(name, ARRAY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new ArrayBlockingQueue<>(capacity);
        } else if (Objects.equals(name, LINKED_BLOCKING_QUEUE.getName())) {
            blockingQueue = new LinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(name, PRIORITY_BLOCKING_QUEUE.getName())) {
            blockingQueue = new PriorityBlockingQueue<>(capacity);
        } else if (Objects.equals(name, DELAY_QUEUE.getName())) {
            blockingQueue = new DelayQueue();
        } else if (Objects.equals(name, SYNCHRONOUS_QUEUE.getName())) {
            blockingQueue = new SynchronousQueue<>(fair);
        } else if (Objects.equals(name, LINKED_TRANSFER_QUEUE.getName())) {
            blockingQueue = new LinkedTransferQueue<>();
        } else if (Objects.equals(name, LINKED_BLOCKING_DEQUE.getName())) {
            blockingQueue = new LinkedBlockingDeque<>(capacity);
        } else if (Objects.equals(name, VARIABLE_LINKED_BLOCKING_QUEUE.getName())) {
            //自己内部实现的队列直接干掉
            blockingQueue = new VariableLinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(name, MEMORY_SAFE_LINKED_BLOCKING_QUEUE.getName())) {
            //自己内部实现的队列直接干掉
            //blockingQueue = new MemorySafeLinkedBlockingQueue<>(capacity, maxFreeMemory * M_1);
        }
        if (blockingQueue != null) {
            return blockingQueue;
        }

        log.error("Cannot find specified BlockingQueue {}", name);
        throw new DynamicException("Cannot find specified BlockingQueue " + name);
    }
}
