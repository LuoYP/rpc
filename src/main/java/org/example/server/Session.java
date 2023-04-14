package org.example.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    public static final Map<String, Channel> ACTIVE_CHANNEL = new ConcurrentHashMap<>();

    private Session(){}
}
