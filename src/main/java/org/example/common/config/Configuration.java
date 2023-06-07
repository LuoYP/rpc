package org.example.common.config;

import cn.hutool.core.io.resource.ResourceUtil;
import org.example.common.annotation.Component;
import org.example.common.annotation.ConfigPrefix;
import org.example.common.constant.Constants;
import org.example.common.utils.CharSequenceUtil;
import org.example.common.utils.CollUtil;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
@ConfigPrefix(value = "rpc")
public class Configuration {

    //默认通讯端口8080
    private Integer tcpPort = 8080;

    private Integer udpPort = 8081;

    private String host = "127.0.0.1";

    /** 组播地址 */
    private String multicastHost = "224.2.2.3";

    private String localhost = "127.0.0.1";

    private Integer reconnectTimes = 3;

    /** netty写缓冲区的高水位线 */
    private Integer highWaterMark = 10*1024*1024;

    /** netty写缓冲区低水位线 */
    private Integer lowWaterMark = 2*1024*1024;

    public Configuration() {
        loadProperties();
    }

    public Integer tcpPort() {
        return tcpPort;
    }

    public Integer udpPort() {
        return udpPort;
    }

    public String host() {
        return host;
    }

    public String multicastHost() {
        return multicastHost;
    }

    public String localhost() {
        return localhost;
    }

    public Integer reconnectTimes() {
        return reconnectTimes;
    }

    public Integer highWaterMark() {
        return highWaterMark;
    }

    public Integer lowWaterMark() {
        return lowWaterMark;
    }

    //写死配置文件路径
    private void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = ResourceUtil.getResource("application.properties").openStream();
            properties.load(inputStream);
        } catch (Exception e) {
            return;
        }
        ConfigPrefix annotation = Configuration.class.getAnnotation(ConfigPrefix.class);
        Map<String, String> collect = null;
        if (Objects.nonNull(annotation)) {
            String prefixStr = annotation.value().concat(Constants.DOT);
            collect = properties.entrySet().stream()
                    .filter(e -> ((String) e.getKey()).startsWith(prefixStr))
                    .collect(Collectors.toMap(e -> format(CharSequenceUtil.removePrefix((String) e.getKey(), prefixStr)),
                            e -> (String) e.getValue()));
        }
        if (CollUtil.isEmpty(collect)) {
            //集合为空->没有用户自定义配置->无需覆写默认值
            return;
        }
        //覆写程序默认值
        Map<String, Field> fieldMap = Arrays.stream(this.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, f -> f));
        collect.forEach((k, v) -> {
            fieldMap.computeIfPresent(k, (fieldName, field) -> {
                try {
                    field.setAccessible(true);
                    field.set(this, CharSequenceUtil.stringToType(v, field.getType()));
                    field.setAccessible(false);
                } catch (Exception e) {
                    //失败使用默认值
                }
                return field;
            });
        });
    }

    /**
     * 将 - 连接的字符转换为驼峰
     *
     * @param value 原字符串
     * @return 转换后的字符串
     */
    private static String format(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (!value.contains(Constants.DASHED)) {
            return value;
        }
        String[] split = value.split(Constants.DASHED);
        for (int i = 1; i < split.length; i++) {
            String sub = split[i];
            if (sub.startsWith(Constants.DOT)) {
                continue;
            }
            split[i] = CharSequenceUtil.firstWordToUpperCase(sub);
        }
        return CharSequenceUtil.concat(split);
    }
}
