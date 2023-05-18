package com.example.dtp.common.pattern.filter;

/**
 * @Author chenl
 * @Date 2023/5/15 2:37 下午
 */
@FunctionalInterface
public interface Invoker<T> {

    void invoke(T context);
}
