package org.example.common.io;

/**
 * 对 {@link java.io.File}类型的封装，用于在RPC通信中进行文件基本信息的传输
 * 对外提供输入输出，通过流的操作实现远程文件传输
 */
public abstract class AbstractRpcFile {

    protected String ip;

    protected String fileAbsolutePath;

    protected RpcFileInputStream inputStream;

    protected RpcFileOutputStream outputStream;

    public String ip() {
        return ip;
    }

    public AbstractRpcFile setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String fileAbsolutePath() {
        return fileAbsolutePath;
    }

    public AbstractRpcFile setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        return this;
    }

    public RpcFileInputStream inputStream() {
        return inputStream;
    }

    public AbstractRpcFile setInputStream(RpcFileInputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public RpcFileOutputStream outputStream() {
        return outputStream;
    }

    public AbstractRpcFile setOutputStream(RpcFileOutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }
}
