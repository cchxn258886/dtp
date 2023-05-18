package com.example.dtp.notifier.content;


import com.example.dtp.entity.NotifyItem;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.support.ExecutorWrapper;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2023/5/12 11:50 上午
 */
@Data
public class BaseNotifyCtx {
    private ExecutorWrapper executorWrapper;
    private NotifyItem notifyItem;

    public BaseNotifyCtx() {
    }

    ;

    public BaseNotifyCtx(ExecutorWrapper executorWrapper, NotifyItem notifyItem) {
        this.executorWrapper = executorWrapper;
        this.notifyItem = notifyItem;
    }

    public NotifyItemEnum getNotifyEnum() {
        return NotifyItemEnum.of(notifyItem.getType());
    }
}
