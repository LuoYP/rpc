package org.example.common.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileMode;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcContainer;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcLine;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Objects;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("receive a rpc message!");
        switch (msg) {
            case RpcRequest request -> processRequest(ctx, request);
            case RpcResponse response -> processResponse(response);
            default -> {
            }
        }
    }

    private void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
        var messageType = request.rpcHeader().messageType();
        switch (messageType) {
            case MessageType.HEART_BEAT -> processHeatBeatRequest();
            case MessageType.COMMENT -> processCommonRequest(ctx, request);
            case MessageType.FILE_IN -> processFileInRequest(ctx, request);
            case MessageType.FILE_OUT -> processFileOutRequest(ctx, request);
            default -> {
            }
        }
    }

    private void processResponse(RpcResponse response) {
        var messageType = response.rpcHeader().messageType();
        switch (messageType) {
            case MessageType.FILE_IN -> {
                long id = response.rpcHeader().id();
                Promise<byte[]> promise = RpcContainer.TRANSFERRING_FILES.get(id);
                if (Objects.nonNull(promise)) {
                    promise.setSuccess((byte[]) response.content());
                }
            }
            case MessageType.FILE_OUT -> {
                long id = response.rpcHeader().id();
                Promise<Boolean> promise = RpcContainer.TRANSFERRING_FILES_OUT.get(id);
                if (Objects.nonNull(promise)) {
                    promise.setSuccess((Boolean) response.content());
                }
            }
            default -> {
                var rpcResponsePromise = RpcContainer.RPC_RESPONSE.get(response.rpcHeader().id());
                //超时会删除RPC_RESPONSE中对应的key，promise可能不存在
                if (Objects.nonNull(rpcResponsePromise)) {
                    rpcResponsePromise.setSuccess(response);
                }
            }
        }

    }

    private void processHeatBeatRequest() {
        //do nothing
    }

    private void processCommonRequest(ChannelHandlerContext ctx, RpcRequest request) {
        var rpcResponse = new RpcResponse();
        rpcResponse.setRpcHeader(request.rpcHeader()).setCode(RpcStatusCode.OK);

        RpcLine rpcLine = request.rpcLine();
        Class<?> requestClass = ClassUtil.loadClass(rpcLine.className());
        String requestMethod = rpcLine.methodName();
        Class<?>[] parameterTypes = rpcLine.parameterTypes();
        Object[] parameters = request.rpcContent().content();

        //获取实现类对象
        if (!Factory.BEAN_WAREHOUSE.containsKey(requestClass)) {
            rpcResponse.setCode(RpcStatusCode.NOT_FOUND);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        var instance = Factory.getBean(requestClass);
        Method method;
        Object result = null;
        var start = System.currentTimeMillis();
        try {
            if (parameterTypes == null || parameterTypes.length == 0) {
                method = requestClass.getMethod(requestMethod);
                result = method.invoke(instance);
            } else {
                method = requestClass.getDeclaredMethod(requestMethod, parameterTypes);
                result = method.invoke(instance, parameters);
            }
        } catch (Exception e) {
            rpcResponse.setCode(RpcStatusCode.SERVER_ERROR);
        }
        LOGGER.debug("request {}-{} cost:{}s", requestClass.getName(), requestMethod, (System.currentTimeMillis() - start) / 1000);
        rpcResponse.setContent(result);
        ctx.channel().writeAndFlush(rpcResponse);
    }

    /**
     * 请求文件输入流,每次读取1M的文件内容传输
     *
     * @param ctx
     * @param request
     */
    private void processFileInRequest(ChannelHandlerContext ctx, RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRpcHeader(request.rpcHeader()).setCode(RpcStatusCode.OK);

        Object[] content = request.rpcContent().content();
        String filePath = (String) content[0];
        Long readIndex = (Long) content[1];
        byte[] fileContent = new byte[1024 * 1024];
        File file = FileUtil.file(filePath);
        if (!FileUtil.exist(file)) {
            rpcResponse.setCode(RpcStatusCode.NOT_FOUND);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        try (RandomAccessFile sourceFile = FileUtil.createRandomAccessFile(file, FileMode.r)) {
            sourceFile.seek(readIndex);
            int read = sourceFile.read(fileContent);
            //最后一段
            if (read < fileContent.length && read > 0) {
                byte[] finish = new byte[read];
                ArrayUtil.copy(fileContent, finish, read);
                fileContent = finish;
            } else if (read == -1) {
                fileContent = new byte[]{};
            }
            rpcResponse.setContent(fileContent);

        } catch (Exception e) {
            rpcResponse.setCode(RpcStatusCode.SERVER_ERROR);
        }
        ctx.channel().writeAndFlush(rpcResponse);
    }

    /**
     * 请求文件输出流,写入文件内容
     *
     * @param ctx
     * @param request
     */
    private void processFileOutRequest(ChannelHandlerContext ctx, RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRpcHeader(request.rpcHeader()).setCode(RpcStatusCode.OK);

        Object[] content = request.rpcContent().content();
        String filePath = (String) content[0];
        byte[] fileContent = (byte[]) content[1];
        File file = FileUtil.file(filePath);
        if (!FileUtil.exist(file)) {
            FileUtil.touch(file);
        }
        try (RandomAccessFile sourceFile = FileUtil.createRandomAccessFile(file, FileMode.rw)) {
            sourceFile.seek(sourceFile.length());
            sourceFile.write(fileContent);
            rpcResponse.setContent(Boolean.TRUE);
        } catch (Exception e) {
            rpcResponse.setCode(RpcStatusCode.SERVER_ERROR);
        }
        ctx.channel().writeAndFlush(rpcResponse);
    }
}
