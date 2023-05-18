package com.example.dtp.notifier.alarm;

import com.example.dtp.entity.NotifyItem;
import com.example.dtp.enums.NotifyItemEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenl
 * @Date 2023/5/15 3:08 下午
 */
public class AlarmLimiter {
    private static final Map<String, Cache<String, String>> ALARM_LIMITER = new ConcurrentHashMap<>();

    private AlarmLimiter() {
    }

    ;

    public static void initAlarmLimiter(String threadPoolName, NotifyItem notifyItem) {
        if (NotifyItemEnum.CHANGE.getValue().equalsIgnoreCase(notifyItem.getType())) {
            return;
        }
        String key = genKey(threadPoolName, notifyItem.getType());
        Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterAccess(notifyItem.getInterval(), TimeUnit.SECONDS)
                .build();
        ALARM_LIMITER.put(key, cache);
    }


    public static void putVal(String threadPoolName, String type) {
        String s = genKey(threadPoolName, type);
        ALARM_LIMITER.get(s).put(type, type);
    }

    public static String getAlarmLimitInfo(String key, String type) {
        Cache<String, String> cache = ALARM_LIMITER.get(key);
        if (Objects.isNull(cache)) return null;
        return cache.getIfPresent(type);
    }

    public static boolean ifAlarm(String threadPoolName, String type) {
        String s = genKey(threadPoolName, type);
        return StringUtils.isBlank(getAlarmLimitInfo(s, type));
    }

    public static String genKey(String threadPoolName, String type) {
        return threadPoolName + ":" + type;
    }
}
