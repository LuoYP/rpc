package org.example.communication.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassCollection {

    public static final Map<String, Class<?>> IMPL_CLASS = new ConcurrentHashMap<>();

//    static {
//        loadAllImplClass(loadCurrentInterface());
//    }
//
//    private static List<Class<?>> loadCurrentInterface() {
//        Package _package = ClassCollection.class.getPackage();
//        String packageName = _package.getName();
//        return loadInterface(packageName);
//    }
//
//    private static void loadAllImplClass(List<Class<?>> interfaceClasses) {
//        List<String> classNames = getClassName("");
//        List<Class<?>> allClass = classNames.stream().map(className -> {
//            Class<?> clazz;
//            try {
//                clazz = Class.forName(className);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            return clazz;
//        }).filter(clazz -> !clazz.isInterface()).collect(Collectors.toList());
//        for (Class<?> clazz : allClass) {
//            for (Class<?> _interface : interfaceClasses) {
//                if (_interface.isAssignableFrom(clazz)) {
//                    IMPL_CLASS.put(_interface.getName(), clazz);
//                }
//            }
//        }
//    }
//
//    private static List<Class<?>> loadInterface(String packageName) {
//        List<String> classNames = getClassName(packageName);
//        return classNames.stream().map(className -> {
//            Class<?> aClass;
//            try {
//                aClass = Class.forName(className);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            return aClass;
//        }).filter(Class::isInterface).collect(Collectors.toList());
//    }
}
