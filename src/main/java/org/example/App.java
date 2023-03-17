package org.example;

import org.example.communication.interfaces.RuntimeInfoService;
import org.example.communication.proxy.RpcProxy;

public class App {
    public static void main(String[] args) throws Exception {
        RpcProxy<RuntimeInfoService> rpcProxy = new RpcProxy<>(RuntimeInfoService.class);
        RuntimeInfoService proxy = rpcProxy.getProxy();
        String s = proxy.sayHello("");
        System.out.println(s);
        System.out.println(proxy);
    }
}
