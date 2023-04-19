package org.example.communication.interfaces;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("");
        if (Objects.isNull(resource)) {
            return;
        }
        File directory = new File(resource.getFile());
        String rootPath = directory.getAbsolutePath() + File.separator;
        List<File> classFiles = new ArrayList<>(loadFileByStack(directory));
        for (File classFile : classFiles) {
            String fileName = classFile.getAbsolutePath();
            if (fileName.endsWith(".class")) {
                String fileClassPath = fileName.replace(rootPath, "");
                String alias = fileClassPath.replace(File.separator, ".");
                String className = alias.substring(0, alias.length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isInterface()) {
                        continue;
                    }
                    for (Class<?> interfaceClass : interfaceClasses) {
                        if (interfaceClass.isAssignableFrom(clazz)) {
                            IMPL_CLASS.put(interfaceClass.getName(), clazz);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<Class<?>> loadInterface(String packageName) {
        List<Class<?>> interfaceClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        URL resource = classLoader.getResource(path);
        if (Objects.isNull(resource)) {
            return interfaceClasses;
        }
        File directory = new File(resource.getFile());
        directory = new File(directory.getAbsolutePath());
        List<File> fileList = new ArrayList<>(loadFileByStack(directory));
        for (File _file : fileList) {
            String fileName = _file.getAbsolutePath();
            if (fileName.endsWith(".class")) {
                String alias = fileName.replace(File.separator, ".");
                String substring = alias.substring(alias.indexOf(packageName));
                String className = substring.substring(0, substring.length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isInterface()) {
                        interfaceClasses.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return interfaceClasses;
    }

    private static List<File> loadFileByStack(File parent) {
        List<File> result = new ArrayList<>();
        Stack<File> directories = new Stack<>();
        directories.push(parent);
        while (!directories.empty()) {
            File directory = directories.pop();
            File[] files = directory.listFiles();
            if (Objects.isNull(files)) {
                continue;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    directories.push(file);
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }

}
