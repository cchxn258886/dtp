package com.example.dtp.notifier.base;


import com.example.dtp.entity.NotifyPlatform;

/**
 * @Author chenl
 * @Date 2023/5/17 2:59 下午
 */
public interface Notifier {
    public static final String HMAC_SHA_256 = "HmacSHA256";

    String platform();

    void send(NotifyPlatform platform, String content);
}
