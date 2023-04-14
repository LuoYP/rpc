package org.example.common.utils;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

public class ClassUtil {

    /**
     * 通过接口名获取实现类，仅返回第一个实现类
     * 切分接口名，获取顶级包名
     *
     * @param interfaceName
     * @return
     */
    public static Class<?> getImplementClass(String interfaceName) throws Exception {
        if (CharSequenceUtil.isEmpty(interfaceName)) {
            return null;
        }
        String[] split = interfaceName.split("\\.");
        return getImplementationClass(interfaceName, split[0]);
    }

    /**
     * 通过接口名与包名获取对应包下的实现类
     *
     * @param interfaceName
     * @param packageName
     * @return
     * @throws Exception
     */
    public static Class<?> getImplementationClass(String interfaceName, String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                for (File subFile : Objects.requireNonNull(file.listFiles())) {
                    if (subFile.isDirectory()) {
                        getImplementationClass(interfaceName, packageName + "." + subFile.getName());
                    } else {
                        String className = packageName + "." + subFile.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (CharSequenceUtil.equals(interfaceName, clazz.getName())) {
                            return clazz;
                        }
                    }
                }
            }
        }
        throw new ClassNotFoundException("No implementation of " + interfaceName + " found in package " + packageName);
    }
}
