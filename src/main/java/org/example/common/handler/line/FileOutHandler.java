package org.example.common.handler.line;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileMode;
import io.netty.channel.ChannelHandlerContext;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcStatusCode;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;

import java.io.File;
import java.io.RandomAccessFile;

@Component
public class FileOutHandler extends AbstractHandler{

    @Autowired
    private AudioHandler nextHandler;

    @Override
    protected boolean verify(byte messageType) {
        return MessageType.FILE_OUT == messageType;
    }

    @Override
    protected AbstractHandler nextHandler() {
        return nextHandler;
    }

    @Override
    protected void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
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
        //缓冲区不可写,直接丢弃,所以应当合理设置高低水位线
        if (ctx.channel().isWritable()) {
            ctx.channel().writeAndFlush(rpcResponse);
        }
    }
}
