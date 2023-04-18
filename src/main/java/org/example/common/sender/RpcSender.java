package org.example.common.sender;

import org.example.common.model.RpcRequest;

public interface RpcSender {

    void send(RpcRequest rpcRequest);
}
