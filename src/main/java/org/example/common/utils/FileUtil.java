package org.example.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtil {

    /**
     * 获取指定路径下的所有文件
     *
     * @param path
     * @return
     */
    public static List<File> getAllFiles(String path) {
        List<File> files = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                files.addAll(getAllFiles(subFile.getAbsolutePath()));
            }
        } else {
            files.add(file);
        }
        return files;
    }
}
