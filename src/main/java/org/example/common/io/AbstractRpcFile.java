package org.example.common.io;

import io.netty.channel.Channel;

/**
 * 提供远程文件的基本信息.
 *
 * <p>提供远程文件的保存路径,与文件传输使用的通道
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
