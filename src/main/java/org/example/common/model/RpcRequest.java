package org.example.common.model;

public class RpcRequest {

    private RpcLine rpcLine;

    private RpcHeader rpcHeader;

    private RpcContent rpcContent;

    public RpcLine rpcLine() {
        return rpcLine;
    }

    public RpcRequest setRpcLine(RpcLine rpcLine) {
        this.rpcLine = rpcLine;
        return this;
    }

    public RpcHeader rpcHeader() {
        return rpcHeader;
    }

    public RpcRequest setRpcHeader(RpcHeader rpcHeader) {
        this.rpcHeader = rpcHeader;
        return this;
    }

    public RpcContent rpcContent() {
        return rpcContent;
    }

    public RpcRequest setRpcContent(RpcContent rpcContent) {
        this.rpcContent = rpcContent;
        return this;
    }
}