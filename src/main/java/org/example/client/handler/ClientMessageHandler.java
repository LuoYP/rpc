package org.example.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.example.client.Cookies;
import org.example.common.context.Factory;
import org.example.common.handler.MessageHandler;

public class ClientMessageHandler extends MessageHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Cookies cookies = (Cookies) Factory.BEAN_WAREHOUSE.get(Cookies.class);
        cookies.setServer(channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Cookies cookies = (Cookies) Factory.BEAN_WAREHOUSE.get(Cookies.class);
        cookies.setServer(null);
        super.channelInactive(ctx);
    }
}
