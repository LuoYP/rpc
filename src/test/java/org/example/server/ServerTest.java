package org.example.server;

import org.example.RpcServer;
import org.example.communication.server.api.SystemInfoService;
import org.example.server.annotation.RpcServerApplication;
import org.example.common.context.Factory;

@RpcServerApplication(rpcApiPackages = {"org.example.communication.server.api"})
public class ServerTest {

    public static void main(String[] args) throws Exception{
        RpcServer.run(ServerTest.class);
        SystemInfoService proxy = (SystemInfoService)Factory.BEAN_WAREHOUSE.get(SystemInfoService.class);
        Thread.sleep(15000);
        String result = proxy.sayHello("127.0.0.1");
        System.out.println(result);
    }
}
