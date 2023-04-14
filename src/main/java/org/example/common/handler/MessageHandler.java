package org.example.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.model.RpcMessage_old;
import org.example.common.utils.CollUtil;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    public static final Map<String, Channel> ONLINE = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        String remoteIp = address.getAddress().getHostAddress();
        ONLINE.put(remoteIp, channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        ONLINE.remove(address.getAddress().getHostAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        RpcMessage_old rpcMessage = (RpcMessage_old) msg;
//        Class<?> clazz = this.getClass().getClassLoader().loadClass(rpcMessage.className());
//        Set<Class<?>> subTypesOf = new Reflections("org.example.communication").getSubTypesOf((Class<Object>) clazz);
//        Class<?> target = subTypesOf.iterator().next();
//        Object object = target.getDeclaredConstructor().newInstance();
//        List<Class<?>> classes = rpcMessage.argsType();
//        Method method;
//        if (CollUtil.isEmpty(classes)) {
//            method = clazz.getDeclaredMethod(rpcMessage.methodName());
//            method.invoke(object);
//        } else {
//            Object[] args = rpcMessage.args().toArray();
//            Class<?>[] array = classes.toArray(new Class<?>[classes.size()]);
//            method = clazz.getDeclaredMethod(rpcMessage.methodName(), array);
//            method.invoke(object, args);
//        }
    }
}
