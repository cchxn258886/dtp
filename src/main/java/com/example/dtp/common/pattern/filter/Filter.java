package com.example.dtp.common.pattern.filter;

/**
 * @Author chenl
 * @Date 2023/5/15 2:38 下午
 */
public interface Filter<T> {
    int getOrder();

    void doFilter(T context, Invoker<T> nextInvoker);
}
