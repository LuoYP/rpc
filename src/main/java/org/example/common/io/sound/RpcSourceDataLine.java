package org.example.common.io.sound;

import io.netty.channel.socket.nio.NioDatagramChannel;
import org.example.client.Cookies;
import org.example.common.constant.Constants;
import org.example.common.constant.MessageType;
import org.example.common.context.Factory;
import org.example.common.model.RpcContent;
import org.example.common.model.RpcHeader;
import org.example.common.model.RpcRequest;

import java.util.Objects;

public class RpcSourceDataLine {

    private NioDatagramChannel channel;

    //在中心申请的房间号，如果为空，消息将在整个组内广播
    private String uniqueNumber;

    //音频格式设置，为了保证音频输出不失真，收发应当保持一致
    private RpcAudioFormat audioFormat;

    private RpcSourceDataLine() {
    }

    public static RpcSourceDataLine build(RpcAudioFormat audioFormat) {
        return build(audioFormat, Constants.MULTICAST);
    }

    public static RpcSourceDataLine build(RpcAudioFormat audioFormat, String uniqueNumber) {
        RpcSourceDataLine sourceDataLine = new RpcSourceDataLine();
        sourceDataLine.audioFormat = audioFormat;
        sourceDataLine.uniqueNumber = uniqueNumber;
        return sourceDataLine;
    }

    public void start() {
        Cookies cookies = (Cookies) Factory.getBeanNotNull(Cookies.class);
        this.channel = cookies.udpChannel();
    }

    public void write(byte[] b) {
        if (Objects.isNull(channel)) {
            throw new RuntimeException("the data line is not init!");
        }
        if (b.length > 2 * 1024) {
            throw new RuntimeException("the data size must between 0B and 2KB");
        }
        RpcRequest request = new RpcRequest();
        request.setRpcHeader(new RpcHeader().setMessageType(MessageType.AUDIO).setAuthorization(uniqueNumber));
        request.setRpcContent(new RpcContent().setBinaryContent(b).setContent(new Object[]{audioFormat}));
        channel.writeAndFlush(request);
    }

    public void stop() {

    }
}
