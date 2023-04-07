package org.example.communication.proxy;

import io.netty.channel.Channel;
import org.example.model.RpcMessage;
import org.example.netty.handler.MessageHandler;
import org.example.utils.CharSequenceUtil;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RpcProxy<T> {

    private Class<T> target;


    public RpcProxy(Class<T> target) {
        this.target = target;
    }

    public T getProxy() {
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
                Channel channel = MessageHandler.ONLINE.get(remote);
                if (Objects.isNull(channel)) {
                    throw new RuntimeException("remote is off-line");
                }
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setClassName(target.getName());
                rpcMessage.setMethodName(method.getName());
                List<Class<?>> argsType = new ArrayList<>();
                List<Object> argList = new ArrayList<>();
                for (Object arg : args) {
                    argsType.add(arg.getClass());
                    argList.add(arg);
                }
                rpcMessage.setArgsType(argsType);
                rpcMessage.setArgs(argList);
                channel.write(rpcMessage);
                return null;
            }
        });
        return (T) proxy;
    }
}
