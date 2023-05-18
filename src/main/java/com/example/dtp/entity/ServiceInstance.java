package com.example.dtp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2023/5/12 2:08 下午
 */
@Data
@AllArgsConstructor
public class ServiceInstance {
    private String ipAddr;
    private int port;
    private String serviceName;
    private String env;
}
