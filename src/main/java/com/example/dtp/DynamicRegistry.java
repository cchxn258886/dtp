package com.example.dtp;


import com.example.dtp.configcenter.DynamicProperties;
import com.example.dtp.convert.ExecutorConverter;
import com.example.dtp.ex.DynamicException;
import com.example.dtp.support.ExecutorWrapper;
import com.github.dadiyang.equator.Equator;
import com.github.dadiyang.equator.GetterBaseEquator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @Author chenl
 * @Date 2023/4/25 5:50 下午
 * ApplicationRunner 可以在Springboot项目启动的时候输出
 * 核心注册表 用来保存所有已保存的动态线程池的执行器
 */
@Slf4j
public class DynamicRegistry implements ApplicationRunner {
    /**
     * Maintain all automatically registered and manually registered Executors(DtpExecutors and JUC ThreadPoolExecutors).
     */
    private static final Map<String, ExecutorWrapper> EXECUTOR_REGISTRY = new ConcurrentHashMap<>();

    /**
     * equator for comparing two TpMainFields
     */
    private static Equator EQUATOR = new GetterBaseEquator();


    /**
     * dynamic properties
     */
    private static DynamicProperties dynamicProperties;

    public DynamicRegistry(DynamicProperties dynamicProperties) {
        //设置属性
        DynamicRegistry.dynamicProperties = dynamicProperties;
    }

    /**
     * Get all executor names
     */
    public static Set<String> listAllExecutorNames() {
        return Collections.unmodifiableSet(EXECUTOR_REGISTRY.keySet());
    }

    public static Map<String, ExecutorWrapper> listAllExecutor() {
        return EXECUTOR_REGISTRY;
    }

    /**
     * register a threadPoolExecutor
     * 后续支持其他线程池放入 TODO
     */
    public static void registerExecutor(ExecutorWrapper wrapper, String source) {
        log.info("DynamicTp register dtpExecutor, source: {}, executor: {}",
                source, ExecutorConverter.toTpMainFields(wrapper));
        EXECUTOR_REGISTRY.putIfAbsent(wrapper.getThreadPoolName(), wrapper);

    }


    /**
     * Get Dynamic ThreadPoolExecutor by threadPoolName
     */
    public static DynamicThreadPoolExecutor getDynamicThreadPoolExecutor(ExecutorWrapper wrapper, String name) {
        ExecutorWrapper executorWrapper = getExecutorWrapper(name);
        if (Objects.isNull(executorWrapper)) {
            log.error("The specified executor is not a DtpExecutor, name: {}", name);
            throw new DynamicException("The specified executor is not a DtpExecutor, name: " + name);
        }
        return (DynamicThreadPoolExecutor) executorWrapper.getExecutorAdapter();
    }


    /**
     * Get threadPoolExecutor by threadPool name
     */
    public static Executor getExecutor(final String name) {
        ExecutorWrapper executorWrapper = EXECUTOR_REGISTRY.get(name);
        if (Objects.isNull(executorWrapper)) {
            log.error("Cannot find a specified executor, name: {}", name);
            throw new DynamicException("Cannot find a specified executor, name: " + name);
        }
        return executorWrapper.getExecutorAdapter();
    }

    /**
     * Get executorWrapper by threadPoolName
     */
    public static ExecutorWrapper getExecutorWrapper(final String name) {
        ExecutorWrapper executorWrapper = EXECUTOR_REGISTRY.get(name);
        if (Objects.isNull(executorWrapper)) {
            log.error("Cannot find a specified executorWrapper, name: {}", name);
            throw new DynamicException("Cannot find a specified executorWrapper, name: " + name);
        }
        return executorWrapper;
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<String> remoteExecutors = Collections.emptySet();

        HashSet<String> registerExecutors = new HashSet<>(EXECUTOR_REGISTRY.keySet());
        Collection localExecutors = CollectionUtils.subtract(registerExecutors, remoteExecutors);
        log.info("DtpRegistry has been initialized, remote executors: {}, local executors: {}",
                remoteExecutors, localExecutors);
    }
}
