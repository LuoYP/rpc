package org.example;

import cn.hutool.core.util.ClassUtil;
import io.netty.channel.Channel;
import org.example.common.annotation.Component;
import org.example.common.annotation.Configuration;
import org.example.common.annotation.RpcService;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.example.common.utils.CharSequenceUtil;
import org.example.server.NettyServer;
import org.example.server.Session;
import org.example.server.annotation.RpcServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private static final Function<Class<?>, Object> PROXY_GENERATOR = clazz -> {
        Object defaultObject = new Object();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Proxy.newProxyInstance(classLoader, new Class[]{clazz}, (proxyInner, method, args) -> {
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
                byte messageType = MessageType.COMMENT;
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.buildRpcLine(className, methodName, parameterTypes)
                        .buildRpcHeader(id, messageType, null)
                        .buildRpcContent(args);
                //发送请求
                RpcSender sender = (RpcSender) Factory.getBean(RpcSender.class);
                RpcResponse rpcResponse = sender.send(rpcRequest, channel);
                RpcStatusCode status = rpcResponse.code();
                if (RpcStatusCode.OK.equals(status)) {
                    return rpcResponse.content();
                }
                throw new RuntimeException(status.msg());
            }
        });
    };


    /**
     * 初始化服务环境
     * 启动netty服务
     */
    public static void run(Class<?> mainClazz) {
        //初始化需要反射创建的对象
        Set<Class<?>> componentClasses = new HashSet<>();
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(mainClazz.getPackageName(), Component.class));
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(RpcServer.class.getPackageName(), Component.class));
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(RpcServer.class.getPackageName(), Configuration.class));
        Factory.initBean(componentClasses);

        //初始化需要代理的RPC接口
        RpcServerApplication annotation = mainClazz.getAnnotation(RpcServerApplication.class);
        if (Objects.isNull(annotation)) {
            throw new RuntimeException("you must statement RpcServerApplication on your starter!");
        }
        Factory.instantiationRpcApi(PROXY_GENERATOR, annotation.rpcApiPackages());
        Set<Class<?>> rpcServiceClasses = ClassUtil.scanPackageByAnnotation("", RpcService.class);
        Factory.instantiationRpcService(rpcServiceClasses);

        //启动Netty服务
        Thread thread = new Thread(() -> {
            NettyServer nettyServer = (NettyServer) Factory.getBean(NettyServer.class);
            nettyServer.start();
        }, "netty-server");
        thread.start();
    }
}
