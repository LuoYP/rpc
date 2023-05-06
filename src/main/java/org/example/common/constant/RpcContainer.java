package org.example.common.constant;

import io.netty.util.concurrent.Promise;
import org.example.common.io.RpcFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContainer {

    public static final Map<Long, Promise<byte[]>> TRANSFERRING_FILES = new ConcurrentHashMap<>();
}
