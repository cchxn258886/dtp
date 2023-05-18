package com.example.dtp.enums;

import lombok.Getter;

/**
 * @Author chenl
 * @Date 2023/4/24 3:38 下午
 */
@Getter
public enum RejectedTypeEnum {
    ABORT_POLICY("AbortPolicy"),

    CALLER_RUNS_POLICY("CallerRunsPolicy"),

    DISCARD_OLDEST_POLICY("DiscardOldestPolicy"),

    DISCARD_POLICY("DiscardPolicy");

    private final String name;

    RejectedTypeEnum(String name) {
        this.name = name;
    }
}
