package org.example.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcContainer;
import org.example.common.context.Factory;
import org.example.common.handler.line.RpcRequestHandler;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("receive a rpc message!");
        switch (msg) {
            case RpcRequest request -> processRequest(ctx, request);
            case RpcResponse response -> processResponse(response);
            default -> {
            }
        }
    }

    private void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
        RpcRequestHandler requestHandler = (RpcRequestHandler) Factory.getBean(RpcRequestHandler.class);
        requestHandler.accept(ctx, request);
    }

    private void processResponse(RpcResponse response) {
        var messageType = response.rpcHeader().messageType();
        switch (messageType) {
            case MessageType.FILE_IN -> {
                long id = response.rpcHeader().id();
                Promise<byte[]> promise = RpcContainer.TRANSFERRING_FILES.get(id);
                if (Objects.nonNull(promise)) {
                    promise.setSuccess((byte[]) response.content());
                }
            }
            case MessageType.FILE_OUT -> {
                long id = response.rpcHeader().id();
                Promise<Boolean> promise = RpcContainer.TRANSFERRING_FILES_OUT.get(id);
                if (Objects.nonNull(promise)) {
                    promise.setSuccess((Boolean) response.content());
                }
            }
            default -> {
                var rpcResponsePromise = RpcContainer.RPC_RESPONSE.get(response.rpcHeader().id());
                //超时会删除RPC_RESPONSE中对应的key，promise可能不存在
                if (Objects.nonNull(rpcResponsePromise)) {
                    rpcResponsePromise.setSuccess(response);
                }
            }
        }

    }
}
