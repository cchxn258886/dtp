package com.example.dtp.handle;



import com.example.dtp.entity.NotifyItem;
import com.example.dtp.entity.TpMainFields;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.notifier.DtpNotifier;
import com.example.dtp.notifier.NotifyDing;
import com.example.dtp.notifier.NotifyLark;
import com.example.dtp.notifier.NotifyWechat;
import com.example.dtp.notifier.base.DingNotifier;
import com.example.dtp.notifier.base.LarkNotifier;
import com.example.dtp.notifier.base.WechatNotifier;
import com.example.dtp.notifier.content.DtpNotifyCtxHolder;
import com.example.dtp.notifier.manager.NotifyHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @Author chenl
 * @Date 2023/5/18 3:12 下午
 */
public class NotifierHandler {

    private static final Map<String, DtpNotifier> NOTIFIERS = new HashMap<>();

    private NotifierHandler() {
        ServiceLoader<DtpNotifier> loader = ServiceLoader.load(DtpNotifier.class);
        for (DtpNotifier notifier : loader) {
            NOTIFIERS.put(notifier.platform(), notifier);
        }
        DtpNotifier dingNotifier = new NotifyDing(new DingNotifier());
        DtpNotifier wechatNotifier = new NotifyWechat(new WechatNotifier());
        DtpNotifier larkNotifier = new NotifyLark(new LarkNotifier());
        NOTIFIERS.put(dingNotifier.platform(), dingNotifier);
        NOTIFIERS.put(wechatNotifier.platform(), wechatNotifier);
        NOTIFIERS.put(larkNotifier.platform(), larkNotifier);
    }

    public void sendNotice(TpMainFields oldFields, List<String> diffs) {
        NotifyItem notifyItem = DtpNotifyCtxHolder.get().getNotifyItem();
        for (String platformId : notifyItem.getPlatFormIds()) {
            NotifyHelper.getPlatForm(platformId).ifPresent(p -> {
                DtpNotifier notifier = NOTIFIERS.get(p.getPlatform().toLowerCase());
                if (notifier != null) {
                    notifier.sendChangeMsg(p, oldFields, diffs);
                }
            });
        }
    }

    public void sendAlarm(NotifyItemEnum notifyItemEnum) {
        NotifyItem notifyItem = DtpNotifyCtxHolder.get().getNotifyItem();
        for (String platformId : notifyItem.getPlatFormIds()) {
            NotifyHelper.getPlatForm(platformId).ifPresent(p -> {
                DtpNotifier notifier = NOTIFIERS.get(p.getPlatform().toLowerCase());
                if (notifier != null) {
                    notifier.sendAlarmMsg(p, notifyItemEnum);
                }
            });
        }
    }

    public static NotifierHandler getInstance() {
        return NotifierHandlerHolder.INSTANCE;
    }

    private static class NotifierHandlerHolder {
        private static final NotifierHandler INSTANCE = new NotifierHandler();
    }
}
