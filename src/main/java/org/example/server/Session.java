package org.example.server;

import io.netty.channel.Channel;
import org.example.common.utils.CharSequenceUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    public static final Map<String, Channel> ACTIVE_CHANNEL = new ConcurrentHashMap<>();

    private Session() {
    }

    public Set<String> getAllClientIP() {
        return ACTIVE_CHANNEL.keySet();
    }

    public Channel getChannel(String clientIP) {
        if (CharSequenceUtil.isEmpty(clientIP)) {
            throw new RuntimeException("target ip is empty");
        }
        return ACTIVE_CHANNEL.get(clientIP);
    }
}
