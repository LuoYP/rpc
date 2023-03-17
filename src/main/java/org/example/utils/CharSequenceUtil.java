package org.example.utils;

import java.lang.reflect.Method;
import java.util.Objects;


public class CharSequenceUtil {

    /**
     * 匹配字符串是否相等,不忽略大小写
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 匹配字符串是否相等,可控制是否忽略大小写
     *
     * @param str1
     * @param str2
     * @param ignoreCase
     * @return
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    /**
     * 字符串是否为空白字符串,为null或者由空白字符组成即为空白字符
     *
     * @param str
     * @return
     */
    public static boolean isBlank(CharSequence str) {
        return isEmpty(str) || str.toString().trim().length() == 0;
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 字符串是否为空,为null或者为空字符串均为空串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return Objects.isNull(str) || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 从源字符串中删除指定的前缀
     *
     * @param source 需要被修改的字符串
     * @param prefix 前缀
     * @return 修改后的字符串
     */
    public static String removePrefix(String source, String prefix) {
        if (equals(source, prefix, false)) {
            return null;
        }
        if (!source.startsWith(prefix)) {
            return source;
        }
        String[] split = source.split(prefix);
        return split[1];
    }

    /**
     * 将字符串转换为基本数据类型的包装类
     *
     * @param value 描述值的字符串
     * @param type  包装类型
     * @param <T>   类型泛型
     * @return 包装类值对象
     * @throws Exception
     */
    public static <T> T stringToType(String value, Class<T> type) throws Exception {
        if (type == String.class) {
            return (T) value;
        }
        String className = type.getSimpleName();
        if (type == Integer.class) {
            className = "Int";
        }
        String methodName = "parse" + className;
        Method method = type.getMethod(methodName, String.class);
        return (T) method.invoke(null, value);
    }

    /**
     * 将英文字符串首字母大写
     *
     * @param value 需要被修改的字符串
     * @return 修改后的字符串
     */
    public static String firstWordToUpperCase(String value) {
        char[] chars = value.toCharArray();
        if (97 <= chars[0] && chars[0] <= 122) {
            chars[0] ^= 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 拼接字符串
     *
     * @param strings 待拼接的字符串数组
     * @return 拼接完成的字符串
     */
    public static String concat(String... strings) {
        return concatWithSeparator(null, strings);
    }

    /**
     * 使用指定连接符拼接字符串
     *
     * @param separator 连接符
     * @param strings   待拼接的字符串数组
     * @return 拼接完成的字符串
     */
    public static String concatWithSeparator(String separator, String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            if (i != strings.length - 1 && isNotEmpty(separator)) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }


}
