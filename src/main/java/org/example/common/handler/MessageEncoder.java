package org.example.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.common.model.RpcMessage_old;
import org.example.common.utils.SerializeUtil;

@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<RpcMessage_old> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage_old msg, ByteBuf out) throws Exception {
        byte[] bytes = SerializeUtil.writeToBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
