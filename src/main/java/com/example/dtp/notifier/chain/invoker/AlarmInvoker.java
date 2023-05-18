package com.example.dtp.notifier.chain.invoker;


import com.example.dtp.common.pattern.filter.Invoker;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.handle.NotifierHandler;
import com.example.dtp.notifier.alarm.AlarmCounter;
import com.example.dtp.notifier.content.AlarmCtx;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.example.dtp.notifier.content.DtpNotifyCtxHolder;
import lombok.val;

/**
 * @Author chenl
 * @Date 2023/5/18 3:10 下午
 */
public class AlarmInvoker implements Invoker<BaseNotifyCtx> {
    @Override
    public void invoke(BaseNotifyCtx context) {

        val alarmCtx = (AlarmCtx) context;
        val executorWrapper = alarmCtx.getExecutorWrapper();
        val notifyItem = alarmCtx.getNotifyItem();
        val alarmInfo = AlarmCounter.getAlarmInfo(executorWrapper.getThreadPoolName(), notifyItem.getType());
        alarmCtx.setAlarmInfo(alarmInfo);

        try {
            DtpNotifyCtxHolder.set(context);
            NotifierHandler.getInstance().sendAlarm(NotifyItemEnum.of(notifyItem.getType()));
            AlarmCounter.reset(executorWrapper.getThreadPoolName(), notifyItem.getType());
        } finally {
            DtpNotifyCtxHolder.remove();
        }
    }
}
