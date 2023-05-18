package com.example.dtp.notifier.content;

/**
 * @Author chenl
 * @Date 2023/5/12 11:49 上午
 */
public class DtpNotifyCtxHolder {
    private static final ThreadLocal<BaseNotifyCtx> CONTEXT = new ThreadLocal<>();

    private DtpNotifyCtxHolder() {
    }

    ;

    public static void set(BaseNotifyCtx baseNotifyCtx) {
        CONTEXT.set(baseNotifyCtx);
    }

    public static BaseNotifyCtx get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
