package org.example.common.handler.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.common.handler.Filter;
import org.example.common.utils.SerializeUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

public class UdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private Filter filter;

    public UdpMessageDecoder() {
    }

    public UdpMessageDecoder(Filter filter) {
        this.filter = filter;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        if (Objects.nonNull(filter)) {
            InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            String hostAddress = socketAddress.getAddress().getHostAddress();
            if (!filter.accept(hostAddress)) {
                return;
            }
        }
        ByteBuf in = msg.content();
        int length = in.readableBytes();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        out.add(SerializeUtil.readFromBytes(bytes));
    }
}
