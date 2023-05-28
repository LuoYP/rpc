package org.example.common.model;

/**
 * RPC协议正文
 * 封装请求参与与响应结果
 */
public class RpcContent {

    private Object[] content;

    private byte[] binaryContent;

    public Object[] content() {
        return content;
    }

    public RpcContent setContent(Object[] content) {
        this.content = content;
        return this;
    }

    public byte[] binaryContent() {
        return binaryContent;
    }

    public RpcContent setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
        return this;
    }
}
