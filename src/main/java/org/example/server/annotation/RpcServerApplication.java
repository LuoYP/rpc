package org.example.server.annotation;

import org.example.common.constant.Protocol;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServerApplication {

    String[] rpcApiPackages();

    Protocol[] protocols();
}
