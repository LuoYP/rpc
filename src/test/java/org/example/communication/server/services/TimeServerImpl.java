package org.example.communication.server.services;

import org.example.common.annotation.RpcService;
import org.example.communication.client.api.TimeServer;

import java.time.LocalDateTime;

@RpcService
public class TimeServerImpl implements TimeServer {
    @Override
    public String now() {
        return LocalDateTime.now().toString();
    }
}
