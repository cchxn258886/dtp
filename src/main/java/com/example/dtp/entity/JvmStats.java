package com.example.dtp.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author chenl
 * @Date 2023/5/12 2:09 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JvmStats extends Metrics {
    private String maxMemory;
    private String totalMemory;
    private String freeMemory;
    private String usableMemory;

}
