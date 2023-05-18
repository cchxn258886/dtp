package com.example.dtp.notifier.manager;


import com.example.dtp.common.ApplicationContextHolder;
import com.example.dtp.common.pattern.filter.Filter;
import com.example.dtp.common.pattern.filter.InvokerChain;
import com.example.dtp.common.pattern.filter.InvokerChainFactory;
import com.example.dtp.enums.NotifyTypeEnum;
import com.example.dtp.notifier.chain.filter.AlarmBaseFilter;
import com.example.dtp.notifier.chain.filter.NoticeBaseFilter;
import com.example.dtp.notifier.chain.filter.NotifyFilter;
import com.example.dtp.notifier.chain.invoker.AlarmInvoker;
import com.example.dtp.notifier.chain.invoker.NoticeInvoker;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.google.common.collect.Lists;
import lombok.val;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/5/15 2:49 下午
 */
public class NotifyFilterBuilder {
    private NotifyFilterBuilder() { }

    public static InvokerChain<BaseNotifyCtx> getAlarmInvokerChain() {
        val filters = ApplicationContextHolder.getBeansOfType(NotifyFilter.class);
        Collection<NotifyFilter> alarmFilters = Lists.newArrayList(filters.values());
        alarmFilters.add(new AlarmBaseFilter());
        alarmFilters = alarmFilters.stream()
                .filter(x -> x.supports(NotifyTypeEnum.ALARM))
                .sorted(Comparator.comparing(Filter::getOrder))
                .collect(Collectors.toList());
        return InvokerChainFactory.buildInvokerChain(new AlarmInvoker(), alarmFilters.toArray(new NotifyFilter[0]));
    }

    public static InvokerChain<BaseNotifyCtx> getCommonInvokerChain() {
        val filters = ApplicationContextHolder.getBeansOfType(NotifyFilter.class);
        Collection<NotifyFilter> noticeFilters = Lists.newArrayList(filters.values());
        noticeFilters.add(new NoticeBaseFilter());
        noticeFilters = noticeFilters.stream()
                .filter(x -> x.supports(NotifyTypeEnum.COMMON))
                .sorted(Comparator.comparing(Filter::getOrder))
                .collect(Collectors.toList());
        return InvokerChainFactory.buildInvokerChain(new NoticeInvoker(), noticeFilters.toArray(new NotifyFilter[0]));
    }
}
