package org.example.common.handler.line;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileMode;
import cn.hutool.core.util.ArrayUtil;
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
public class FileInHandler extends AbstractHandler{

    @Autowired
    private FileOutHandler nextHandler;

    @Override
    protected boolean verify(byte messageType) {
        return MessageType.FILE_IN == messageType;
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
        Long readIndex = (Long) content[1];
        byte[] fileContent = new byte[1024 * 1024];
        File file = FileUtil.file(filePath);
        if (!FileUtil.exist(file)) {
            rpcResponse.setCode(RpcStatusCode.NOT_FOUND);
            //缓冲区不可写,直接丢弃,所以应当合理设置高低水位线
            if (ctx.channel().isWritable()) {
                ctx.channel().writeAndFlush(rpcResponse);
            }
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
        //缓冲区不可写,直接丢弃,所以应当合理设置高低水位线
        if (ctx.channel().isWritable()) {
            ctx.channel().writeAndFlush(rpcResponse);
        }
    }
}
