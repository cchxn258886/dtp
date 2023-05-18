package com.example.dtp.entity;

import com.example.dtp.enums.NotifyItemEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author chenl
 * @Date 2023/5/15 3:26 下午
 */
@Data
@Builder
public class AlarmInfo {
    private NotifyItemEnum notifyItemEnum;
    private String lastAlarmTime;
    private final AtomicInteger counter = new AtomicInteger(0);

    public void incCounter() {
        counter.incrementAndGet();
    }

    public void reset() {
        Date date = new Date();
        lastAlarmTime = date.toString();
        counter.set(0);
    }

    public Integer getCount() {
        return this.counter.get();
    }
}
