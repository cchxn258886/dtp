package com.example.dtp.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author chenl
 * @Date 2023/5/17 4:02 下午
 */
@Data
public class MarkdownReq {
    private String msgType;
    private MarkDown markDown;
    private At at;

    @Data
    public static class MarkDown {
        private String title;
        //for wechat
        private String content;
        //for ding
        private String text;
    }

    @Data
    public static class At {
        private List<String> atMobiles;
        private boolean isAtAll;
    }
}
