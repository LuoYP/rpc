package org.example.common.model;

import java.io.Serializable;
import java.util.List;

public class RpcMessage_old implements Serializable {

    public RpcMessage_old(){}

    private String className;

    private String methodName;

    private List<Class<?>> argsType;

    private List<Object> args;

    public String className() {
        return className;
    }

    public RpcMessage_old setClassName(String className) {
        this.className = className;
        return this;
    }

    public String methodName() {
        return methodName;
    }

    public RpcMessage_old setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public List<Class<?>> argsType() {
        return argsType;
    }

    public RpcMessage_old setArgsType(List<Class<?>> argsType) {
        this.argsType = argsType;
        return this;
    }

    public List<Object> args() {
        return args;
    }

    public RpcMessage_old setArgs(List<Object> args) {
        this.args = args;
        return this;
    }
}
