package org.example.client.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.example.common.constant.MessageType;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;

@Sharable
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    /**
     * 空闲状态监听器,如果5秒没有写事件发生就往通道中写一个心跳消息
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state.equals(IdleState.WRITER_IDLE)) {
                RpcHeader rpcHeader = new RpcHeader().setMessageType(MessageType.HEART_BEAT);
                RpcRequest rpcRequest = new RpcRequest().setRpcHeader(rpcHeader);
                ctx.channel().writeAndFlush(rpcRequest);
            }
        }
    }
}
