package org.example.model;

import java.io.Serializable;
import java.util.List;

public class RpcMessage implements Serializable {

    public RpcMessage(){}

    private String className;

    private String methodName;

    private List<Class<?>> argsType;

    private List<Object> args;

    public String className() {
        return className;
    }

    public RpcMessage setClassName(String className) {
        this.className = className;
        return this;
    }

    public String methodName() {
        return methodName;
    }

    public RpcMessage setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public List<Class<?>> argsType() {
        return argsType;
    }

    public RpcMessage setArgsType(List<Class<?>> argsType) {
        this.argsType = argsType;
        return this;
    }

    public List<Object> args() {
        return args;
    }

    public RpcMessage setArgs(List<Object> args) {
        this.args = args;
        return this;
    }
}
