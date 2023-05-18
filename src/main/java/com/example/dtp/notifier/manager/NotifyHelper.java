package com.example.dtp.notifier.manager;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.common.ApplicationContextHolder;
import com.example.dtp.common.util.StreamUtil;
import com.example.dtp.configcenter.DynamicProperties;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.support.ExecutorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/5/12 4:24 下午
 */
@Slf4j
public class NotifyHelper {
    private static final List<String> COMMON_ALARM_KEYS = Arrays.asList("alarmType", "threshold");
    private static final Set<String> LIVENESS_ALARM_KEYS = new HashSet<String>(
            Arrays.asList("corePoolSize", "maximumPoolSize", "poolSize", "activeCount"));
    private static final Set<String> CAPACITY_ALARM_KEYS = new HashSet<>(
            Arrays.asList("queueType", "queueCapacity", "queueSize", "queueRemaining"));
    private static final Set<String> REJECT_ALARM_KEYS = new HashSet<>(Arrays.asList(
            "rejectType", "rejectCount"));
    private static final Set<String> RUN_TIMEOUT_ALARM_KEYS = new HashSet<>(Arrays.asList(
            "runTimeoutCount"));
    private static final Set<String> QUEUE_TIMEOUT_ALARM_KEYS = new HashSet<>(Arrays.asList(
            "queueTimeoutCount"));

    private static final Set<String> ALL_ALARM_KEYS;
    private static final Map<String, Set<String>> ALARM_KEYS = new HashMap<>();

    static {
        ALARM_KEYS.put(NotifyItemEnum.LIVENESS.name(), LIVENESS_ALARM_KEYS);
        ALARM_KEYS.put(NotifyItemEnum.CAPACITY.name(), CAPACITY_ALARM_KEYS);
        ALARM_KEYS.put(NotifyItemEnum.REJECT.name(), REJECT_ALARM_KEYS);
        ALARM_KEYS.put(NotifyItemEnum.RUN_TIMEOUT.name(), RUN_TIMEOUT_ALARM_KEYS);
        ALARM_KEYS.put(NotifyItemEnum.QUEUE_TIMEOUT.name(), QUEUE_TIMEOUT_ALARM_KEYS);

        ALL_ALARM_KEYS = ALARM_KEYS.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        ALL_ALARM_KEYS.addAll(COMMON_ALARM_KEYS);
    }

    private NotifyHelper() {
    }

    public static Set<String> getAllAlarmKeys() {
        return ALL_ALARM_KEYS;
    }

    public static Set<String> getAlarmKeys(NotifyItemEnum notifyItemEnum) {
        Set<String> keys = ALARM_KEYS.get(notifyItemEnum.getValue());
        keys.addAll(COMMON_ALARM_KEYS);
        return keys;
    }

    public static Optional<NotifyItem> getNotifyItem(ExecutorWrapper executorWrapper, NotifyItemEnum notifyItemEnum) {
        if (CollectionUtils.isEmpty(executorWrapper.getNotifyItems())) {
            return Optional.empty();
        }

        return executorWrapper.getNotifyItems().stream().filter((x) -> notifyItemEnum.getValue().equalsIgnoreCase(x.getType())).findFirst();
    }

    public static void fillPlatforms(List<String> platformIds, List<NotifyPlatform> platforms,
                                     List<NotifyItem> notifyItems
    ) {
        if (CollectionUtils.isEmpty(platforms) || CollectionUtils.isEmpty(notifyItems)) {
            return;
        }
        List<String> globalPlatformIds = StreamUtil.fetchProperty(platforms, NotifyPlatform::getPlatformId);
        notifyItems.forEach((n) -> {
            if (CollectionUtils.isNotEmpty(n.getPlatFormIds())) {
                n.setPlatFormIds((List<String>) CollectionUtils.intersection(globalPlatformIds, n.getPlatFormIds()));
            } else if (CollectionUtils.isNotEmpty(platformIds)) {
                n.setPlatFormIds((List<String>) CollectionUtils.intersection(globalPlatformIds, platformIds));
            } else {
                n.setPlatFormIds(globalPlatformIds);
            }
        });

    }


    public static Optional<NotifyPlatform> getPlatForm(String platformId) {
        Map<String, NotifyPlatform> allPlatforms = getAllPlatforms();
        return Optional.ofNullable(allPlatforms.get(platformId));
    }

    public static Map<String, NotifyPlatform> getAllPlatforms() {
        DynamicProperties dtpProperties = ApplicationContextHolder.getBean(DynamicProperties.class);
        if (CollectionUtils.isEmpty(dtpProperties.getPlatforms())) {
            return Collections.emptyMap();
        }
        return StreamUtil.toMap(dtpProperties.getPlatforms(), NotifyPlatform::getPlatformId);
    }

    public static void initNotify(DynamicThreadPoolExecutor dynamicThreadPoolExecutor) {
        DynamicProperties dtpProperties = ApplicationContextHolder.getBean(DynamicProperties.class);
        List<NotifyPlatform> platforms = dtpProperties.getPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            dynamicThreadPoolExecutor.setNotifyItems(new ArrayList<>());
            dynamicThreadPoolExecutor.setPlatFormIds(new ArrayList<>());
            log.warn("DynamicTp notify, no notify platforms configured for [{}]", dynamicThreadPoolExecutor.getThreadPoolName());
            return;
        }
        if (CollectionUtils.isEmpty(dynamicThreadPoolExecutor.getNotifyItems())) {
            log.warn("DynamicTp notify, no notify items configured for [{}]", dynamicThreadPoolExecutor.getThreadPoolName());
            return;
        }
        fillPlatforms(dynamicThreadPoolExecutor.getPlatFormIds(), platforms, dynamicThreadPoolExecutor.getNotifyItems());

    }
}
