package org.example.common.constant;

public class MessageType {

    /** RPC消息类型,封装在RpcHeader中. */
    public static final byte HEART_BEAT = 0x01;

    public static final byte COMMENT = 0x02;

    /** 该类型为请求远程文件输入流&响应文件输入流 */
    public static final byte FILE_IN = 0x03;

    /** 该类型为请求远程文件输出流&响应文件输出流 */
    public static final byte FILE_OUT = 0x04;

    /** 该类型为请求远程音频输出 */
    public static final byte AUDIO = 0x05;
}
