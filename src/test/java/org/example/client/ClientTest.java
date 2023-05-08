package org.example.client;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.MD5;
import org.example.RpcClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.client.io.RpcFile;
import org.example.common.context.Factory;
import org.example.communication.client.api.TimeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.System.currentTimeMillis;

@RpcClientApplication(rpcApiPackages = "org.example.communication.client.api")
public class ClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) throws Exception{
        RpcClient.run(ClientTest.class);
        Thread.sleep(15000);
        testRpcFile("G:\\进化的四十六亿重奏.txt");
    }

    private static void tetRpc() throws Exception {
        Thread.sleep(20000);
        TimeServer timeServer = (TimeServer) Factory.getBean(TimeServer.class);
        System.out.println(timeServer.now());
    }

    private static void testRpcFile(String filePath) {
        RpcFile file = new RpcFile(filePath);
        File local = FileUtil.file("C:\\Users\\Administrator\\Desktop\\进化的四十六亿重奏.txt");
        long start = currentTimeMillis();
        LOGGER.info("start download file!");
        FileUtil.writeFromStream(file.inputStream(), local);
        LOGGER.info("download success,cost {}s!", (currentTimeMillis() - start) / 1000);


        String sourceHash = MD5.create().digestHex(FileUtil.file(filePath));
        LOGGER.info("remote file hash: {}", sourceHash);
        String downloadHash = MD5.create().digestHex(FileUtil.file(local));
        LOGGER.info("download file hash: {}", downloadHash);
    }
}
