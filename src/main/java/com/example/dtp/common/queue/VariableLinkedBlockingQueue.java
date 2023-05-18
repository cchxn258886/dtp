package com.example.dtp.common.queue;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author chenl
 * @Date 2023/4/26 7:15 下午
 */
public class VariableLinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, Serializable {

    private static final long serialVersionUID = -6903933977591709194L;

    static class Node<E> {
        volatile E item;
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }

    /**
     * 容量 如果为0 拿Int.Max
     */
    private int capacity;
    /**
     * 当前元素大小
     */
    private final AtomicInteger count = new AtomicInteger(0);
    /**
     * 双向链表头
     */
    private transient Node<E> head;

    /**
     * 链尾
     */
    private transient Node<E> last;

    private final ReentrantLock takeLock = new ReentrantLock();

    /**
     * 等待任务的等待队列
     */
    private final Condition notEmpty = takeLock.newCondition();

    /**
     * 锁 put or offer etc
     */
    private final ReentrantLock putLock = new ReentrantLock();

    /**
     * 等待队列 等到put
     */
    private final Condition notFull = putLock.newCondition();

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * signal a waiting put called only from take/poll
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
            ;
        }
    }

    /**
     * 创建node节点链接到队列尾部
     */
    private void insert(E e) {
        last = last.next = new Node<E>(e);
    }

    /**
     * 删除一个节点从队列头部
     */
    private E extract() {
        Node<E> first = head.next;
        head = first;
        E item = first.item;
        first.item = null;
        return item;
    }

    /**
     * 锁定 防止出入
     */
    private void fullyLock() {
        this.putLock.lock();
        this.takeLock.lock();
    }

    /**
     * 解锁
     */
    private void fullyUnlock() {
        this.putLock.unlock();
        this.takeLock.unlock();
    }

    /**
     * 创建一个linkedBlockingQueue
     * max_value 容易出现oom的问题。和JDK自带的LinkedBlockingQueue一样建议是改一下
     */
    public VariableLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * 带大小
     */
    public VariableLinkedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

    /**
     * 创建一个带初始化大小的zusai 队列 通过Colletion创建
     */
    public VariableLinkedBlockingQueue(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        for (Iterator<? extends E> it = c.iterator(); it.hasNext(); ) {
            add(it.next());
        }
    }

    public void setCapacity(int capacity) {
        final int oldCapacity = this.capacity;
        this.capacity = capacity;
        final int size = count.get();
        if (capacity > size && size >= oldCapacity) {
            signalNotFull();
        }
    }


    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;

        Itr() {
            final ReentrantLock putLock = VariableLinkedBlockingQueue.this.putLock;
            final ReentrantLock takeLock = VariableLinkedBlockingQueue.this.takeLock;
            putLock.lock();
            takeLock.lock();
            try {
                current = head.next;
                if (current != null) {
                    currentElement = current.item;
                }
            } finally {
                takeLock.unlock();
                ;
                putLock.unlock();
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            ReentrantLock putLock = VariableLinkedBlockingQueue.this.putLock;
            ReentrantLock takeLock = VariableLinkedBlockingQueue.this.takeLock;
            putLock.lock();
            takeLock.lock();
            try {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                E x = currentElement;
                lastRet = current;
                current = current.next;
                if (current != null) {
                    currentElement = current.item;
                }
                return x;
            } finally {
                takeLock.unlock();
                putLock.unlock();
            }
        }

        @Override
        public void remove() {
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            ReentrantLock putLock = VariableLinkedBlockingQueue.this.putLock;
            ReentrantLock takeLock = VariableLinkedBlockingQueue.this.takeLock;
            putLock.lock();
            takeLock.lock();
            try {
                Node<E> node = this.lastRet;
                lastRet = null;
                Node<E> trail = VariableLinkedBlockingQueue.this.head;
                Node<E> p = head.next;
                while (p != null && p != node) {
                    trail = p;
                    p = p.next;
                }
                if (p == node) {
                    p.item = null;
                    trail.next = p.next;
                    int c = count.getAndDecrement();
                    if (c >= capacity) {
                        notFull.signalAll();
                    }
                }
            } finally {
                takeLock.unlock();
                putLock.unlock();
                ;
            }
        }

    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public boolean offer(E e) {
        if (Objects.isNull(e)) {
            throw new NullPointerException();
        }
        final AtomicInteger count = this.count;
        return false;
    }

    @Override
    public void put(E e) throws InterruptedException {
        if (Objects.isNull(e)) {
            throw new NullPointerException();
        }
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            try {
                while (count.get() >= capacity) {
                    notFull.await();
                }
            } catch (InterruptedException ie) {
                notFull.signal();
                throw ie;
            }
            insert(e);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (Objects.isNull(e)) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            for (; ; ) {
                if (count.get() < capacity) {
                    insert(e);
                    c = count.getAndIncrement();
                    if (c + 1 < capacity) {
                        notFull.signal();
                    }
                    break;
                }
                if (nanos <= 0) {
                    return false;
                }
                try {
                    nanos = notFull.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notFull.signal();//唤醒未中断线程
                    throw ie;
                }
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();

        try {
            try {
                while (count.get() == 0) {
                    notEmpty.await();
                }
            } catch (InterruptedException ie) {
                notEmpty.signal();
                throw ie;
            }

            x = extract();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c >= capacity) {
            signalNotFull();
        }
        return x;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        AtomicInteger count = this.count;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            for (; ; ) {
                if (count.get() > 0) {
                    x = extract();
                    c = count.getAndDecrement();
                    if (c > 1) {
                        notEmpty.signal();
                    }
                    break;
                }
                if (nanos <= 0) {
                    return null;
                }
                try {
                    nanos = notEmpty.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notEmpty.signal();
                    throw ie;
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c >= capacity) {
            signalNotFull();
        }
        return x;
    }

    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        Node<E> first;
        fullyLock();
        try {
            first = head.next;
            head.next = null;
            if (count.getAndSet(0) >= capacity) {
                notFull.signalAll();
            }
        } finally {
            fullyUnlock();
        }
        int n = 0;
        for (Node<E> p = first; p != null; p = p.next) {
            c.add(p.item);
            p.item = null;
            ++n;
        }
        return n;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        fullyLock();
        try {
            int n = 0;
            Node<E> p = head.next;
            while (p != null && n < maxElements) {
                c.add(p.item);
                p.item = null;
                p = p.next;
                ++n;
            }
            if (n != 0) {
                head.next = p;
                if (count.getAndAdd(-n) >= capacity) {
                    notFull.signalAll();
                }
            }
            return n;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public E poll() {
        AtomicInteger count = this.count;
        if (count.get() == 0) {
            return null;
        }
        E x = null;
        int c = -1;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = extract();
                c = count.getAndDecrement();
                if (c > 1) {
                    notEmpty.signal();
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c >= capacity) {
            signalNotFull();
        }
        return x;
    }

    @Override
    public E peek() {
        if (count.get() == 0) {
            return null;
        }
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        ;
        try {
            Node<E> next = head.next;
            if (Objects.isNull(next)) {
                return null;
            } else {
                return next.item;
            }
        } finally {
            takeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        if (Objects.isNull(o)) {
            return false;
        }
        boolean removed = false;
        fullyLock();
        try {
            Node<E> trail = this.head;
            Node<E> p = head.next;
            while (p != null) {
                if (o.equals(p.item)) {
                    removed = true;
                    break;
                }
                trail = p;
                p = p.next;
            }
            if (removed) {
                p.item = null;
                trail.next = p.next;
                if (count.getAndDecrement() >= capacity) {
                    notFull.signalAll();
                }
            }
        } finally {
            fullyUnlock();
        }
        return removed;
    }

    @Override
    public Object[] toArray() {
        fullyLock();
        try {
            int size = count.get();
            Object[] objects = new Object[size];
            int k = 0;

            for (Node<E> p = head.next; p != null; p = p.next) {
                objects[k++] = p.item;
            }
            return objects;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        fullyLock();
        try {
            int size = count.get();
            if (a.length < size) {
                a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
            }
            int k = 0;
            for (Node<?> p = head.next; p != null; p = p.next) {
                a[k++] = (T) p.item;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public String toString() {
        fullyLock();
        try {
            return super.toString();
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public void clear() {
        fullyLock();
        try {
            head.next = null;
            if (count.getAndSet(0) >= capacity) {
                notFull.signalAll();
            }
        } finally {
            fullyUnlock();
        }
    }

}
