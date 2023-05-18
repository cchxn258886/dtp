package com.example.dtp.entity;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @Author chenl
 * @Date 2023/4/26 11:56 上午
 */
@Data
public class TpMainFields {
    private static final List<Field> FIELD_NAMES;

    static {
        FIELD_NAMES = Arrays.asList(TpMainFields.class.getDeclaredFields());
    }

    private String threadPoolName;
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;
    private String queueType;
    private int queueCapacity;
    private String rejectType;
    private boolean allowCoreThreadTimeout;

    public static List<Field> getMainField() {
        return FIELD_NAMES;
    }

}
