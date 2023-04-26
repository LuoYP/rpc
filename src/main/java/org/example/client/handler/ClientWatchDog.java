package org.example.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.example.client.Cookies;

/**
 * 看门狗,帮助客户端断线重连
 * 使用Netty的时间轮执行重连任务
 */
public class ClientWatchDog extends ChannelInboundHandlerAdapter implements TimerTask {

    private Timer timer;

    private Bootstrap bootstrap;

    private String host;

    private Integer port;

    public ClientWatchDog(Timer timer, Bootstrap bootstrap, String host, Integer port) {
        this.timer = timer;
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel server = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void run(Timeout timeout) throws Exception {

    }
}
