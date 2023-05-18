package com.example.dtp.notifier.alarm;


import com.example.dtp.DynamicThreadPoolExecutor;
import com.example.dtp.common.constant.DynamicTpConst;
import com.example.dtp.entity.AlarmInfo;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.support.ExecutorAdapter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author chenl
 * @Date 2023/5/15 3:21 下午
 */
public class AlarmCounter {
    private static final String UNKNOWN_COUNT_STR = DynamicTpConst.UNKNOWN + " / " + DynamicTpConst.UNKNOWN;

    private AlarmCounter() {
    }

    ;
    private static final Map<String, AlarmInfo> ALARM_INFO_CACHE = new ConcurrentHashMap<>();

    public static void init(String threadPoolName, String notifyItemType) {

    }

    public static AlarmInfo getAlarmInfo(String threadPoolName, String notifyItemType) {
        String key = buildKey(threadPoolName, notifyItemType);
        return ALARM_INFO_CACHE.get(key);
    }

    public static String getCount(String threadPoolName, String notifyItemType) {
        String s = buildKey(threadPoolName, notifyItemType);
        AlarmInfo alarmInfo = ALARM_INFO_CACHE.get(s);
        if (Objects.nonNull(alarmInfo)) {
            return String.valueOf(alarmInfo.getCounter());
        }
        return DynamicTpConst.UNKNOWN;
    }

    public static void reset(String threadPoolName, String notifyItemType) {
        String s = buildKey(threadPoolName, notifyItemType);
        AlarmInfo alarmInfo = ALARM_INFO_CACHE.get(s);
        if (Objects.nonNull(alarmInfo)) {
            alarmInfo.incCounter();
        }
    }

    public static Triple<String, String, String> countStrRrq(String threadPoolName, ExecutorAdapter<?> executorAdapter) {
        if (!(executorAdapter.getOriginal() instanceof DynamicThreadPoolExecutor)) {
            return new ImmutableTriple<>(UNKNOWN_COUNT_STR, UNKNOWN_COUNT_STR, UNKNOWN_COUNT_STR);
        }
        DynamicThreadPoolExecutor dtpExecutor = (DynamicThreadPoolExecutor) executorAdapter.getOriginal();
        String rejectCount = getCount(threadPoolName, NotifyItemEnum.REJECT.getValue()) + " / " + dtpExecutor.getRejectCount();
        String runTimeoutCount = getCount(threadPoolName, NotifyItemEnum.RUN_TIMEOUT.getValue()) + " / "
                + dtpExecutor.getRunTimeoutCount();
        String queueTimeoutCount = getCount(threadPoolName, NotifyItemEnum.QUEUE_TIMEOUT.getValue()) + " / "
                + dtpExecutor.getQueueTimeoutCount();
        return new ImmutableTriple<>(rejectCount, runTimeoutCount, queueTimeoutCount);
    }


    private static String buildKey(String threadPoolName, String notifyItemType) {
        return threadPoolName + ":" + notifyItemType;
    }

    public static void incAlarmCounter(String threadPoolName, String notifyItemType) {
        String key = buildKey(threadPoolName, notifyItemType);
        AlarmInfo alarmInfo = ALARM_INFO_CACHE.get(key);
        if (Objects.nonNull(alarmInfo)) {
            alarmInfo.incCounter();
        }
    }
}
