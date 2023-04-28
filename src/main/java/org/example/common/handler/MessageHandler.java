package org.example.common.handler;

import cn.hutool.core.util.ClassUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.constant.Constants;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcLine;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
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
        var messageType = request.rpcHeader().messageType();
        switch (messageType) {
            case Constants.HEART_BEAT -> processHeatBeatRequest();
            case Constants.COMMENT -> processCommonRequest(ctx, request);
            case Constants.FILE -> processFileRequest(ctx, request);
            default -> {
            }
        }
    }

    private void processResponse(RpcResponse response) {
        var rpcResponsePromise = RpcSender.RPC_RESPONSE.get(response.rpcHeader().id());
        //超时会删除RPC_RESPONSE中对应的key，promise可能不存在
        if (Objects.nonNull(rpcResponsePromise)) {
            rpcResponsePromise.setSuccess(response);
        }
    }

    private void processHeatBeatRequest() {
        //do nothing
    }

    private void processCommonRequest(ChannelHandlerContext ctx, RpcRequest request) {
        var rpcResponse = new RpcResponse();
        rpcResponse.setRpcHeader(request.rpcHeader()).setCode(RpcStatusCode.OK);

        RpcLine rpcLine = request.rpcLine();
        Class<?> requestClass = ClassUtil.loadClass(rpcLine.className());
        String requestMethod = rpcLine.methodName();
        Class<?>[] parameterTypes = rpcLine.parameterTypes();
        Object[] parameters = request.rpcContent().content();

        //获取实现类对象
        if (!Factory.BEAN_WAREHOUSE.containsKey(requestClass)) {
            rpcResponse.setCode(RpcStatusCode.NOT_FOUND);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        var instance = Factory.getBean(requestClass);
        Method method;
        Object result = null;
        var start = System.currentTimeMillis();
        try {
            if (parameterTypes == null || parameterTypes.length == 0) {
                method = requestClass.getMethod(requestMethod);
                result = method.invoke(instance);
            } else {
                method = requestClass.getDeclaredMethod(requestMethod, parameterTypes);
                result = method.invoke(instance, parameters);
            }
        } catch (Exception e) {
            rpcResponse.setCode(RpcStatusCode.SERVER_ERROR);
        }
        LOGGER.debug("request {}-{} cost:{}s", requestClass.getName(), requestMethod, (System.currentTimeMillis() - start) / 1000);
        rpcResponse.setContent(result);
        ctx.channel().writeAndFlush(rpcResponse);
    }

    private void processFileRequest(ChannelHandlerContext ctx, RpcRequest request) {
    }
}
