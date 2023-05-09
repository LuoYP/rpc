package org.example.common.io;

import io.netty.channel.Channel;

/**
 * 对 {@link java.io.File}类型的封装，用于在RPC通信中进行文件基本信息的传输
 * 对外提供输入输出，通过流的操作实现远程文件传输
 */
public abstract class AbstractRpcFile {

    protected String fileAbsolutePath;

    protected abstract Channel getChannel();

    public String fileAbsolutePath() {
        return fileAbsolutePath;
    }

    public AbstractRpcFile setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        return this;
    }
}
