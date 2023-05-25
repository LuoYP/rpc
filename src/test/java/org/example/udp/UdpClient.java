package org.example.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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

public class UdpClient {

    public static void main(String[] args) {
        // 组播地址
        InetSocketAddress groupAddress = new InetSocketAddress("224.2.1.1", 9000);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            NetworkInterface ni = NetUtil.LOOPBACK_IF;

            Bootstrap b = new Bootstrap();
            // 设置Channel
            b.group(group).channel(NioDatagramChannel.class)
                    // 设置Option 组播
                    .option(ChannelOption.IP_MULTICAST_IF, ni)
                    // 设置Option 单机测试设置端口可重复绑定
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 设置Handler
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
                                        throws Exception {
                                    // 打印一句话
                                    System.out.println(msg.content().toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            // 获取Channel
            Channel ch = b.bind(groupAddress.getPort()).sync().channel();
            // 往组播地址中发送数据报
            ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8), groupAddress)).sync();// 发送数据
            // 关闭Channel
            ch.close().awaitUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
