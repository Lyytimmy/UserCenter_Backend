package com.example.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;

    @Bean
    public RedissonClient redissonClient(){
        // 1. 创建配置
        Config config = new Config();
        // useClusterServers集群
        // useSingleServer单个
        String redisAdress = String.format("redis://%s:%s", host, port);
        config.useSingleServer()
                .setAddress(redisAdress) // 设置地址
                .setDatabase(1); // 设置库号

        // 2. 创建实例
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
