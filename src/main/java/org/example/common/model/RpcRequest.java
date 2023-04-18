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

    public RpcRequest buildRpcLine(String className, String methodName, Class<?>[] parameterTypes) {
        RpcLine _rpcLine = new RpcLine();
        return this.setRpcLine(_rpcLine.setClassName(className).setMethodName(methodName).setParameterTypes(parameterTypes));
    }

    public RpcRequest buildRpcHeader(long id, byte messageType, String authorization) {
        RpcHeader _rpcHeader = new RpcHeader();
        return this.setRpcHeader(_rpcHeader.setId(id).setMessageType(messageType).setAuthorization(authorization));
    }

    public RpcRequest buildRpcContent(Object[] parameters) {
        RpcContent _rpcContent = new RpcContent();
        return this.setRpcContent(_rpcContent.setContent(parameters));
    }
}