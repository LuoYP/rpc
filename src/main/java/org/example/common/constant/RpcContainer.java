package org.example.common.constant;

import io.netty.util.concurrent.Promise;
import org.example.common.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContainer {

    /**
     * 文件下载用缓存
     */
    public static final Map<Long, Promise<byte[]>> TRANSFERRING_FILES = new ConcurrentHashMap<>();

    /**
     * 文件上传用缓存
     */
    public static final Map<Long, Promise<Boolean>> TRANSFERRING_FILES_OUT = new ConcurrentHashMap<>();

    /**
     * 普通方法调用缓存
     */
    public static final Map<Long, Promise<RpcResponse>> RPC_RESPONSE = new ConcurrentHashMap<>();
}
