package com.example.dtp.notifier;


import com.example.dtp.common.constant.LarkNotifyConst;
import com.example.dtp.enums.NotifyPlatFormEnum;
import com.example.dtp.notifier.base.Notifier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @Author chenl
 * @Date 2023/5/18 1:58 下午
 */
public class NotifyLark extends AbstractDtpNotify {
    public NotifyLark(Notifier notifier) {
        super(notifier);
    }

    @Override
    public String platform() {
        return NotifyPlatFormEnum.LARK.name().toLowerCase();
    }

    @Override
    protected String getNoticeTemplate() {
        return LarkNotifyConst.LARK_CHANGE_NOTICE_JSON_STR;
    }

    @Override
    protected String getAlarmTemplate() {
        return LarkNotifyConst.LARK_ALARM_JSON_STR;
    }

    @Override
    protected Pair<String, String> getColors() {
        return new ImmutablePair<>(LarkNotifyConst.WARNING_COLOR, LarkNotifyConst.COMMENT_COLOR);
    }
}
