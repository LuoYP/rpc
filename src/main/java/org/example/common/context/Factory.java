package org.example.common.context;

import cn.hutool.core.util.ClassUtil;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Bean;
import org.example.common.annotation.Component;
import org.example.common.annotation.Configuration;
import org.example.common.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Factory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);

    public static final Map<Class<?>, Object> BEAN_WAREHOUSE = new ConcurrentHashMap<>();

    public static void initBean(Set<Class<?>> classes) {
        LOGGER.debug("init the bean container!");
        for (Class<?> clazz : classes) {
            Configuration configuration = clazz.getAnnotation(Configuration.class);
            if (Objects.nonNull(configuration)) {
                Object configurationObject = newInstance(clazz);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    Bean bean = method.getAnnotation(Bean.class);
                    if (Objects.nonNull(bean)) {
                        try {
                            Class<?> returnType = method.getReturnType();
                            Object returnValue = method.invoke(configurationObject);
                            BEAN_WAREHOUSE.put(returnType, returnValue);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            Component annotation = clazz.getAnnotation(Component.class);
            if (Objects.nonNull(annotation)) {
                Object instance = BEAN_WAREHOUSE.containsKey(clazz) ? BEAN_WAREHOUSE.get(clazz) : newInstance(clazz);

                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if (Objects.isNull(autowired)) {
                        continue;
                    }
                    Class<?> fieldType = field.getType();
                    Object factoryBean = BEAN_WAREHOUSE.get(fieldType);
                    if (Objects.nonNull(factoryBean)) {
                        setValue(instance, factoryBean, field);
                        continue;
                    }
                    Component annotation1 = fieldType.getAnnotation(Component.class);
                    if (Objects.isNull(annotation1)) {
                        throw new RuntimeException("inject bean mast statement Component");
                    }
                    Object fieldObject = newInstance(fieldType);
                    BEAN_WAREHOUSE.put(fieldType, fieldObject);
                    setValue(instance, fieldObject, field);
                }
                BEAN_WAREHOUSE.put(clazz, instance);
            }
        }
    }

    public static void instantiationRpcApi(Function<Class<?>, Object> proxyGenerator, String... rpcApiPackages) {
        LOGGER.debug("start implement the rpc interface!");
        Set<Class<?>> interfaceClasses = new HashSet<>();
        for (String rpcApiPackage : rpcApiPackages) {
            interfaceClasses.addAll(ClassUtil.scanPackage(rpcApiPackage));
        }
        if (interfaceClasses.isEmpty()) {
            throw new RuntimeException("you must statement rpc remote interface!");
        }
        LOGGER.info("find rpc api, total {}", interfaceClasses.size());
        interfaceClasses.forEach(clazz -> {
            BEAN_WAREHOUSE.put(clazz, proxyGenerator.apply(clazz));
        });
    }

    public static void instantiationRpcService(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            RpcService annotation = clazz.getAnnotation(RpcService.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            Class<?> interfaceClazz = clazz.getInterfaces()[0];
            //已经存在代理对象了,说明接口与实现定义在一起了,忽略
            if (BEAN_WAREHOUSE.containsKey(interfaceClazz)) {
                continue;
            }
            BEAN_WAREHOUSE.put(interfaceClazz, newInstance(clazz));
        }
    }

    public static Object getBean(Class<?> clazz) {
        Object bean = BEAN_WAREHOUSE.get(clazz);
        if (Objects.isNull(bean)) {
            throw new RuntimeException("can not find target bean");
        }
        return bean;
    }

    private static Object newInstance(Class<?> clazz) {
        Object autoCreate;
        try {
            autoCreate = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("you must have parameterless constructor");
        }
        return autoCreate;
    }

    private static void setValue(Object object, Object value, Field field) {
        boolean access = field.canAccess(object);
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("inject failed");
        }
        field.setAccessible(access);
    }
}
