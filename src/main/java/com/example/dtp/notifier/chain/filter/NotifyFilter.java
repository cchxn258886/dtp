package com.example.dtp.notifier.chain.filter;


import com.example.dtp.common.pattern.filter.Filter;
import com.example.dtp.enums.NotifyTypeEnum;
import com.example.dtp.notifier.content.BaseNotifyCtx;

/**
 * @Author chenl
 * @Date 2023/5/15 2:51 下午
 */
public interface NotifyFilter extends Filter<BaseNotifyCtx> {
    default boolean supports(NotifyTypeEnum notifyTypeEnum) {
        return true;
    }
}
