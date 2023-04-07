package org.example.communication.impl;

import org.example.communication.interfaces.RuntimeInfoService;

public class RuntimeInfoServiceImpl implements RuntimeInfoService {

    @Override
    public String sayHello(String remote) {
        System.out.println("我被反射调用了");
        return "hello";
    }
}
