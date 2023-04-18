package org.example.common.model;

/**
 * 自定义RPC协议的请求行
 * 主要描述RPC请求的目标地址与协议版本
 */
public class RpcLine {

    /** 请求类型 */
    private String className;

    /** 请求方法 */
    private String methodName;

    /** 请求方法参数列表，与方法名构成方法签名，唯一确定目标方法 */
    private Class<?>[] parameterTypes;

    /** 协议版本 */
    private String version;

    public String className() {
        return className;
    }

    public RpcLine setClassName(String className) {
        this.className = className;
        return this;
    }

    public String methodName() {
        return methodName;
    }

    public RpcLine setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Class<?>[] parameterTypes() {
        return parameterTypes;
    }

    public RpcLine setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public String version() {
        return version;
    }

    public RpcLine setVersion(String version) {
        this.version = version;
        return this;
    }
}
