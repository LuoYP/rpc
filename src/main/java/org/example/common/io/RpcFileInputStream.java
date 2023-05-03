package org.example.common.io;

import io.netty.channel.Channel;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.model.RpcContent;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;
import org.example.common.utils.CharSequenceUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class RpcFileInputStream extends InputStream {

    private Channel channel;

    private String fileAbsolutePath;

    private final AtomicBoolean reading = new AtomicBoolean(Boolean.TRUE);

    public RpcFileInputStream(Channel channel, String fileAbsolutePath) {
        this.channel = channel;
        this.fileAbsolutePath = fileAbsolutePath;
    }

    @Override
    public int read() throws IOException {

        //打开文件连接,请求远程文件输出流
        RpcRequest request = new RpcRequest();
        var id = Long.valueOf(Constants.ID.getAndIncrement());
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_OUT).setId(id));
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, 0}));
        channel.writeAndFlush(request);
        String cacheKey = CharSequenceUtil.concatWithSeparator("-", fileAbsolutePath, id.toString());
        byte[] filePart = FileCache.cache.remove(cacheKey);
        var byteArrayInputStream = new ByteArrayInputStream(filePart);
        return byteArrayInputStream.read();
    }

    @Override
    public void close() throws IOException {
        reading.set(Boolean.FALSE);
    }

    public AtomicBoolean reading() {
        return reading;
    }
}
