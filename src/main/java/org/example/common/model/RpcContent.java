package org.example.common.model;

/**
 * RPC协议正文
 * 封装请求参与与响应结果
 */
public class RpcContent {

    private Object[] content;

    public Object[] content() {
        return content;
    }

    public RpcContent setContent(Object[] content) {
        this.content = content;
        return this;
    }
}
