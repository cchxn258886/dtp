package com.example.dtp.notifier;


import com.example.dtp.common.constant.WechatNotifyConst;
import com.example.dtp.enums.NotifyPlatFormEnum;
import com.example.dtp.notifier.base.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @Author chenl
 * @Date 2023/5/18 2:10 下午
 */
@Slf4j
public class NotifyWechat extends AbstractDtpNotify {

    public NotifyWechat(Notifier notifier) {
        super(notifier);
    }

    public String platform() {
        return NotifyPlatFormEnum.WECHAT.name().toLowerCase();
    }

    @Override
    protected String getNoticeTemplate() {
        return WechatNotifyConst.WECHAT_CHANGE_NOTICE_TEMPLATE;
    }

    @Override
    protected String getAlarmTemplate() {
        return WechatNotifyConst.WECHAT_ALARM_TEMPLATE;
    }

    @Override
    protected Pair<String, String> getColors() {
        return new ImmutablePair<>(WechatNotifyConst.WARNING_COLOR, WechatNotifyConst.COMMENT_COLOR);
    }
}
