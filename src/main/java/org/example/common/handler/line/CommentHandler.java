package org.example.common.handler.line;

import cn.hutool.core.util.ClassUtil;
import io.netty.channel.ChannelHandlerContext;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcLine;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Component
public class CommentHandler extends AbstractHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentHandler.class);

    @Autowired
    private FileInHandler fileInHandler;

    @Override
    protected boolean verify(byte messageType) {
        return MessageType.COMMENT == messageType;
    }

    @Override
    protected AbstractHandler nextHandler() {
        return fileInHandler;
    }

    @Override
    protected void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
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
        //缓冲区不可写,直接丢弃,所以应当合理设置高低水位线
        if (ctx.channel().isWritable()) {
            ctx.channel().writeAndFlush(rpcResponse);
        }
    }
}
