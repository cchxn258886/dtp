package com.example.dtp.reject;

import ch.qos.logback.core.util.DynamicClassLoadingException;
import com.jjn.distribution.infrastructure.dynamictp.enums.RejectedTypeEnum;
import com.jjn.distribution.infrastructure.dynamictp.ex.DynamicException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author chenl
 * @Date 2023/4/24 3:36 下午
 */
@Slf4j
public class RejectHandlerGetter {
    private RejectHandlerGetter() {
    }

    ;


    public static RejectedExecutionHandler buildRejectedHandler(String name) {
        if (Objects.equals(name, RejectedTypeEnum.ABORT_POLICY.getName())) {
            return new ThreadPoolExecutor.AbortPolicy();
        }
        if (Objects.equals(name, RejectedTypeEnum.CALLER_RUNS_POLICY.getName())) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
        if (Objects.equals(name, RejectedTypeEnum.DISCARD_OLDEST_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
        if (Objects.equals(name, RejectedTypeEnum.DISCARD_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
        ServiceLoader<RejectedExecutionHandler> serviceLoader = ServiceLoader.load(RejectedExecutionHandler.class);
        for (RejectedExecutionHandler handler : serviceLoader) {
            String simpleName = handler.getClass().getSimpleName();
            if (name.equalsIgnoreCase(simpleName)) {
                return handler;
            }
        }
        log.error("Cannot find specified rejectedHandler {}", name);
        throw new DynamicException("Cannot find specified rejectedHandler " + name);
    }

    public static RejectedExecutionHandler getProxy(String name) {
        return getProxy(buildRejectedHandler(name));
    }

    public static RejectedExecutionHandler getProxy(RejectedExecutionHandler handler) {
        return (RejectedExecutionHandler) Proxy.newProxyInstance(
                handler.getClass().getClassLoader(),
                new Class[]{RejectedExecutionHandler.class},
                new RejectedInvocationHandler(handler));
    }
}
