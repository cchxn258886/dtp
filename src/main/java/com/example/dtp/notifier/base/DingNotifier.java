package com.example.dtp.notifier.base;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSONObject;
import com.example.dtp.common.constant.DingNotifyConst;
import com.example.dtp.common.util.DingSignUtil;
import com.example.dtp.entity.MarkdownReq;
import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.enums.NotifyPlatFormEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2023/5/17 3:19 下午
 */
@Slf4j
public class DingNotifier implements Notifier {
    @Override
    public String platform() {
        return NotifyPlatFormEnum.DING.name().toLowerCase();
    }

    @Override
    public void send(NotifyPlatform platform, String text) {
        MarkdownReq.MarkDown markDown = new MarkdownReq.MarkDown();
        markDown.setTitle(DingNotifyConst.DING_NOTICE_TITLE);
        markDown.setText(text);

        MarkdownReq.At at = new MarkdownReq.At();
        List<String> mobiles = Arrays.asList(StringUtils.split(platform.getReceivers(), ','));
        at.setAtMobiles(mobiles);

        if (CollectionUtils.isEmpty(mobiles)) {
            at.setAtAll(true);
        }
        MarkdownReq markdownReq = new MarkdownReq();
        markdownReq.setMsgType("markdown");
        markdownReq.setMarkDown(markDown);
        markdownReq.setAt(at);
        String hookUrl = getTargetUrl(platform.getSecret(), platform.getUrlKey());
        try {
            HttpResponse response = HttpRequest.post(hookUrl).body(JSONObject.toJSONString(markdownReq)).execute();
            if (Objects.nonNull(response)) {
                log.info("DynamicTp notify, ding send success, response: {}, request: {}",
                        response.body(), JSONObject.toJSONString(markdownReq));
            }
        } catch (Exception e) {
            log.error("DynamicTp notify, ding send failed...", e);
        }
    }


    private String getTargetUrl(String secret, String accessToken) {
        if (StringUtils.isBlank(secret)) {
            return DingNotifyConst.DING_WEB_HOOK + accessToken;
        }
        long currentTimeMillis = System.currentTimeMillis();
        String sign = DingSignUtil.dingSign(secret, currentTimeMillis);
        return DingNotifyConst.DING_WEB_HOOK + accessToken + "&timestamp=" + currentTimeMillis + "&sign=" + sign;
    }
}
