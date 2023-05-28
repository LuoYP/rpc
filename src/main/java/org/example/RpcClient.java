package org.example;

import cn.hutool.core.util.ClassUtil;
import io.netty.channel.Channel;
import org.example.client.Cookies;
import org.example.client.NettyClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.common.annotation.Component;
import org.example.common.annotation.Configuration;
import org.example.common.annotation.RpcService;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.constant.Protocol;
import org.example.common.constant.RpcStatusCode;
import org.example.common.context.Factory;
import org.example.common.model.RpcRequest;
import org.example.common.model.RpcResponse;
import org.example.common.sender.RpcSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class RpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private static final Function<Class<?>, Object> PROXY_GENERATOR = clazz -> {
        Object defaultObject = new Object();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Proxy.newProxyInstance(classLoader, new Class[]{clazz}, (proxyInner, method, args) -> {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(defaultObject, args);
            } else {
                //通过netty发起远程过程调用
                Cookies cookies = (Cookies) Factory.getBean(Cookies.class);
                Channel server = cookies.server();
                if (Objects.isNull(server)) {
                    throw new RuntimeException("server is off-line");
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
                RpcResponse rpcResponse = sender.send(rpcRequest, server);
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
     * 启动netty客户端
     */
    public static void run(Class<?> mainClazz) {
        //初始化需要反射创建的对象
        Set<Class<?>> componentClasses = new HashSet<>();
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(mainClazz.getPackageName(), Component.class));
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(RpcClient.class.getPackageName(), Component.class));
        componentClasses.addAll(ClassUtil.scanPackageByAnnotation(RpcClient.class.getPackageName(), Configuration.class));
        Factory.initBean(componentClasses);

        //初始化需要代理的RPC接口
        RpcClientApplication annotation = mainClazz.getAnnotation(RpcClientApplication.class);
        if (Objects.isNull(annotation)) {
            throw new RuntimeException("you must statement RpcClientApplication on your starter!");
        }
        Factory.instantiationRpcApi(PROXY_GENERATOR, annotation.rpcApiPackages());
        Set<Class<?>> rpcServiceClasses = ClassUtil.scanPackageByAnnotation("", RpcService.class);
        Factory.instantiationRpcService(rpcServiceClasses);

        //启动Netty服务
        Protocol[] protocols = annotation.protocols();
        Thread thread = new Thread(() -> {
            NettyClient nettyClient = (NettyClient) Factory.getBean(NettyClient.class);
            nettyClient.start(protocols);
        }, "netty-client");
        thread.start();
    }
}
