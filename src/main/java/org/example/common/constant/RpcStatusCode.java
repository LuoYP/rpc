package org.example.common.constant;

public enum RpcStatusCode {

    OK(200, "success"),
    NOT_FOUND(404, "request resource not found"),

    TIMEOUT(408, "request timeout"),
    SERVER_ERROR(500, "system inner error"),
    ;


    private int code;

    private String msg;

    RpcStatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public RpcStatusCode setCode(int code) {
        this.code = code;
        return this;
    }

    public String msg() {
        return msg;
    }

    public RpcStatusCode setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
