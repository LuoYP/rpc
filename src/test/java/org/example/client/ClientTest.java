package org.example.client;

import org.example.RpcClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.common.context.Factory;
import org.example.communication.client.api.TimeServer;

@RpcClientApplication(rpcApiPackages = "org.example.communication.client.api")
public class ClientTest {

    public static void main(String[] args) throws Exception{
        RpcClient.run(ClientTest.class);
//        Thread.sleep(20000);
//        TimeServer timeServer = (TimeServer)Factory.getBean(TimeServer.class);
//        System.out.println(timeServer.now());
    }
}
