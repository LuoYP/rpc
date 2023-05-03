package org.example.common.io;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {

    public static final Map<String, byte[]> cache = new ConcurrentHashMap<>();
}
