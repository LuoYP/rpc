package org.example.communication.client.services;

import org.example.common.annotation.RpcService;
import org.example.communication.server.api.SystemInfoService;

@RpcService
public class SystemInfoServiceImpl implements SystemInfoService {
    @Override
    public String sayHello(String remote) {
        return "hello server, this is client";
    }
}
