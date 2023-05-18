package com.example.dtp.entity;

import lombok.Data;

import java.util.UUID;

/**
 * @Author chenl
 * @Date 2023/4/26 5:35 下午
 * 通知平台
 */
@Data
public class NotifyPlatform {
    /**
     * Notify platform id.
     */
    private String platformId = UUID.randomUUID().toString();

    /**
     * Notify platform name.
     */
    private String platform;

    /**
     * Token of url.
     */
    private String urlKey;

    /**
     * Secret, may be null.
     */
    private String secret;

    /**
     * Receivers, split by ,
     */
    private String receivers = "all";
}
