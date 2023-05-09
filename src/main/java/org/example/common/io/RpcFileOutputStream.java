package org.example.common.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcContainer;
import org.example.common.context.Factory;
import org.example.common.model.RpcContent;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class RpcFileOutputStream extends OutputStream {

    private final Channel channel;

    private final String fileAbsolutePath;

    private final ByteBuf fileMemoryCache = Unpooled.buffer(1024 * 1024);

    public RpcFileOutputStream(AbstractRpcFile abstractRpcFile) {
        fileAbsolutePath = abstractRpcFile.fileAbsolutePath;
        channel = abstractRpcFile.getChannel();
    }

    @Override
    public void write(int b) throws IOException {
        if (fileMemoryCache.isWritable()) {
            fileMemoryCache.writeByte(b);
            return;
        }
        byte[] bytes = new byte[fileMemoryCache.readableBytes()];
        fileMemoryCache.readBytes(bytes);
        uploadPartFileToRemote(bytes);
        fileMemoryCache.clear().writeByte(b);
    }

    @Override
    public void close() throws IOException {
        byte[] bytes = new byte[fileMemoryCache.readableBytes()];
        fileMemoryCache.readBytes(bytes);
        uploadPartFileToRemote(bytes);
        super.close();
    }

    private void uploadPartFileToRemote(byte[] content) {
        RpcRequest request = new RpcRequest();
        var id = Constants.ID.getAndIncrement();
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_OUT).setId(id));
        //filePath & readIndex
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, content}));
        channel.writeAndFlush(request);
        //阻塞等待远程调用结束再写入文件内容到缓存
        EventExecutor eventExecutor = (EventExecutor) Factory.getBean(EventExecutor.class);
        Promise<Boolean> promise = new DefaultPromise<>(eventExecutor);
        RpcContainer.TRANSFERRING_FILES_OUT.put(id, promise);
        try {
            promise.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
