package org.example.client;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.example.common.annotation.Component;

@Component
public class Cookies {

    private Channel server;

    private NioDatagramChannel udpChannel;

    public Channel server() {
        return server;
    }

    public Cookies setServer(Channel server) {
        this.server = server;
        return this;
    }

    public NioDatagramChannel udpChannel() {
        return udpChannel;
    }

    public Cookies setUdpChannel(NioDatagramChannel udpChannel) {
        this.udpChannel = udpChannel;
        return this;
    }
}
