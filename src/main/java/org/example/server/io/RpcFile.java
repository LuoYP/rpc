package org.example.server.io;

import io.netty.channel.Channel;
import org.example.common.io.AbstractRpcFile;
import org.example.common.io.RpcFileInputStream;
import org.example.server.Session;

public class RpcFile extends AbstractRpcFile {

    public RpcFile(String ip, String fileAbsolutePath) {
        this.ip = ip;
        this.fileAbsolutePath = fileAbsolutePath;
        Channel channel = Session.ACTIVE_CHANNEL.get(ip);
        inputStream = new RpcFileInputStream(channel, fileAbsolutePath);
    }
}
