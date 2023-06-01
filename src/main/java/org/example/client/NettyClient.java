package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.NetUtil;
import io.netty.util.Timer;
import org.example.client.handler.ClientHeartBeatHandler;
import org.example.client.handler.ClientMessageHandler;
import org.example.client.handler.ClientWatchDog;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.config.Configuration;
import org.example.common.constant.Protocol;
import org.example.common.context.Factory;
import org.example.common.handler.MessageDecoder;
import org.example.common.handler.MessageEncoder;
import org.example.common.handler.MessageHandler;
import org.example.common.handler.udp.UdpMessageDecoder;
import org.example.common.handler.udp.UdpMessageEncoder;
import org.example.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    protected Timer timer = new HashedWheelTimer();

    @Autowired
    private Configuration configuration;

    public void start(Protocol[] protocols) {
        Arrays.stream(protocols).forEach(protocol -> {
            switch (protocol) {
                case TCP -> startTcpClient();
                case UDP -> startUdpClient();
                default -> throw new RuntimeException("un support protocol: " + protocol);
            }
        });
    }

    private void startTcpClient() {
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

    private void startUdpClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        InetSocketAddress groupAddress = new InetSocketAddress(configuration.multicastHost(), configuration.udpPort());
        try {
            NetworkInterface ni = NetUtil.LOOPBACK_IF;
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new UdpMessageEncoder(groupAddress));
                            ch.pipeline().addLast(new UdpMessageDecoder());
                            ch.pipeline().addLast(new MessageHandler());
                        }
                    });
            //监听UDP端口,加入组
            NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind(groupAddress.getPort()).sync().channel();
            ChannelFuture future = channel.joinGroup(groupAddress, ni);
            future.addListener((ChannelFutureListener) future1 -> {
                LOGGER.info("join group success!");
            });
            Cookies cookies = (Cookies) Factory.getBean(Cookies.class);
            cookies.setUdpChannel(channel);;
            LOGGER.info("udp client is start!");
            channel.closeFuture().addListener((ChannelFutureListener) closeFuture -> {
                group.shutdownGracefully();
                LOGGER.info("udp client closed, release the thread-pool resource!");
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
