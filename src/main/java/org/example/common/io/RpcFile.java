package org.example.common.io;

/**
 * 对 {@link java.io.File}类型的封装，用于在RPC通信中进行文件基本信息的传输
 * 对外提供输入输出，通过流的操作实现远程文件传输
 */
public class RpcFile {

    private String IP;

    private String fileAbsolutePath;

    private RpcFileInputStream inputStream;

    private RpcFileOutputStream outputStream;

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
