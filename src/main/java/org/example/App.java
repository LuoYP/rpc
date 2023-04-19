package org.example;

import org.example.common.config.Configuration;
import org.example.common.proxy.RpcProxy;
import org.example.common.sender.RpcSender;
import org.example.communication.interfaces.RuntimeInfoService;
import org.example.server.NettyServer;

public class App {
    public static void main(String[] args) throws Exception{

        new Thread(() -> {
            Configuration configuration = new Configuration();
            NettyServer nettyServer = new NettyServer(configuration);
            nettyServer.start();
        }).start();
        Thread.sleep(10000);
        RpcProxy<RuntimeInfoService> proxy = new RpcProxy<>(RuntimeInfoService.class);
        RuntimeInfoService runtimeInfoService = proxy.getProxy(new RpcSender());
        String result = runtimeInfoService.sayHello("172.24.108.59");
        System.out.println(result);
    }
}
