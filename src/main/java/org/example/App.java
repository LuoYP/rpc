package org.example;

import org.example.common.config.Configuration;
import org.example.common.utils.ClassUtil;
import org.example.server.NettyServer;

public class App {
    public static void main(String[] args) throws Exception {

        Class<?> implementClass = ClassUtil.getImplementClass("org.example.communication.interfaces.RuntimeInfoService");
        System.out.println(implementClass.getName());

        Configuration configuration = new Configuration();
        NettyServer nettyServer = new NettyServer(configuration);
        nettyServer.start();
    }
}
