package org.example.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class CollUtil {

    /**
     * 判断是否为空集合
     *
     * @param collection 需要被判断的集合
     * @param <T>        代表集合元素类型的泛型
     * @return true:空集合；false:非空集合
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    /**
     * 迭代器是否为空
     *
     * @param iterable 包含迭代器的集合
     * @param <T>      集合元素使用的泛型
     * @return true:空集合；false:非空集合
     */
    public static <T> boolean isEmpty(Iterable<T> iterable) {
        return Objects.isNull(iterable) || !iterable.iterator().hasNext();
    }

    /**
     * 映射是否为空
     *
     * @param map 需要判断的映射关系
     * @param <T> 映射KEY类型的泛型
     * @param <V> 映射VALUE类型的泛型
     * @return true:空映射；false:非空映射
     */
    public static <T, V> boolean isEmpty(Map<T, V> map) {
        return Objects.isNull(map) || map.isEmpty();
    }
}
