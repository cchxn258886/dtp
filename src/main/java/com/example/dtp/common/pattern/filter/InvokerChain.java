package com.example.dtp.common.pattern.filter;

/**
 * @Author chenl
 * @Date 2023/5/15 2:35 下午
 */
public class InvokerChain<T> {
    private Invoker<T> head;

    public void proceed(T context) {
        head.invoke(context);
    }

    public void setHead(Invoker<T> head) {
        this.head = head;
    }
}
