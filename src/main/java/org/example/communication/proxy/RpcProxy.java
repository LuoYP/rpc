package org.example.communication.proxy;

import org.example.utils.CharSequenceUtil;

import java.lang.reflect.Proxy;

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
                assert !CharSequenceUtil.isBlank(remote) : "remote ip is empty";
                System.out.println("我是代理实现的");
                return "hello";
            }
        });
        return (T) proxy;
    }
}
