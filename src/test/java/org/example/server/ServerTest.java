package org.example.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.MD5;
import org.example.RpcServer;
import org.example.common.constant.Protocol;
import org.example.common.context.Factory;
import org.example.common.io.RpcFileInputStream;
import org.example.communication.server.api.SystemInfoService;
import org.example.server.annotation.RpcServerApplication;
import org.example.server.io.RpcFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.System.currentTimeMillis;

@RpcServerApplication(rpcApiPackages = {"org.example.communication.server.api"}, protocols = Protocol.TCP)
public class ServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTest.class);

    public static void main(String[] args) throws Exception {
        RpcServer.run(ServerTest.class);
//        Thread.sleep(15000);
//        testRPCFile("G:\\进化的四十六亿重奏.txt");
    }

    private static void testRPC() throws Exception {
        SystemInfoService proxy = (SystemInfoService) Factory.getBeanNotNull(SystemInfoService.class);
        Thread.sleep(15000);
        String result = proxy.sayHello("127.0.0.1");
        LOGGER.info("receive a result: {}", result);
    }

    private static void testRPCFile(String filePath) {
        RpcFile file = new RpcFile("127.0.0.1", filePath);
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
}
