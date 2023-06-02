package org.example.client;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.MD5;
import org.example.RpcClient;
import org.example.client.annotation.RpcClientApplication;
import org.example.client.io.RpcFile;
import org.example.common.constant.Protocol;
import org.example.common.context.Factory;
import org.example.common.io.RpcFileInputStream;
import org.example.common.io.RpcFileOutputStream;
import org.example.communication.client.api.TimeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;

import static java.lang.System.currentTimeMillis;

@RpcClientApplication(rpcApiPackages = "org.example.communication.client.api", protocols = {Protocol.TCP})
public class ClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) throws Exception {
        RpcClient.run(ClientTest.class);
        Thread.sleep(5000);
        testRpcFile("G:\\进化的四十六亿重奏.txt");
    }

    private static void tetRpc() throws Exception {
        Thread.sleep(20000);
        TimeServer timeServer = (TimeServer) Factory.getBeanNotNull(TimeServer.class);
        System.out.println(timeServer.now());
    }

    private static void testRpcFile(String filePath) {
        RpcFile file = new RpcFile(filePath);
        File local = FileUtil.file("C:\\Users\\Administrator\\Desktop\\进化的四十六亿重奏.txt");
        long start = currentTimeMillis();
        LOGGER.info("start download file!");
        RpcFileInputStream inputStream = new RpcFileInputStream(file);
        FileUtil.writeFromStream(inputStream, local);
        LOGGER.info("download success,cost {}s!", (currentTimeMillis() - start) / 1000);


        String sourceHash = MD5.create().digestHex(FileUtil.file(filePath));
        LOGGER.info("remote file hash: {}", sourceHash);
        String downloadHash = MD5.create().digestHex(FileUtil.file(local));
        LOGGER.info("download file hash: {}", downloadHash);
    }

    private static void testUploadRpcFile(String remoteUploadPath) {
        RpcFile rpcFile = new RpcFile(remoteUploadPath);
        File local = FileUtil.file("G:\\进化的四十六亿重奏.txt");
        long start = currentTimeMillis();
        LOGGER.info("start upload file!");
        RpcFileOutputStream outputStream = new RpcFileOutputStream(rpcFile);
        BufferedInputStream inputStream = FileUtil.getInputStream(local);
        IoUtil.copy(inputStream, outputStream);
        LOGGER.info("upload success,cost {}s!", (currentTimeMillis() - start) / 1000);

        String sourceHash = MD5.create().digestHex(local);
        LOGGER.info("local file hash: {}", sourceHash);
        String uploadHash = MD5.create().digestHex(FileUtil.file(remoteUploadPath));
        LOGGER.info("upload file hash: {}", uploadHash);
    }
}
