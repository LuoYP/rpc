package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.common.config.Configuration;
import org.example.common.handler.MessageDecoder;
import org.example.common.handler.MessageEncoder;
import org.example.common.handler.MessageHandler;
import org.example.server.handler.ServerHeartBeatHandler;

import java.util.concurrent.TimeUnit;

public class NettyClient {
    private Configuration configuration;

    public NettyClient(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    //将小数据包打包发出
                    .option(ChannelOption.TCP_NODELAY, true)
                    //保持长连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //为netty服务器添加处理器完成对消息的简单处理
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ServerHeartBeatHandler());
                            //长度用int就可以表示了，所以长度域取4个字节
                            //长度域不是业务消息的内容，消息实际内容忽略长度域的4个字节
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new MessageHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(configuration.host(), configuration.port()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
