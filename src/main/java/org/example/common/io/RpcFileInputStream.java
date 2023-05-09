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
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RpcFileInputStream extends InputStream {

    private final Channel channel;

    private final String fileAbsolutePath;

    private final ByteBuf fileMemoryCache = Unpooled.buffer(1024 * 1024);

    private final AtomicBoolean isFirst = new AtomicBoolean(Boolean.TRUE);

    private final AtomicLong readBytes = new AtomicLong(0);

    public RpcFileInputStream(AbstractRpcFile rpcFile) {
        this.channel = rpcFile.getChannel();
        this.fileAbsolutePath = rpcFile.fileAbsolutePath;
    }

    @Override
    public int read() throws IOException {
        if (isFirst.get()) {
            loadPartFileFromRemote(fileAbsolutePath, 0L);
            isFirst.compareAndSet(Boolean.TRUE, Boolean.FALSE);
        }
        if (fileMemoryCache.isReadable()) {
            return fileMemoryCache.readByte() & 0xFF;
        }
        long readIndex = readBytes.addAndGet(fileMemoryCache.readerIndex());
        fileMemoryCache.clear();
        loadPartFileFromRemote(fileAbsolutePath, readIndex);
        if (!fileMemoryCache.isReadable()) {
            return -1;
        }
        return fileMemoryCache.readByte() & 0xFF;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    private void loadPartFileFromRemote(String fileAbsolutePath, Long readIndex) {
        RpcRequest request = new RpcRequest();
        var id = Constants.ID.getAndIncrement();
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_OUT).setId(id));
        //filePath & readIndex
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, readIndex}));
        channel.writeAndFlush(request);
        //阻塞等待第一段文件内容到来再开始读取
        EventExecutor eventExecutor = (EventExecutor) Factory.getBean(EventExecutor.class);
        Promise<byte[]> promise = new DefaultPromise<>(eventExecutor);
        RpcContainer.TRANSFERRING_FILES.put(id, promise);
        try {
            byte[] filePartContent = promise.get(30, TimeUnit.SECONDS);
            fileMemoryCache.clear().writeBytes(filePartContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
