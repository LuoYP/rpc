package org.example.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.example.client.Cookies;
import org.example.common.context.Factory;

import java.util.concurrent.TimeUnit;

/**
 * 看门狗,帮助客户端断线重连
 * 使用Netty的时间轮执行重连任务
 */
@Sharable
public class ClientWatchDog extends ChannelInboundHandlerAdapter implements TimerTask {

    private Timer timer;

    private final Bootstrap bootstrap;

    private String host;

    private Integer port;

    private Integer reconnectTimes;

    private int retryCount = 0;

    public ClientWatchDog(Timer timer, Bootstrap bootstrap, String host, Integer port, Integer reconnectTimes) {
        this.timer = timer;
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
        this.reconnectTimes = reconnectTimes;
    }

    /**
     * 连接成功后需要缓存一下Channel方便客户端主动调用服务端
     * 将retryCount清零
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel server = ctx.channel();
        Cookies cookies = (Cookies) Factory.getBean(Cookies.class);
        cookies.setServer(server);
        retryCount = 0;
        super.channelActive(ctx);
    }

    /**
     * 使用时间轮定时重连
     * 不能一直重连，限制重连次数和重连的时间间隔,时间间隔呈指数增长
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Cookies cookies = (Cookies) Factory.getBean(Cookies.class);
        cookies.setServer(null);
        if (retryCount < reconnectTimes) {
            retryCount++;
            int timeout = 1 << retryCount;
            timer.newTimeout(this, timeout, TimeUnit.SECONDS);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future = bootstrap.connect(host, port);
        future.sync();
        future.channel().closeFuture().sync();
    }
}
