package com.example.dtp.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2023/5/12 5:36 下午
 */
public class StreamUtil {
    private StreamUtil() {
    }

    ;

    public static <I, T> List<I> fetchProperty(Collection<T> data, Function<T, I> mapping) {
        Assert.notNull(mapping, "mapping function must not be nul");
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }
        return data.stream().map(mapping).collect(Collectors.toList());
    }

    public static <P, O> Map<O, P> toMap(Collection<P> coll, Function<P, O> key) {
        Assert.notNull(key, "key function must not be null");
        if (CollectionUtils.isEmpty(coll)) {
            return Collections.emptyMap();
        }
        return coll.stream().collect(Collectors.toMap(key, Function.identity(), (v1, v2) -> v2));
    }

    public static <O, D, P> Map<O, P> toMap(Collection<D> list, Function<D, O> key, Function<D, P> value) {
        Assert.notNull(key, "key function must be not null");
        Assert.notNull(value, "value function must not be null");
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(key, value, (v1, v2) -> v2));
    }

    public static <I, D> Map<I, List<D>> toListMap(Collection<I> ids, Collection<D> list,
                                                   Function<D, I> key) {
        Assert.notNull(key, "mapping function must not be null");
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<I, List<D>> resultMap = list.stream().collect(Collectors.groupingBy(key));
        ids.forEach((id) -> {
            resultMap.putIfAbsent(id, Collections.emptyList());
        });
        return resultMap;
    }
}
