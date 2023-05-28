package org.example.common.handler.line;

import io.netty.channel.ChannelHandlerContext;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.model.RpcRequest;

@Component
public class RpcRequestHandler extends AbstractHandler{

    @Autowired
    private HeartBeatHandler nextHandler;

    @Override
    protected boolean verify(byte messageType) {
        return false;
    }

    @Override
    protected AbstractHandler nextHandler() {
        return nextHandler;
    }

    @Override
    protected void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
        //do nothing
    }
}
