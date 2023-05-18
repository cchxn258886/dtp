package com.example.dtp.notifier.base;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;

import com.example.dtp.common.constant.WechatNotifyConst;
import com.example.dtp.entity.MarkdownReq;
import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.enums.NotifyPlatFormEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @Author chenl
 * @Date 2023/5/18 11:18 上午
 */
@Slf4j
public class WechatNotifier implements Notifier {
    @Override
    public String platform() {
        return NotifyPlatFormEnum.WECHAT.name().toLowerCase();
    }

    @Override
    public void send(NotifyPlatform platform, String content) {
        String serverUrl = WechatNotifyConst.WECHAT_WEB_HOOK + platform.getUrlKey();
        MarkdownReq markdownReq = new MarkdownReq();
        markdownReq.setMsgType("markdown");
        MarkdownReq.MarkDown markDown = new MarkdownReq.MarkDown();
        markDown.setContent(content);
        markdownReq.setMarkDown(markDown);

        try {
            HttpResponse response = HttpRequest.post(serverUrl).body(JSONObject.toJSONString(markdownReq)).execute();
            if (Objects.nonNull(response)) {
                log.info("DynamicTp notify, wechat send success, response: {}, request:{}",
                        response.body(), JSONObject.toJSONString(markdownReq));
            }
        } catch (Exception e) {
            log.error("DynamicTp notify, wechat send failed...", e);
        }
    }
}
