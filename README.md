# RPC
#### Server
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
```java
@RpcClientApplication(rpcApiPackages = "your.rpc.api.package")
public class ClientTest {

    public static void main(String[] args) {
        RpcClient.run(ClientTest.class);
        TimeServer timeServer = (TimeServer)Factory.BEAN_WAREHOUSE.get(TimeServer.class);
        System.out.println(timeServer.now());
    }
}
```