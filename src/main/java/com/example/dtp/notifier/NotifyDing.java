package com.example.dtp.notifier;


import com.example.dtp.common.constant.DingNotifyConst;
import com.example.dtp.enums.NotifyPlatFormEnum;
import com.example.dtp.notifier.base.Notifier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @Author chenl
 * @Date 2023/5/17 2:58 下午
 */
public class NotifyDing extends AbstractDtpNotify {
    public NotifyDing(Notifier notifyer) {
        super(notifyer);
    }

    @Override
    public String platform() {
        return NotifyPlatFormEnum.DING.name().toLowerCase();
    }

    @Override
    protected String getNoticeTemplate() {
        return DingNotifyConst.DING_CHANGE_NOTICE_TEMPLATE;
    }

    @Override
    protected String getAlarmTemplate() {
        return DingNotifyConst.DING_ALARM_TEMPLATE;
    }

    @Override
    protected Pair<String, String> getColors() {
        return new ImmutablePair<>(DingNotifyConst.WARNING_COLOR, DingNotifyConst.CONTENT_COLOR);
    }
}
