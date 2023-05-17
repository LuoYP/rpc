package org.example.common.sender;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.constant.RpcContainer;
import org.example.common.constant.RpcStatusCode;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component
public class RpcSender {

    @Autowired
    private EventExecutor eventExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSender.class);

    public RpcResponse send(RpcRequest request, Channel channel) {
        long id = request.rpcHeader().id();
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>(eventExecutor);
        RpcContainer.RPC_RESPONSE.put(id, promise);
        channel.writeAndFlush(request);
        RpcResponse rpcResponse;
        try {
            rpcResponse = promise.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.warn("request {} time out", request.rpcHeader().id());
            rpcResponse = new RpcResponse();
            rpcResponse.setCode(RpcStatusCode.TIMEOUT).setRpcHeader(request.rpcHeader()).setContent(null);
        }
        RpcContainer.RPC_RESPONSE.remove(id);
        return rpcResponse;
    }
}
