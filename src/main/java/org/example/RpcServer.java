package org.example;

import org.example.common.annotation.RpcServerApplication;
import org.example.common.context.Factory;
import org.example.common.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    /**
     * 初始化服务环境
     * 启动netty服务
     */
    public void run(Class<?> mainClazz) {
        //获取接口
        RpcServerApplication annotation = mainClazz.getAnnotation(RpcServerApplication.class);
        if (Objects.isNull(annotation)) {
            throw new RuntimeException("you must statement RpcServerApplication on your starter!");
        }
        String[] rpcApiPackages = annotation.rpcApiPackages();
        Set<Class<?>> interfaceClasses = ClassUtil.getInterfaceClasses(rpcApiPackages);
        if (interfaceClasses.isEmpty()) {
            throw new RuntimeException("you must statement rpc remote interface!");
        }

        List<Class<?>> classes = ClassUtil.getClasses("");
        Factory.initBean(classes);

    }
}
