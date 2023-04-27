package org.example.server;

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
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.config.Configuration;
import org.example.common.handler.MessageDecoder;
import org.example.common.handler.MessageEncoder;
import org.example.server.handler.ServerHeartBeatHandler;
import org.example.server.handler.ServerMessageHandler;

import java.util.concurrent.TimeUnit;

@Component
public class NettyServer {

    @Autowired
    private Configuration configuration;

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
                            ch.pipeline().addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ServerHeartBeatHandler());
                            //长度用int就可以表示了，所以长度域取4个字节
                            //长度域不是业务消息的内容，消息实际内容忽略长度域的4个字节
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new ServerMessageHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(configuration.host(), configuration.port()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
