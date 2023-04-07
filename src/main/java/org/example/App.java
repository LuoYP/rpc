package org.example;

import org.example.config.Configuration;
import org.example.netty.NettyServer;

public class App {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        NettyServer nettyServer = new NettyServer(configuration);
        nettyServer.start();
    }
}
