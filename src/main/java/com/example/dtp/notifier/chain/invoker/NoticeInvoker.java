package com.example.dtp.notifier.chain.invoker;


import com.example.dtp.common.pattern.filter.Invoker;
import com.example.dtp.handle.NotifierHandler;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.example.dtp.notifier.content.DtpNotifyCtxHolder;
import com.example.dtp.notifier.content.NoticeCtx;
import lombok.val;

/**
 * @Author chenl
 * @Date 2023/5/18 3:18 下午
 */
public class NoticeInvoker implements Invoker<BaseNotifyCtx> {
    @Override
    public void invoke(BaseNotifyCtx context) {
        try {
            DtpNotifyCtxHolder.set(context);
            val noticeCtx = (NoticeCtx) context;
            NotifierHandler.getInstance().sendNotice(noticeCtx.getOldFields(), noticeCtx.getDiffs());
        } finally {
            DtpNotifyCtxHolder.remove();
        }
    }
}
