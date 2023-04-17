package org.example.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.model.RpcLine;
import org.example.common.model.RpcRequest;
import org.example.common.utils.CharSequenceUtil;
import org.example.server.Session;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * Server用消息处理器
 * 1.维护所有连接
 * 2.鉴权
 * 3.消息分发
 */
@Sharable
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String ipAddress = socketAddress.getAddress().getHostAddress();
        Session.ACTIVE_CHANNEL.putIfAbsent(ipAddress, channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String ipAddress = socketAddress.getAddress().getHostAddress();
        Session.ACTIVE_CHANNEL.entrySet().removeIf(e -> CharSequenceUtil.equals(ipAddress, e.getKey()));
        super.channelInactive(ctx);
    }

    /**
     * 处理RPC请求,
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcLine rpcLine = ((RpcRequest) msg).rpcLine();
            Class<?> requestClass = this.getClass().getClassLoader().loadClass(rpcLine.className());
            Type type = requestClass.getGenericInterfaces()[0];
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> implementationClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];


            String s = rpcLine.className();
        }

        super.channelRead(ctx, msg);
    }
}
