package com.example.dtp.notifier.base;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import com.example.dtp.common.constant.LarkNotifyConst;
import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.enums.NotifyPlatFormEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2023/5/18 10:36 上午
 */
@Slf4j
public class LarkNotifier implements Notifier {
    public static final String LF = "\n";

    @Override
    public String platform() {
        return NotifyPlatFormEnum.LARK.name().toLowerCase();
    }

    @Override
    public void send(NotifyPlatform platform, String text) {
        String serverUrl = LarkNotifyConst.LARK_WEBHOOK + platform.getUrlKey();
        if (StringUtils.isNotBlank(platform.getSecret())) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                String sign = genSign(platform.getSecret(), currentTimeMillis);
                text = text.replace(LarkNotifyConst.SIGN_REPLACE, String.format(LarkNotifyConst.SIGN_PARAM, currentTimeMillis, sign));
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                log.error("DynamicTp notify, lark generate signature failed...", e);
            }
        }
        try {
            HttpResponse resp = HttpRequest.post(serverUrl).body(text).execute();
            if (Objects.nonNull(resp)) {
                log.info("DynamicTp notify, lark send success, response: {}, request:{}", resp.body(), text);
            }
        } catch (Exception e) {
            log.error("DynamicTp notify, lark send failed...", e);
        }
    }

    protected String genSign(String secret, Long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String stringToSign = timestamp + LF + secret;
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        byte[] bytes = mac.doFinal(new byte[]{});
        return new String(Base64.getEncoder().encode(bytes));
    }
}
