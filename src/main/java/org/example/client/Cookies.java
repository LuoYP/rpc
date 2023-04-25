package org.example.client;

import io.netty.channel.Channel;
import org.example.common.annotation.Component;

@Component
public class Cookies {

    private Channel server;

    public Channel server() {
        return server;
    }

    public Cookies setServer(Channel server) {
        this.server = server;
        return this;
    }
}
