package com.example.dtp.notifier;



import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.entity.TpMainFields;
import com.example.dtp.enums.NotifyItemEnum;

import java.util.List;

/**
 * @Author chenl
 * @Date 2023/5/12 10:51 上午
 */
public interface DtpNotifier {
    String platform();

    void sendChangeMsg(NotifyPlatform notifyPlatform, TpMainFields oldFields, List<String> diffs);

    void sendAlarmMsg(NotifyPlatform notifyPlatform, NotifyItemEnum notifyItemEnum);

}
