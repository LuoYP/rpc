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

    static {
        loadAllImplClass(loadCurrentInterface());
    }

    private static List<Class<?>> loadCurrentInterface() {
        Package _package = ClassCollection.class.getPackage();
        String packageName = _package.getName();
        return loadInterface(packageName);
    }

    private static void loadAllImplClass(List<Class<?>> interfaceClasses) {
        List<String> classNames = getClassName("");
        List<Class<?>> allClass = classNames.stream().map(className -> {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return clazz;
        }).filter(clazz -> !clazz.isInterface()).collect(Collectors.toList());
        for (Class<?> clazz : allClass) {
            for (Class<?> _interface : interfaceClasses) {
                if (_interface.isAssignableFrom(clazz)) {
                    IMPL_CLASS.put(_interface.getName(), clazz);
                }
            }
        }
    }

    private static List<Class<?>> loadInterface(String packageName) {
        List<String> classNames = getClassName(packageName);
        return classNames.stream().map(className -> {
            Class<?> aClass;
            try {
                aClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return aClass;
        }).filter(Class::isInterface).collect(Collectors.toList());
    }

    private static List<String> getClassName(String packageName) {
        List<String> classNames = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (Objects.isNull(resource)) {
            return classNames;
        }
        String fileType = resource.getProtocol();
        if ("file".equals(fileType)) {
            classNames.addAll(loadClassNameByFile(resource.getFile()));
        } else if ("jar".equals(fileType)) {
            classNames.addAll(loadClassNameByJar(resource.getFile()));
        }
        return classNames;
    }

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

    private static List<String> loadClassNameByJar(String jarPath) {
        List<String> classNames = new ArrayList<>();
        String[] jarPathArr = jarPath.split("!");
        String jarFilePath = jarPathArr[0].substring(jarPathArr[0].indexOf("/"));
        String jarPackage = jarPathArr[1].substring(1);
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String jarPathName = jarEntry.getName();
                if (jarPathName.startsWith(jarPackage) && jarPathName.endsWith(".class")) {
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
