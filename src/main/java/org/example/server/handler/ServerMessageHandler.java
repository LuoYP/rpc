package org.example.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import org.example.common.handler.MessageHandler;
import org.example.common.utils.CharSequenceUtil;
import org.example.server.Session;

import java.net.InetSocketAddress;

/**
 * Server用消息处理器
 * 1.维护所有连接
 * 2.鉴权
 * 3.消息分发
 */
@Sharable
public class ServerMessageHandler extends MessageHandler {

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
}
