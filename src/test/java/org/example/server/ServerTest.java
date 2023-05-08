package org.example.server;

import cn.hutool.core.io.FileUtil;
import org.example.RpcServer;
import org.example.common.context.Factory;
import org.example.common.io.RpcFile;
import org.example.communication.server.api.SystemInfoService;
import org.example.server.annotation.RpcServerApplication;

import java.io.File;

import static java.lang.System.currentTimeMillis;

@RpcServerApplication(rpcApiPackages = {"org.example.communication.server.api"})
public class ServerTest {

    public static void main(String[] args) throws Exception {
        RpcServer.run(ServerTest.class);
        Thread.sleep(15000);
        testRPCFile("D:\\斗罗之我的系统又不服气了.txt");
    }

    private static void testRPC() throws Exception {
        SystemInfoService proxy = (SystemInfoService) Factory.getBean(SystemInfoService.class);
        Thread.sleep(15000);
        String result = proxy.sayHello("127.0.0.1");
        System.out.println(result);
    }

    private static void testRPCFile(String filePath) {
        RpcFile file = new RpcFile("127.0.0.1", filePath);
        File local = FileUtil.file("C:\\Users\\luoyp\\Desktop\\斗罗之我的系统又不服气了.txt");
        long start = currentTimeMillis();
        System.out.println("start download");
        FileUtil.writeFromStream(file.inputStream(), local);
        System.out.println("download success,cost %d" + (currentTimeMillis() - start));
    }
}
