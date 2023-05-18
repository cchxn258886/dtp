package com.example.dtp.notifier.chain.filter;

import com.example.dtp.common.pattern.filter.Invoker;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.notifier.alarm.AlarmLimiter;
import com.example.dtp.support.ExecutorWrapper;

import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.example.dtp.notifier.manager.AlarmManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;

/**
 * @Author chenl
 * @Date 2023/5/15 2:51 下午
 */
@Slf4j
public class AlarmBaseFilter implements NotifyFilter {
    private static final Object SEND_LOCK = new Object();

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void doFilter(BaseNotifyCtx context, Invoker<BaseNotifyCtx> nextInvoker) {
        ExecutorWrapper executorWrapper = context.getExecutorWrapper();
        NotifyItem notifyItem = context.getNotifyItem();
        if (Objects.isNull(notifyItem) || !satisfyBaseCondition(notifyItem, executorWrapper)) {
            return;
        }
        boolean ifAlarm = AlarmLimiter.ifAlarm(executorWrapper.getThreadPoolName(), notifyItem.getType());
        if (!ifAlarm) {
            log.debug("DynamicTp notify, alarm limit, threadPoolName: {}, notifyItem: {}",
                    executorWrapper.getThreadPoolName(), notifyItem.getType());
            return;
        }
        if (!AlarmManager.checkThreshold(executorWrapper, context.getNotifyEnum(), notifyItem)) {
            return;
        }
        synchronized (SEND_LOCK) {
            ifAlarm = AlarmLimiter.ifAlarm(executorWrapper.getThreadPoolName(), notifyItem.getType());
            if (!ifAlarm) {
                log.warn("DynamicTp notify, concurrent send, alarm limit, threadPoolName: {}, notifyItem: {}",
                        executorWrapper.getThreadPoolName(), notifyItem.getType());
                return;
            }
            AlarmLimiter.putVal(executorWrapper.getThreadPoolName(), notifyItem.getType());
        }
        nextInvoker.invoke(context);
    }


    private boolean satisfyBaseCondition(NotifyItem notifyItem, ExecutorWrapper executor) {
        return executor.isNotifyEnable()
                && notifyItem.isEnabled()
                && CollectionUtils.isNotEmpty(notifyItem.getPlatFormIds());
    }

}
