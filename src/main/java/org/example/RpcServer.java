package org.example;

import io.netty.channel.Channel;
import org.example.common.annotation.RpcServerApplication;
import org.example.common.constant.Constants;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcFile;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.example.common.utils.CharSequenceUtil;
import org.example.common.utils.ClassUtil;
import org.example.server.NettyServer;
import org.example.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    /**
     * 初始化服务环境
     * 启动netty服务
     */
    public static void run(Class<?> mainClazz) {
        //初始化需要反射创建的对象
        List<Class<?>> classes = ClassUtil.getClasses("");
        Factory.initBean(classes);

        //初始化需要代理的RPC接口
        RpcServerApplication annotation = mainClazz.getAnnotation(RpcServerApplication.class);
        if (Objects.isNull(annotation)) {
            throw new RuntimeException("you must statement RpcServerApplication on your starter!");
        }
        instantiationRpcApi(annotation.rpcApiPackages());

        //启动Netty服务
        NettyServer nettyServer = (NettyServer) Factory.BEAN_WAREHOUSE.get(NettyServer.class);
        nettyServer.start();
    }

    private static void instantiationRpcApi(String... rpcApiPackages) {
        Set<Class<?>> interfaceClasses = ClassUtil.getInterfaceClasses(rpcApiPackages);
        if (interfaceClasses.isEmpty()) {
            throw new RuntimeException("you must statement rpc remote interface!");
        }
        LOGGER.info("find rpc api, total {}", interfaceClasses.size());
        interfaceClasses.forEach(clazz -> {
            Object defaultObject = new Object();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Object proxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{clazz}, (proxyInner, method, args) -> {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(defaultObject, args);
                } else {
                    //通过netty发起远程过程调用
                    String remote = (String) args[0];
                    if (CharSequenceUtil.isEmpty(remote)) {
                        throw new RuntimeException("remote IP is empty");
                    }
                    Channel channel = Session.ACTIVE_CHANNEL.get(remote);
                    if (Objects.isNull(channel)) {
                        throw new RuntimeException("remote is off-line");
                    }
                    //构造RPC请求
                    String className = clazz.getName();
                    String methodName = method.getName();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    long id = Constants.ID.getAndIncrement();
                    byte messageType = method.getReturnType().equals(RpcFile.class) ? Constants.FILE : Constants.COMMENT;
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.buildRpcLine(className, methodName, parameterTypes)
                            .buildRpcHeader(id, messageType, null)
                            .buildRpcContent(args);
                    //发送请求
                    RpcSender sender = (RpcSender) Factory.BEAN_WAREHOUSE.get(RpcSender.class);
                    RpcResponse rpcResponse = sender.send(rpcRequest, channel);
                    RpcStatusCode status = rpcResponse.code();
                    if (RpcStatusCode.OK.equals(status)) {
                        return rpcResponse.content();
                    }
                    throw new RuntimeException(status.msg());
                }
            });
            Factory.BEAN_WAREHOUSE.put(clazz, proxyInstance);
        });
    }
}
