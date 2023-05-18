package com.example.dtp.common.pattern.filter;

/**
 * @Author chenl
 * @Date 2023/5/15 2:35 下午
 */
public class InvokerChainFactory {
    private InvokerChainFactory() {
    }

    ;

    public static <T> InvokerChain<T> buildInvokerChain(Invoker<T> target, Filter<T>... filters) {
        InvokerChain<T> invokerChain = new InvokerChain<>();
        Invoker<T> last = target;
        for (int i = filters.length - 1; i >= 0; i--) {
            Invoker<T> next = last;
            Filter<T> filter = filters[i];
            last = context -> filter.doFilter(context, next);
        }
        invokerChain.setHead(last);
        return invokerChain;
    }
}
