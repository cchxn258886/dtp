package com.example.dtp.notifier.content;


import com.example.dtp.entity.AlarmInfo;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.support.ExecutorWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author chenl
 * @Date 2023/5/15 4:33 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmCtx extends BaseNotifyCtx {
    private AlarmInfo alarmInfo;

    public AlarmCtx(ExecutorWrapper wrapper, NotifyItem notifyItem) {
        super(wrapper, notifyItem);
    }
}
