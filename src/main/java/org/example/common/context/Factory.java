package org.example.common.context;

import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Factory {

    public static final Map<Class<?>, Object> BEAN_WAREHOUSE = new ConcurrentHashMap<>();

    public static void initBean(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            Component annotation = clazz.getAnnotation(Component.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            Object instance = BEAN_WAREHOUSE.containsKey(clazz) ? BEAN_WAREHOUSE.get(clazz) : newInstance(clazz);

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (Objects.isNull(autowired)) {
                    continue;
                }
                Class<?> fieldType = field.getType();
                Component annotation1 = fieldType.getAnnotation(Component.class);
                if (Objects.isNull(annotation1)) {
                    throw new RuntimeException("inject bean mast statement Component");
                }
                Object factoryBean = BEAN_WAREHOUSE.get(fieldType);
                if (Objects.nonNull(factoryBean)) {
                    setValue(instance, factoryBean, field);
                    continue;
                }
                Object fieldObject = newInstance(fieldType);
                BEAN_WAREHOUSE.put(fieldType, fieldObject);
                setValue(instance, fieldObject, field);
            }
            BEAN_WAREHOUSE.put(clazz, instance);
        }
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
