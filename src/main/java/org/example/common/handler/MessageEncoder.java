package org.example.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.common.utils.SerializeUtil;

@Sharable
public class MessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = SerializeUtil.writeToBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
