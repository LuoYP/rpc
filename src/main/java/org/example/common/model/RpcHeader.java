package org.example.common.model;

/**
 * RPC协议请求头
 */
public class RpcHeader {

    /** 一次会话的唯一标识，由于Netty异步通讯，用来匹配请求与响应 */
    private long id;

    /** 消息类型 */
    private byte messageType;

    /** 认证信息 */
    private String authorization;


    public long id() {
        return id;
    }

    public RpcHeader setId(long id) {
        this.id = id;
        return this;
    }

    public byte messageType() {
        return messageType;
    }

    public RpcHeader setMessageType(byte messageType) {
        this.messageType = messageType;
        return this;
    }

    public String authorization() {
        return authorization;
    }

    public RpcHeader setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }
}
