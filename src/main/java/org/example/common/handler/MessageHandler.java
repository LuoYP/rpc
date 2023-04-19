package org.example.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import org.example.common.constant.RpcStatusCode;
import org.example.common.model.RpcLine;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.example.communication.interfaces.ClassCollection;

import java.lang.reflect.Method;
import java.util.Objects;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RpcRequest) {
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setRpcHeader(((RpcRequest) msg).rpcHeader()).setCode(RpcStatusCode.OK);

            RpcLine rpcLine = ((RpcRequest) msg).rpcLine();
            Class<?> requestClass = this.getClass().getClassLoader().loadClass(rpcLine.className());
            String requestMethod = rpcLine.methodName();
            Class<?>[] parameterTypes = rpcLine.parameterTypes();
            Object[] parameters = ((RpcRequest) msg).rpcContent().content();

            //获取接口的实现类,反射调用实现接口的方法
            Class<?> implClass = ClassCollection.IMPL_CLASS.get(requestClass.getName());
            if (Objects.isNull(implClass)) {
                rpcResponse.setCode(RpcStatusCode.NOT_FOUND);
                ctx.writeAndFlush(rpcResponse);
                return;
            }
            Object instance = implClass.getDeclaredConstructor().newInstance();
            Method method;
            Object result = null;

            try {
                if (parameterTypes == null || parameterTypes.length == 0) {
                    method = implClass.getMethod(requestMethod);
                    result = method.invoke(instance);
                } else {
                    method = implClass.getDeclaredMethod(requestMethod, parameterTypes);
                    result = method.invoke(instance, parameters);
                }
            } catch (Exception e) {
                rpcResponse.setCode(RpcStatusCode.SERVER_ERROR);
            }
            rpcResponse.setContent(result);
            ctx.writeAndFlush(rpcResponse);
        } else if (msg instanceof RpcResponse) {
            Promise<RpcResponse> rpcResponsePromise = RpcSender.RPC_RESPONSE.get(((RpcResponse) msg).rpcHeader().id());
            //超时会删除RPC_RESPONSE中对应的key，promise可能不存在
            if (Objects.nonNull(rpcResponsePromise)) {
                rpcResponsePromise.setSuccess((RpcResponse) msg);
            }
        }
    }
}
