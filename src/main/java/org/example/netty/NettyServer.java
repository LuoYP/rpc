package org.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.config.Configuration;
import org.example.netty.handler.ServerHeartBeatHandler;

import java.util.concurrent.TimeUnit;

public class NettyServer {

    private Configuration configuration;

    public NettyServer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //客户端连接请求等待队列
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //允许服务器在该端口上启动多个实例，使用不同的本地IP
                    .option(ChannelOption.SO_REUSEADDR, true)
                    //将小数据包打包发出
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //为netty服务器添加处理器完成对消息的简单处理
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ServerHeartBeatHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(configuration.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {

        }
    }
}
