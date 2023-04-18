package org.example.common.sender;

import io.netty.channel.Channel;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;

public interface RpcSender {

    RpcResponse send(RpcRequest rpcRequest, Channel channel);
}
