package com.example.dtp.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author chenl
 * @Date 2023/5/17 7:20 下午
 */
@Slf4j
public class DingSignUtil {
    private DingSignUtil() {
    }

    ;

    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    private static String ALGORITHM = "HmacSHA256";

    public static String dingSign(String secret, long timestamp) {
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(DEFAULT_ENCODING), ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(DEFAULT_ENCODING));
            return URLEncoder.encode(new String(Base64.getEncoder().encode(signData), DEFAULT_ENCODING.name()));
        } catch (Exception e) {
            log.error("DynamicTp, cal ding sign error", e);
            return "";
        }
    }
}
