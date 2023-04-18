package org.example.common.model;

import org.example.common.constant.RpcStatusCode;

public class RpcResponse {

    /** 状态码 */
    private RpcStatusCode code;

    private RpcHeader rpcHeader;

    private Object content;

    public RpcStatusCode code() {
        return code;
    }

    public RpcResponse setCode(RpcStatusCode code) {
        this.code = code;
        return this;
    }

    public RpcHeader rpcHeader() {
        return rpcHeader;
    }

    public RpcResponse setRpcHeader(RpcHeader rpcHeader) {
        this.rpcHeader = rpcHeader;
        return this;
    }

    public Object content() {
        return content;
    }

    public RpcResponse setContent(Object content) {
        this.content = content;
        return this;
    }
}
