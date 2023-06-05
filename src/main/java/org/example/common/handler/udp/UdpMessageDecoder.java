package org.example.common.handler.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.common.utils.SerializeUtil;

import java.util.List;

public class UdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    public UdpMessageDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf in = msg.content();
        int length = in.readableBytes();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        out.add(SerializeUtil.readFromBytes(bytes));
    }
}
