package org.example.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收到超时空闲的通知就关闭连接,以免占用资源
     *
     * @param ctx 通道上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state.equals(IdleState.ALL_IDLE)) {
                ctx.close();
            }
        }
    }
}
