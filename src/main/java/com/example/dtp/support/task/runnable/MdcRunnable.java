package com.example.dtp.support.task.runnable;

import com.jjn.distribution.infrastructure.dynamictp.common.constant.DynamicTpConst;
import org.apache.commons.collections.MapUtils;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Set;

/**
 * @Author chenl
 * @Date 2023/5/15 11:37 上午
 */
public class MdcRunnable implements Runnable {
    private final Runnable runnable;
    private final Map<String, String> parentMdc;

    public MdcRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.parentMdc = MDC.getCopyOfContextMap();
    }

    public static MdcRunnable get(Runnable runnable) {
        return new MdcRunnable(runnable);
    }

    @Override
    public void run() {
        if (MapUtils.isEmpty(parentMdc)) {
            runnable.run();
            return;
        }
        Set<Map.Entry<String, String>> entries = parentMdc.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            MDC.put(entry.getKey(), entry.getValue());
        }
        try {
            //exec
            runnable.run();
        } finally {
            for (Map.Entry<String, String> entry : entries) {
                if (!DynamicTpConst.TRACE_ID.equals(entry.getKey())) {
                    MDC.remove(entry.getKey());
                }
            }
        }
    }
}
