package com.example.dtp.notifier.chain.filter;

import com.jjn.distribution.infrastructure.dynamictp.common.pattern.filter.Invoker;
import com.jjn.distribution.infrastructure.dynamictp.entity.NotifyItem;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.jjn.distribution.infrastructure.dynamictp.support.ExecutorWrapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;

/**
 * @Author chenl
 * @Date 2023/5/15 2:56 下午
 */
@Slf4j
public class NoticeBaseFilter implements NotifyFilter {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void doFilter(BaseNotifyCtx context, Invoker<BaseNotifyCtx> nextInvoker) {
        ExecutorWrapper executorWrapper = context.getExecutorWrapper();
        val notifyItem = context.getNotifyItem();
        if (Objects.isNull(notifyItem) || !satisFyBaseCondition(notifyItem, executorWrapper)) {
            log.debug("DynamicTp notify, no platforms configured or notification is not enabled, threadPoolName: {}",
                    executorWrapper.getThreadPoolName());
            return;
        }
        nextInvoker.invoke(context);
    }


    private boolean satisFyBaseCondition(NotifyItem notifyItem, ExecutorWrapper executorWrapper) {
        return executorWrapper.isNotifyEnable() && notifyItem.isEnabled() && CollectionUtils.isNotEmpty(notifyItem.getPlatFormIds());
    }
}
