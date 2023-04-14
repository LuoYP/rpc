package org.example.common.model;

public class RpcResponse {

    /** 状态码 */
    private RpcStatusCode code;

    private RpcHeader rpcHeader;

    private RpcContent rpcContent;

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

    public RpcContent rpcContent() {
        return rpcContent;
    }

    public RpcResponse setRpcContent(RpcContent rpcContent) {
        this.rpcContent = rpcContent;
        return this;
    }
}

enum RpcStatusCode {

    OK(200),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    ;


    private int code;

    RpcStatusCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public RpcStatusCode setCode(int code) {
        this.code = code;
        return this;
    }
}
