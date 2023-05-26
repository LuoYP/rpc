package org.example.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UdpServer {

    public static void main(String[] args) {
        // 组播地址
        InetSocketAddress groupAddress = new InetSocketAddress("224.2.1.1", 9000);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            NetworkInterface ni = NetUtil.LOOPBACK_IF;
            Bootstrap bootstrap = new Bootstrap();
            //设置NioDatagramChannel
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    //单机测试设置端口可复用
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ChannelPipeline addLast = ch.pipeline().addLast();
                            addLast.addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                    System.out.println(msg.sender() + " >>> " + msg.content().toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            //获取NioDatagramChannel
            NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind(groupAddress.getPort()).sync().channel();
            //加入组
            ChannelFuture future = channel.joinGroup(groupAddress, ni);
            future.addListener((ChannelFutureListener) future1 -> {
                System.out.println("join group success!");
            });
            //关闭Channel
            channel.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();//优雅退出
        }
    }
}
