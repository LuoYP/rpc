package org.example.common.io;

import io.netty.channel.Channel;
import org.example.server.Session;

/**
 * 对 {@link java.io.File}类型的封装，用于在RPC通信中进行文件基本信息的传输
 * 对外提供输入输出，通过流的操作实现远程文件传输
 */
public class RpcFile {

    private String ip;

    private String fileAbsolutePath;

    private RpcFileInputStream inputStream;

    private RpcFileOutputStream outputStream;

    public RpcFile(String ip, String fileAbsolutePath) {
        this.ip = ip;
        this.fileAbsolutePath = fileAbsolutePath;
        Channel channel = Session.ACTIVE_CHANNEL.get(ip);
        inputStream = new RpcFileInputStream(channel, fileAbsolutePath);
    }

    public String ip() {
        return ip;
    }

    public RpcFile setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String fileAbsolutePath() {
        return fileAbsolutePath;
    }

    public RpcFile setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        return this;
    }

    public RpcFileInputStream inputStream() {
        return inputStream;
    }

    public RpcFile setInputStream(RpcFileInputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public RpcFileOutputStream outputStream() {
        return outputStream;
    }

    public RpcFile setOutputStream(RpcFileOutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
}
