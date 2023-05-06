package org.example.common.constant;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContainer {

    public static final Map<Long, Promise<byte[]>> TRANSFERRING_FILES = new ConcurrentHashMap<>();
}
