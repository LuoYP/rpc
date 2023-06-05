package org.example.common.handler.line;

import io.netty.channel.ChannelHandlerContext;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.Component;
import org.example.common.constant.MessageType;
import org.example.common.handler.Filter;
import org.example.common.io.sound.RpcAudioFormat;
import org.example.common.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Objects;

@Component
public class AudioHandler extends AbstractHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioHandler.class);

    private AudioFormat audioFormat;

    private DataLine.Info dataInfo;

    private SourceDataLine sourceDataLine;

    @Autowired
    private DefaultHandler nextHandler;

    @Autowired(required = false)
    private Filter filter;

    @Override
    protected boolean verify(byte messageType) {
        return MessageType.AUDIO == messageType;
    }

    @Override
    protected AbstractHandler nextHandler() {
        return nextHandler;
    }

    @Override
    protected void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
        if (Objects.nonNull(filter) && !filter.accept(request.rpcHeader().authorization())) {
            return;
        }
        byte[] audio = request.rpcContent().binaryContent();
        RpcAudioFormat rpcAudioFormat = (RpcAudioFormat) request.rpcContent().content()[0];
        if (Objects.isNull(audioFormat)) {
            audioFormat = new AudioFormat(rpcAudioFormat.sampleRate(), rpcAudioFormat.sampleSizeInBits(),
                    rpcAudioFormat.channels(), rpcAudioFormat.signed(), rpcAudioFormat.bigEndian());
        }
        if (Objects.isNull(dataInfo)) {
            dataInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        }
        try {
            if (Objects.isNull(sourceDataLine)) {
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataInfo);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();
            }
        } catch (LineUnavailableException e) {
            LOGGER.error("fetch audio output device failed!");
            return;
        }
        sourceDataLine.write(audio, 0, audio.length);
    }
}
