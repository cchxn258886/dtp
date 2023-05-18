package com.example.dtp.notifier;

import cn.hutool.core.date.DateUtil;
import com.example.dtp.common.constant.DynamicTpConst;
import com.example.dtp.common.constant.LarkNotifyConst;
import com.example.dtp.common.util.CommonUtil;
import com.example.dtp.entity.AlarmInfo;
import com.example.dtp.entity.NotifyItem;
import com.example.dtp.entity.NotifyPlatform;
import com.example.dtp.entity.TpMainFields;
import com.example.dtp.enums.NotifyItemEnum;
import com.example.dtp.enums.NotifyPlatFormEnum;
import com.example.dtp.notifier.alarm.AlarmCounter;
import com.example.dtp.notifier.base.Notifier;
import com.example.dtp.notifier.content.AlarmCtx;
import com.example.dtp.notifier.content.BaseNotifyCtx;
import com.example.dtp.notifier.content.DtpNotifyCtxHolder;
import com.example.dtp.notifier.manager.NotifyHelper;
import com.example.dtp.support.ExecutorAdapter;
import com.example.dtp.support.ExecutorWrapper;
import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/5/12 10:51 上午
 */
@Slf4j
public abstract class AbstractDtpNotify implements DtpNotifier {
    protected Notifier notifier;

    protected AbstractDtpNotify() {

    }

    protected AbstractDtpNotify(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public String platform() {
        return null;
    }

    @Override
    public void sendChangeMsg(NotifyPlatform notifyPlatform, TpMainFields oldFields, List<String> diffs) {
        String content = buildNoticeContent(notifyPlatform, oldFields, diffs);
        if (StringUtils.isBlank(content)) {
            log.debug("Notice content is empty, ignore send notice message.");
            return;
        }
        notifier.send(notifyPlatform, content);
    }

    @Override
    public void sendAlarmMsg(NotifyPlatform notifyPlatform, NotifyItemEnum notifyItemEnum) {
        String content = buildAlarmContent(notifyPlatform, notifyItemEnum);
        if (StringUtils.isBlank(content)) {
            log.debug("Alarm content is empty, ignore send alarm message.");
            return;
        }
        notifier.send(notifyPlatform, content);
    }


    protected abstract String getNoticeTemplate();

    protected abstract String getAlarmTemplate();

    protected abstract Pair<String, String> getColors();

    protected String buildNoticeContent(NotifyPlatform platform, TpMainFields oldFields, List<String> diffs) {
        BaseNotifyCtx context = DtpNotifyCtxHolder.get();
        ExecutorWrapper executorWrapper = context.getExecutorWrapper();
        ExecutorAdapter<?> executorAdapter = executorWrapper.getExecutorAdapter();
        String content = String.format(getNoticeTemplate(), CommonUtil.getServiceInstance().getServiceName(),
                CommonUtil.getServiceInstance().getIpAddr() + ":" + CommonUtil.getServiceInstance().getPort(),
                CommonUtil.getServiceInstance().getEnv(), populatePoolName(executorWrapper),
                oldFields.getCorePoolSize(), executorAdapter.getCorePoolSize(),
                oldFields.getMaxPoolSize(), executorAdapter.getMaximumPoolSize(),
                oldFields.isAllowCoreThreadTimeout(), executorAdapter.allowsCoreThreadTimeOut(),
                oldFields.getKeepAliveTime(), executorAdapter.getKeepAliveTime(TimeUnit.SECONDS),
                executorAdapter.getQueueType(), oldFields.getQueueCapacity(), executorAdapter.getQueueCapacity(),
                oldFields.getRejectType(), executorAdapter.getRejectHandlerType(),
                getReceives(platform.getPlatform(), platform.getReceivers()),
                new Date(System.currentTimeMillis())
        );
        return highlightNotifyContent(content, diffs);
    }


    protected String buildAlarmContent(NotifyPlatform platform, NotifyItemEnum notifyItemEnum) {
        AlarmCtx context = (AlarmCtx) DtpNotifyCtxHolder.get();
        ExecutorWrapper executorWrapper = context.getExecutorWrapper();
        val executor = executorWrapper.getExecutorAdapter();
        NotifyItem notifyItem = context.getNotifyItem();
        val alarmCounter = AlarmCounter.countStrRrq(executorWrapper.getThreadPoolName(), executor);

        String content = String.format(
                getAlarmTemplate(),
                CommonUtil.getServiceInstance().getServiceName(),
                CommonUtil.getServiceInstance().getIpAddr() + ":" + CommonUtil.getServiceInstance().getPort(),
                CommonUtil.getServiceInstance().getEnv(),
                populatePoolName(executorWrapper),
                notifyItemEnum.getValue(),
                notifyItem.getThreshold(),
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getLargestPoolSize(),
                executor.getTaskCount(),
                executor.getCompletedTaskCount(),
                executor.getQueueSize(),
                executor.getQueueType(),
                executor.getQueueCapacity(),
                executor.getQueueSize(),
                executor.getQueueRemainingCapacity(),
                executor.getRejectHandlerType(),
                alarmCounter.getLeft(),
                alarmCounter.getMiddle(),
                alarmCounter.getRight(),
                Optional.ofNullable(context.getAlarmInfo()).map(AlarmInfo::getLastAlarmTime).orElse(DynamicTpConst.UNKNOWN),
                DateUtil.now(),
                getReceives(platform.getPlatform(), platform.getReceivers()),
                Optional.ofNullable(MDC.get(DynamicTpConst.TRACE_ID)).orElse(DynamicTpConst.UNKNOWN),
                notifyItem.getInterval()
        );
        return highlightAlarmContent(content, notifyItemEnum);
    }

    private String getReceives(String platform, String receives) {
        if (StringUtils.isBlank(receives)) {
            return "";
        }
        if (NotifyPlatFormEnum.LARK.name().toLowerCase().equals(platform)) {
            String[] receivers = StringUtils.split(receives, ',');
            return Joiner.on(",@").join(receivers);
        }
        return Arrays.stream(receives.split(",")).map(item ->
                StringUtils.startsWith(item, LarkNotifyConst.LARK_OPENID_PREFIX) ?
                        String.format(LarkNotifyConst.LARK_AT_FORMAT_OPENID, receives) :
                        String.format(LarkNotifyConst.LARK_AT_FORMAT_USERNAME, receives)
        ).collect(Collectors.joining(" "));
    }


    protected String populatePoolName(ExecutorWrapper executorWrapper) {
        String poolAliasName = executorWrapper.getThreadPoolAliasName();
        if (StringUtils.isBlank(poolAliasName)) {
            return executorWrapper.getThreadPoolName();
        }
        return executorWrapper.getThreadPoolName() + "(" + poolAliasName + ")";
    }

    private String highlightNotifyContent(String content, List<String> diffs) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        Pair<String, String> pair = getColors();
        for (String field : diffs) {
            content.replace(field, pair.getLeft());
        }
        for (Field field : TpMainFields.getMainField()) {
            content = content.replace(field.getName(), pair.getRight());
        }
        return content;
    }


    private String highlightAlarmContent(String content, NotifyItemEnum notifyItemEnum) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        Set<String> colorKeys = NotifyHelper.getAlarmKeys(notifyItemEnum);
        Pair<String, String> colors = getColors();
        for (String field : colorKeys) {
            content = content.replace(field, colors.getLeft());
        }
        Set<String> allAlarmKeys = NotifyHelper.getAllAlarmKeys();
        for (String field : allAlarmKeys) {
            content = content.replace(field, colors.getRight());
        }
        return content;
    }
}
