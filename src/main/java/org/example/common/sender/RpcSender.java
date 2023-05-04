package org.example.common.sender;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.constant.RpcStatusCode;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RpcSender {

    @Autowired
    private EventExecutor eventExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSender.class);

    public static final Map<Long, Promise<RpcResponse>> RPC_RESPONSE = new ConcurrentHashMap<>();

    public RpcResponse send(RpcRequest request, Channel channel) {
        long id = request.rpcHeader().id();
        //不能每次都new一个线程，需要优化
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>(eventExecutor);
        RPC_RESPONSE.put(id, promise);
        channel.writeAndFlush(request);
        RpcResponse rpcResponse;
        try {
            rpcResponse = promise.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.warn("request {} time out", request.rpcHeader().id());
            rpcResponse = new RpcResponse();
            rpcResponse.setCode(RpcStatusCode.TIMEOUT).setRpcHeader(request.rpcHeader()).setContent(null);
        }
        RPC_RESPONSE.remove(id);
        return rpcResponse;
    }
}
