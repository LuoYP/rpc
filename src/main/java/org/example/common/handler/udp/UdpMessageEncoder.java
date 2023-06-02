package org.example.common.handler.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.example.common.model.RpcRequest;
import org.example.common.utils.SerializeUtil;

import java.net.InetSocketAddress;
import java.util.List;

@Sharable
public class UdpMessageEncoder extends MessageToMessageEncoder<RpcRequest> {

    private InetSocketAddress groupAddress;

    public UdpMessageEncoder(InetSocketAddress groupAddress) {
        this.groupAddress = groupAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, List<Object> out) throws Exception {
        byte[] bytes = SerializeUtil.writeToBytes(msg);
        ByteBuf rpcMessageBytes = Unpooled.copiedBuffer(bytes);
        out.add(new DatagramPacket(rpcMessageBytes, groupAddress));
    }
}
