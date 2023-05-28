package org.example.common.handler.line;

import io.netty.channel.ChannelHandlerContext;
import org.example.common.annotation.Component;
import org.example.common.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DefaultHandler extends  AbstractHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

    @Override
    protected boolean verify(byte messageType) {
        return true;
    }

    @Override
    protected AbstractHandler nextHandler() {
        return null;
    }

    @Override
    protected void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
        LOGGER.info("accept a un support type message!");
    }
}
