package org.example.common.io;

import io.netty.channel.Channel;
import org.example.common.constant.Constants;
import org.example.common.model.RpcContent;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RpcFileInputStream extends InputStream {

    private Channel channel;

    private String fileAbsolutePath;

    public RpcFileInputStream(Channel channel, String fileAbsolutePath) {
        this.channel = channel;
        this.fileAbsolutePath = fileAbsolutePath;
    }

    @Override
    public int read() throws IOException {

        //打开文件连接
        RpcRequest request = new RpcRequest();
        request.setRpcHeader(new RpcHeader().setMessageType(Constants.FILE));
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath}));
        channel.writeAndFlush(request);


        var byteArrayInputStream = new ByteArrayInputStream(new byte[]{});
        return byteArrayInputStream.read();
    }
}
