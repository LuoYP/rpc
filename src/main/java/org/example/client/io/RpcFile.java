package org.example.client.io;

import io.netty.channel.Channel;
import org.example.client.Cookies;
import org.example.common.context.Factory;
import org.example.common.io.AbstractRpcFile;

public class RpcFile extends AbstractRpcFile {

    public RpcFile(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    @Override
    protected Channel getChannel() {
        Cookies cookies = (Cookies) Factory.getBeanNotNull(Cookies.class);
        return cookies.server();
    }
}
