package org.example.server;

import io.netty.channel.Channel;
import org.example.common.constant.Protocol;
import org.example.common.utils.CharSequenceUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final Map<String, Channel> ACTIVE_CHANNEL = new ConcurrentHashMap<>();

    private Session() {
    }

    public static Set<String> getAllClientIP() {
        return ACTIVE_CHANNEL.keySet();
    }

    public static Channel getTcpChannel(String clientIP) {
        if (CharSequenceUtil.isEmpty(clientIP)) {
            throw new RuntimeException("target ip is empty");
        }
        return ACTIVE_CHANNEL.get(clientIP);
    }

    public static void putTcpChannel(String clientIp, Channel channel) {
        ACTIVE_CHANNEL.put(clientIp, channel);
    }

    public static void removeTcpChannel(String ip) {
        ACTIVE_CHANNEL.remove(ip);
    }

    public static Channel getUdpChannel() {
        return ACTIVE_CHANNEL.get(Protocol.UDP.name());
    }

    public static void putUdpChannel(Channel channel) {
        ACTIVE_CHANNEL.put(Protocol.UDP.name(), channel);
    }

    public static void removeUdpChannel() {
        ACTIVE_CHANNEL.remove(Protocol.UDP.name());
    }
}
