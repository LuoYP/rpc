package org.example.common.utils;

import cn.hutool.core.collection.EnumerationIter;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassUtil {

    /**
     * 获取指定包下的所有接口
     *
     * @param packageNames 包名
     * @return 接口
     */
    public static Set<Class<?>> getInterfaceClasses(String... packageNames) {
        Set<Class<?>> interfaces = new HashSet<>();
        for (String packageName : packageNames) {
            List<Class<?>> classes = getClasses(packageName);
            if (!classes.isEmpty()) {
                interfaces.addAll(classes);
            }
        }
        return interfaces.stream().filter(Class::isInterface).collect(Collectors.toSet());
    }

    /**
     * 获取指定包下的所有Class
     *
     * @param packageName 包名
     * @return Class
     */
    public static List<Class<?>> getClasses(String packageName) {
        List<String> classNames = getClassName(packageName);
        return classNames.stream().map(className -> {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                // 由于依赖库导致的类无法加载，直接跳过此类
            } catch (UnsupportedClassVersionError e) {
                // 版本导致的不兼容的类，跳过
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return clazz;
        }).collect(Collectors.toList());
    }

    /**
     * 获取指定包下的所有类名
     *
     * @param packageName 包名
     * @return 类名
     */
    public static List<String> getClassName(String packageName) {
        List<String> classNames = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = Thread.currentThread().getContextClassLoader().getResources(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Objects.isNull(resources)) {
            return classNames;
        }
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileType = resource.getProtocol();
            if ("file".equals(fileType)) {
                classNames.addAll(loadClassNameByFile(resource.getFile()));
            } else if ("jar".equals(fileType)) {
                classNames.addAll(loadClassNameByJar(resource.getFile()));
            }
        }
        return classNames;
    }

    /**
     * 获取项目中指定路径的类名
     *
     * @param directoryName 文件路径
     * @return 指定路径下的类名
     */
    private static List<String> loadClassNameByFile(String directoryName) {
        File directory = new File(directoryName);
        //避免递归导致栈溢出，使用栈结构
        Stack<File> directories = new Stack<>();
        directories.push(directory);
        List<String> classNames = new ArrayList<>();
        while (!directories.empty()) {
            File _directory = directories.pop();
            File[] files = _directory.listFiles();
            if (Objects.isNull(files)) {
                continue;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    directories.push(file);
                } else if (file.isFile() && file.getName().endsWith(".class")) {
                    String filePath = file.getAbsolutePath();
                    String className = filePath.substring(filePath.indexOf("classes") + 8, filePath.lastIndexOf('.')).replace(File.separator, ".");
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    /**
     * 获取jar包中的类名
     *
     * @param jarPath jar包路径
     * @return 指定路径下的类名
     */
    private static List<String> loadClassNameByJar(String jarPath) {
        List<String> classNames = new ArrayList<>();
        String[] jarPathArr = jarPath.split("!");
        String jarFilePath = jarPathArr[0].substring(jarPathArr[0].indexOf("/"));
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String jarPathName = jarEntry.getName();
                if (jarPathName.endsWith(".class") && !jarEntry.isDirectory()) {
                    String className = jarPathName.substring(0, jarPathName.lastIndexOf('.')).replace('/', '.');
                    classNames.add(className);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classNames;
    }
}
