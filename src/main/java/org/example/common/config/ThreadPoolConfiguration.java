package org.example.common.config;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import org.example.common.annotation.Bean;
import org.example.common.annotation.Configuration;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public EventExecutor defaultEventExecutor() {
        return new DefaultEventExecutor();
    }
}
