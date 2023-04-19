package org.example.common.proxy;

import io.netty.channel.Channel;
import org.example.common.constant.Constants;
import org.example.common.constant.RpcStatusCode;
import org.example.common.model.RpcFile;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.example.common.utils.CharSequenceUtil;
import org.example.server.Session;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * RPC通讯接口的代理实现
 * 通过Netty远程调用目标方法
 *
 * @param <T>
 */
public class RpcProxy<T> {

    private Class<T> target;


    public RpcProxy(Class<T> target) {
        this.target = target;
    }

    public T getProxy(RpcSender sender) {
        Object proxy;
        ClassLoader classLoader = target.getClassLoader();
        Class<?>[] interfaces = new Class[]{target};
        proxy = Proxy.newProxyInstance(classLoader, interfaces, (proxyInner, method, args) -> {
            //Object方法直接调用this
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                //通过netty发起远程过程调用
                String remote = (String) args[0];
                if (CharSequenceUtil.isEmpty(remote)) {
                    throw new RuntimeException("remote IP is empty");
                }
                Channel channel = Session.ACTIVE_CHANNEL.get(remote);
                if (Objects.isNull(channel)) {
                    throw new RuntimeException("remote is off-line");
                }
                //构造RPC请求
                String className = target.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                long id = Constants.ID.getAndIncrement();
                byte messageType = method.getReturnType().equals(RpcFile.class) ? Constants.FILE : Constants.COMMENT;
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.buildRpcLine(className, methodName, parameterTypes)
                        .buildRpcHeader(id, messageType, null)
                        .buildRpcContent(args);
                //发送请求
                RpcResponse rpcResponse = sender.send(rpcRequest, channel);
                RpcStatusCode status = rpcResponse.code();
                if (RpcStatusCode.OK.equals(status)) {
                    return rpcResponse.content();
                }
                throw new RuntimeException(status.msg());
            }
        });
        return (T) proxy;
    }
}
