package com.example.dtp.notifier.content;


import com.example.dtp.entity.NotifyItem;
import com.example.dtp.entity.TpMainFields;
import com.example.dtp.support.ExecutorWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author chenl
 * @Date 2023/5/18 3:19 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeCtx extends BaseNotifyCtx {

    private TpMainFields oldFields;

    private List<String> diffs;

    public NoticeCtx(ExecutorWrapper wrapper, NotifyItem notifyItem, TpMainFields oldFields, List<String> diffs) {
        super(wrapper, notifyItem);
        this.oldFields = oldFields;
        this.diffs = diffs;
    }
}
