package org.example.common.handler.line;

import io.netty.channel.ChannelHandlerContext;
import org.example.common.model.RpcRequest;

public abstract class AbstractHandler {

    protected abstract boolean verify(byte messageType);

    protected abstract AbstractHandler nextHandler();

    protected abstract void processRequest(ChannelHandlerContext ctx, RpcRequest request);

    public void accept(ChannelHandlerContext ctx, RpcRequest request) {
        boolean verify = verify(request.rpcHeader().messageType());
        if (!verify) {
            nextHandler().accept(ctx, request);
            return;
        }
        processRequest(ctx, request);
    }
}
