package com.example.dtp.common.constant;

/**
 * @Author chenl
 * @Date 2023/5/17 11:20 上午
 */
public class LarkNotifyConst {
    private LarkNotifyConst() {
    }

    ;
    public static final String LARK_WEBHOOK = "https://open.feishu.cn/open-apis/bot/v2/hook";
    /**
     * 配置这个参数的时候机器人可以aite人
     */
    public static final String LARK_AT_FORMAT_OPENID = "<at id='%s'></at>";

    /**
     * 配置username 只能蓝色字体展示@username 被@的人无@提醒
     */
    public static final String LARK_AT_FORMAT_USERNAME = "<at id=''>%s</at>";

    public static final String LARK_OPENID_PREFIX = "ou_";

    public static final String WARNING_COLOR = "\uD83D\uDD34";

    public static final String INFO_COLOR = "";

    public static final String COMMENT_COLOR = "";

    public static final String SIGN_REPLACE = "{";

    public static final String SIGN_PARAM = SIGN_REPLACE + "\"timestamp\": \"%s\",\"sign\": \"%s\",";

    /**
     * lark alarm json str
     */
    public static final String LARK_ALARM_JSON_STR =
            "{\"msg_type\":\"interactive\",\"card\":{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"red\",\"title\":{\"tag\":\"plain_text\",\"content\":\"【报警】 动态线程池告警\"}},\"elements\":[{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**服务名称：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**实例信息：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**环境：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**线程池名称：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"alarmType **报警类型：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"threshold **报警阈值：**\\n%s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"corePoolSize **核心线程数：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"maximumPoolSize **最大线程数：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"poolSize **当前线程数：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"activeCount **活跃线程数：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**历史最大线程数：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**任务总数：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**执行完成任务数：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**等待执行任务数：**\\n%s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueType **队列类型：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueCapacity **队列容量：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueSize **队列任务数量：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueRemaining **队列剩余容量：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"rejectType **拒绝策略：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"rejectCount **拒绝任务数量：**\\n%s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"runTimeoutCount **执行超时任务数量：**\\n%s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueTimeoutCount **等待超时任务数量：**\\n%s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**上次报警时间：**\\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**报警时间：**\\n %s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**接收人：**\\n %s\"}},{\"is_short\": true,\"text\": {\"tag\": \"lark_md\",\"content\": \"**tid：**\\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**报警间隔：**\\n %s\"}}]}]}}";

    /**
     * lark notice json str
     */
    public static final String LARK_CHANGE_NOTICE_JSON_STR =
            "{\"msg_type\":\"interactive\",\"card\":{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"green\",\"title\":{\"tag\":\"plain_text\",\"content\":\"【通知】动态线程池参数变更\"}},\"elements\":[{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**服务名称: ** \\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**实例信息：**\\n %s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**环境：**\\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**线程池名称：**\\n %s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"corePoolSize **核心线程数：**\\n %s => %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"maxPoolSize **最大线程数：**\\n %s => %s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"allowCoreThreadTimeOut **允许核心线程超时：**\\n %s => %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"keepAliveTime **线程存活时间：**\\n %s => %s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**队列类型：**\\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"queueCapacity **队列容量：**\\n %s => %s\"}}]},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"rejectType **拒绝策略：**\\n %s => %s\"}}]},{\"tag\":\"hr\"},{\"tag\":\"div\",\"fields\":[{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**接收人：**\\n %s\"}},{\"is_short\":true,\"text\":{\"tag\":\"lark_md\",\"content\":\"**通知时间：**\\n %s\"}}]}]}}";

}
