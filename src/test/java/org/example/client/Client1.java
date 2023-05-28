package org.example.client;

import org.example.RpcClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.common.constant.Protocol;
import org.example.common.io.sound.RpcAudioFormat;
import org.example.common.io.sound.RpcSourceDataLine;

import javax.sound.sampled.*;

@RpcClientApplication(rpcApiPackages = "org.example.communication.client.api", protocols = {Protocol.UDP})
public class Client1 {

    public static void main(String[] args) throws Exception {
        RpcClient.run(Client1.class);
        Thread.sleep(3000);

        AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        line.open(audioFormat);
        line.start();
        AudioInputStream audioInputStream = new AudioInputStream(line);

        RpcAudioFormat rpcAudioFormat = new RpcAudioFormat(44100.0F, 16, 2, true, false);
        RpcSourceDataLine sourceDataLine = RpcSourceDataLine.build(rpcAudioFormat);
        sourceDataLine.start();
        byte[] bytes = new byte[1024];
        while (audioInputStream.read(bytes, 0, 1024) != -1) {
            sourceDataLine.write(bytes);
        }
        sourceDataLine.stop();
    }
}
