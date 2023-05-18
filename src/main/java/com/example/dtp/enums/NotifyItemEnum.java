package com.example.dtp.enums;

import lombok.Getter;

/**
 * @Author chenl
 * @Date 2023/4/24 10:16 上午
 */
@Getter
public enum NotifyItemEnum {

    /**
     * config change notify
     */
    CHANGE("change"),

    /**
     * threadPool livenes notify
     */

    LIVENESS("liveness"),

    /**
     * Capacity threshold notify
     */
    CAPACITY("CAPACITY"),

    /**
     * reject notify
     */
    REJECT("reject"),

    /**
     * task run timeout alarm;
     */
    RUN_TIMEOUT("run_timeout"),

    /**
     * task queue wait timeout alarm
     */
    QUEUE_TIMEOUT("queue_timeout");;

    private final String value;

    NotifyItemEnum(String value) {
        this.value = value;
    }


    public static NotifyItemEnum of(String value) {
        for (NotifyItemEnum notifyItem : NotifyItemEnum.values()) {
            if (notifyItem.value.equals(value)) {
                return notifyItem;
            }
        }
        return null;
    }
}
