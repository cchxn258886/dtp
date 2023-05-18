package com.example.dtp.support;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/5/15 11:09 上午
 */
public class TaskWrappers {
    private static final List<TaskWrapper> TASK_WRAPPERS = new ArrayList<>();

    private TaskWrappers() {
        ServiceLoader<TaskWrapper> loader = ServiceLoader.load(TaskWrapper.class);
        for (TaskWrapper taskWrapper : loader) {
            TASK_WRAPPERS.add(taskWrapper);
        }
        TASK_WRAPPERS.add(new TtlTaskWrapper());
        TASK_WRAPPERS.add(new MdCTaskWrapper());
    }

    public List<TaskWrapper> getByNames(Set<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            return Collections.emptyList();
        }
        List<TaskWrapper> result = new ArrayList<>();
        TASK_WRAPPERS.forEach((item) -> {
            String name = item.name();
            TASK_WRAPPERS.forEach((k) -> {
                if (k.name().equalsIgnoreCase(name)) {
                    result.add(k);
                }
            });
        });
        return result;
    }

    public static TaskWrappers getInstance() {
        return TaskWrappersHolder.INSTANCE;
    }

    private static class TaskWrappersHolder {
        private static final TaskWrappers INSTANCE = new TaskWrappers();
    }

}
