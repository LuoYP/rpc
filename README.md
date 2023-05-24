# RPC
+ 支持多类型消息传输:字符串文本，文件，音频，视频，图片
+ 支持多协议传输消息:UDP,TCP


#### Server
___
```java
@RpcServerApplication(rpcApiPackages = {"your.rpc.api.package"})
public class Application {

    public static void main(String[] args) {
        RpcServer.run(Application.class);
        SystemInfoService proxy = (SystemInfoService)Factory.getBean(SystemInfoService.class);
        System.out.println(proxy.sayHello("remote-ip(client-IP)"));
    }
}
```

#### Client
___
```java
@RpcClientApplication(rpcApiPackages = "your.rpc.api.package")
public class Application {

    public static void main(String[] args) {
        RpcClient.run(Application.class);
        TimeServer timeServer = (TimeServer)Factory.getBean(TimeServer.class);
        System.out.println(timeServer.now());
    }
}
```

#### File
___
```java
@RpcServerApplication(rpcApiPackages = "your.rpc.api.package")
public class Application {

    public static void main(String[] args) {
        RpcServer.run(Application.class);
        Thread.sleep(15000);
        RpcFile remoteFile = new RpcFile("127.0.0.1", "remote-file-path");
        File local = FileUtil.file("local-file-path");
        //hutool
        FileUtil.writeFromStream(new RpcFileInputStream(remoteFile), local);
    }
}
```