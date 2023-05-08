package org.example.common.constant;

import io.netty.util.concurrent.Promise;
import org.example.common.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContainer {

    public static final Map<Long, Promise<byte[]>> TRANSFERRING_FILES = new ConcurrentHashMap<>();

    public static final Map<Long, Promise<RpcResponse>> RPC_RESPONSE = new ConcurrentHashMap<>();
}
