package com.example.dtp.entity;

import com.example.dtp.enums.NotifyItemEnum;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/4/23 2:45 下午
 */
@Data
public class NotifyItem {
    /**
     * notify platform id
     */
    private List<String> platFormIds;

    /**
     * if enable notify
     */
    private boolean enabled = true;

    /**
     * notify item
     */
    private String type;

    /**
     * alarm threshold
     */
    private int threshold;
    /**
     * alarm interval time unit;
     */
    private int interval = 120;

    /**
     * cluster notify limit
     */
    private int clusterLimit = 1;

    public static List<NotifyItem> mergeSimpleNotifyItem(List<NotifyItem> resource) {
        //update notify items
        if (CollectionUtils.isEmpty(resource)) {
            return getSimpleNotifyItems();
        } else {
            List<String> configTypes = resource.stream().map(NotifyItem::getType).collect(Collectors.toList());
            List<NotifyItem> defaultItems = getSimpleNotifyItems().stream().filter((item) -> {
                return !stringContainsTypes(item.getType(), configTypes);
            }).collect(Collectors.toList());

            resource.addAll(defaultItems);
            return resource;
        }
    }


    private static boolean stringContainsTypes(String s, List<String> ss) {
        if (CollectionUtils.isEmpty(ss)) {
            return false;
        }
        return ss.contains(s.toUpperCase()) || ss.contains(s.toLowerCase());
    }

    public static List<NotifyItem> mergeAllNotifyItems(List<NotifyItem> resource) {

        //update notify items
        if (CollectionUtils.isEmpty(resource)) {
            return getSimpleNotifyItems();
        } else {
            List<String> configTypes = resource.stream().map(NotifyItem::getType).collect(Collectors.toList());
            List<NotifyItem> defaultItems = getSimpleNotifyItems().stream().filter((item) -> {
                return !stringContainsTypes(item.getType(), configTypes);
            }).collect(Collectors.toList());

            resource.addAll(defaultItems);
            return resource;
        }
    }

    public static List<NotifyItem> getSimpleNotifyItems() {
        NotifyItem notifyItem = new NotifyItem();
        notifyItem.setType(NotifyItemEnum.CHANGE.getValue());
        notifyItem.setInterval(1);

        NotifyItem livenessNotify = new NotifyItem();
        livenessNotify.setType(NotifyItemEnum.LIVENESS.getValue());
        livenessNotify.setThreshold(70);

        NotifyItem capacityNotify = new NotifyItem();
        capacityNotify.setType(NotifyItemEnum.CAPACITY.getValue());
        capacityNotify.setThreshold(70);

        ArrayList<NotifyItem> notifyItems = new ArrayList<>(3);
        notifyItems.add(notifyItem);
        notifyItems.add(livenessNotify);
        notifyItems.add(capacityNotify);
        return notifyItems;
    }

    public static List<NotifyItem> getAllNotifyItems() {
        NotifyItem notifyItem = new NotifyItem();
        notifyItem.setType(NotifyItemEnum.CHANGE.getValue());
        notifyItem.setInterval(1);

        NotifyItem livenessNotify = new NotifyItem();
        livenessNotify.setType(NotifyItemEnum.LIVENESS.getValue());
        livenessNotify.setThreshold(70);

        NotifyItem capacityNotify = new NotifyItem();
        capacityNotify.setType(NotifyItemEnum.CAPACITY.getValue());
        capacityNotify.setThreshold(70);

        ArrayList<NotifyItem> notifyItems = new ArrayList<>(3);
        notifyItems.add(notifyItem);
        notifyItems.add(livenessNotify);
        notifyItems.add(capacityNotify);
        return notifyItems;
    }

}
