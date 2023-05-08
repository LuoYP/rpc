package org.example.client.io;

import io.netty.channel.Channel;
import org.example.client.Cookies;
import org.example.common.context.Factory;
import org.example.common.io.AbstractRpcFile;
import org.example.common.io.RpcFileInputStream;

public class RpcFile extends AbstractRpcFile {

    public RpcFile(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        Cookies cookies = (Cookies) Factory.getBean(Cookies.class);
        Channel channel = cookies.server();
        inputStream = new RpcFileInputStream(channel, fileAbsolutePath);
    }
}
