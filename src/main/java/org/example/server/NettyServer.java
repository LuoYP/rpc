package org.example.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NetUtil;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.config.Configuration;
import org.example.common.constant.Protocol;
import org.example.common.handler.MessageDecoder;
import org.example.common.handler.MessageEncoder;
import org.example.server.handler.ServerHeartBeatHandler;
import org.example.server.handler.ServerMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private Configuration configuration;

    public void start(Protocol[] protocols) {
        Arrays.stream(protocols).forEach(protocol -> {
            switch (protocol) {
                case TCP -> startTcpServer();
                case UDP -> startUdpServer();
                default -> throw new RuntimeException("un support protocol: " + protocol);
            }
        });
    }

    private void startTcpServer() {
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
                    //写缓存的高低水位线,通过该设置进行流量控制
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(configuration.lowWaterMark(), configuration.highWaterMark()))
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
            ChannelFuture future = bootstrap.bind(configuration.host(), configuration.tcpPort());
            LOGGER.info("tcp server is starting!");
            future.channel().closeFuture().addListener((ChannelFutureListener) closeFuture -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                LOGGER.info("tcp server closed, release the thread-pool resource!");
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startUdpServer() {
        EventLoopGroup group = new NioEventLoopGroup();
        InetSocketAddress groupAddress = new InetSocketAddress(configuration.multicastHost(), configuration.udpPort());
        try {
            NetworkInterface ni = NetUtil.LOOPBACK_IF;
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new MessageDecoder());
                        }
                    });
            //监听UDP端口,加入组
            NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind(groupAddress.getPort()).sync().channel();
            ChannelFuture future = channel.joinGroup(groupAddress, ni);
            future.addListener((ChannelFutureListener) future1 -> {
                LOGGER.info("join group success!");
            });
            Session.putUdpChannel(channel);
            LOGGER.info("udp server is start!");
            channel.closeFuture().addListener((ChannelFutureListener) closeFuture -> {
                group.shutdownGracefully();
                Session.removeUdpChannel();
                LOGGER.info("udp server closed, release the thread-pool resource!");
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
