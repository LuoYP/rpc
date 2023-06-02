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

/**
 * 模拟文件的输入流.
 *
 * <p>通过{@link org.example.server.io.RpcFile}或者{@link org.example.client.io.RpcFile}创建该输入流;
 * 通过{@code RpcFile}对象获取文件传输的通道与远程文件地址;
 * 每次传输1M的文件内容,并将其缓存在内存中,使用{@link ByteBuf}封装.
 * <p><pre class="code">
 *  RpcFileInputStream inputStream = new RpcFileInputStream(rpcFile);
 *  int temp;
 *  while((temp = inputStream.read()) != -1) {
 *      //READ
 *  }
 * </pre>
 */
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

    /**
     * 通过网络获取远程文件内容.
     *
     * <p>获取远程指定路径的文件内容,并将对应的内容写入到{@link ByteBuf}中;
     * 因为使用了byte[],可能导致255被赋值为-1,如果直接读取将导致流提前关闭,将其与{@code 0xFF}进行位运算;
     * 将每次获取文件内容的请求保存到{@link RpcContainer#TRANSFERRING_FILES}中,并阻塞等待文件内容返回,阻塞超时时间30S.
     *
     * @return 0~255,如果读到结尾返回-1.
     */
    @Override
    public int read() throws IOException {
        if (isFirst.get()) {
            loadPartFileFromRemote(0L);
            isFirst.compareAndSet(Boolean.TRUE, Boolean.FALSE);
        }
        if (fileMemoryCache.isReadable()) {
            return fileMemoryCache.readByte() & 0xFF;
        }
        long readIndex = readBytes.addAndGet(fileMemoryCache.readerIndex());
        fileMemoryCache.clear();
        loadPartFileFromRemote(readIndex);
        if (!fileMemoryCache.isReadable()) {
            return -1;
        }
        return fileMemoryCache.readByte() & 0xFF;
    }

    /**
     * 手动释放内存资源
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        fileMemoryCache.release();
        super.close();
    }

    private void loadPartFileFromRemote(Long readIndex) {
        RpcRequest request = new RpcRequest();
        var id = Constants.ID.getAndIncrement();
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.FILE_IN).setId(id));
        //filePath & readIndex
        request.setRpcContent(new RpcContent().setContent(new Object[]{fileAbsolutePath, readIndex}));
        channel.writeAndFlush(request);
        //阻塞等待第一段文件内容到来再开始读取
        EventExecutor eventExecutor = (EventExecutor) Factory.getBeanNotNull(EventExecutor.class);
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
