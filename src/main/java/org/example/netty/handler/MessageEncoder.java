package org.example.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.model.RpcMessage;
import org.example.utils.SerializeUtil;

@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        byte[] bytes = SerializeUtil.writeToBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
