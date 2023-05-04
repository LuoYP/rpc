package org.example.common.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcContainer;
import org.example.common.model.RpcContent;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class RpcFileInputStream extends InputStream {

    private final RpcFile source;

    private Channel channel;

    private String fileAbsolutePath;

    private final AtomicBoolean reading = new AtomicBoolean(Boolean.TRUE);

    private ByteBuf fileMemoryCache = Unpooled.buffer(1024 * 1024);

    private final AtomicBoolean isFirst = new AtomicBoolean(Boolean.TRUE);

    private final AtomicBoolean isFinish = new AtomicBoolean(Boolean.FALSE);

    private final AtomicBoolean canWrite = new AtomicBoolean(Boolean.TRUE);

    public RpcFileInputStream(Channel channel, RpcFile source) {
        this.channel = channel;
        this.source = source;

    }

    @Override
    public int read() throws IOException {

        if (isFirst.get()) {
            //打开文件连接,请求远程文件输出流
            RpcRequest request = new RpcRequest();
            id = Constants.ID.getAndIncrement();
            request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_OUT).setId(id));
            //filePath & readSize
            request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, 0}));
            channel.writeAndFlush(request);
            //阻塞等待第一段文件内容到来再开始读取
            isFirst.compareAndSet(Boolean.TRUE, Boolean.FALSE);
        }
        if (fileMemoryCache.isReadable()) {
            return fileMemoryCache.readInt();
        }
        canWrite.compareAndSet(Boolean.FALSE, Boolean.TRUE);
        synchronized (RpcContainer.TRANSFERRING_FILES.get(id)) {
            try {
                RpcContainer.TRANSFERRING_FILES.get(id).wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return fileMemoryCache.readInt();
    }

    @Override
    public void close() throws IOException {
        reading.set(Boolean.FALSE);
    }

    public AtomicBoolean reading() {
        return reading;
    }

    public ByteBuf fileMemoryCache() {
        return fileMemoryCache;
    }

    public AtomicBoolean isFinish() {
        return isFinish;
    }

    public AtomicBoolean canWrite() {
        return canWrite;
    }

    private void loadPartFileFromRemote(String fileAbsolutePath, Long readIndex) {
        RpcRequest request = new RpcRequest();
        var id = Constants.ID.getAndIncrement();
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_OUT).setId(id));
        //filePath & readIndex
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, readIndex}));
        channel.writeAndFlush(request);
        //阻塞等待第一段文件内容到来再开始读取
        Promise<byte[]> promise = new DefaultPromise<>();
        isFirst.compareAndSet(Boolean.TRUE, Boolean.FALSE);
    }
}
