package org.example.common.constant;

import org.example.common.io.RpcFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContainer {

    public static final Map<Long, RpcFile> TRANSFERRING_FILES = new ConcurrentHashMap<>();
}
