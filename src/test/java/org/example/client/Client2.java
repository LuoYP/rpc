package org.example.client;

import org.example.RpcClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.common.constant.Protocol;

@RpcClientApplication(rpcApiPackages = "org.example.communication.client.api", protocols = {Protocol.UDP})
public class Client2 {

    public static void main(String[] args) throws Exception {
        RpcClient.run(Client2.class);
    }
}
