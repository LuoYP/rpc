package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.example.client.handler.ClientHeartBeatHandler;
import org.example.client.handler.ClientMessageHandler;
import org.example.client.handler.ClientWatchDog;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.config.Configuration;
import org.example.common.handler.MessageDecoder;
import org.example.common.handler.MessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    protected Timer timer = new HashedWheelTimer();

    @Autowired
    private Configuration configuration;

    public void start() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                //将小数据包打包发出
                .option(ChannelOption.TCP_NODELAY, true)
                //保持长连接
                .option(ChannelOption.SO_KEEPALIVE, true)
                //写缓存区的高低水位线,通过该设置进行流量控制
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(configuration.lowWaterMark(), configuration.highWaterMark()))
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ClientWatchDog(timer, bootstrap, configuration.host(), configuration.tcpPort(), configuration.reconnectTimes()));
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ClientHeartBeatHandler());
                        //长度用int就可以表示了，所以长度域取4个字节
                        //长度域不是业务消息的内容，消息实际内容忽略长度域的4个字节
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast(new MessageEncoder());
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new ClientMessageHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(configuration.host(), configuration.tcpPort());
            future.sync();
            LOGGER.info("client start!");
            future.channel().closeFuture().sync();
            LOGGER.info("client closed!");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
