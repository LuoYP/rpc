package org.example.server.io;

import io.netty.channel.Channel;
import org.example.common.io.AbstractRpcFile;
import org.example.server.Session;

public class RpcFile extends AbstractRpcFile {

    private String ip;

    public RpcFile(String ip, String fileAbsolutePath) {
        this.ip = ip;
        this.fileAbsolutePath = fileAbsolutePath;
    }

    @Override
    public Channel getChannel() {
        return Session.ACTIVE_CHANNEL.get(ip);
    }

    public String ip() {
        return ip;
    }

    public RpcFile setIp(String ip) {
        this.ip = ip;
        return this;
    }
}
