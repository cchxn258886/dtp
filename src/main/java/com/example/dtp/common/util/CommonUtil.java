package com.example.dtp.common.util;

import com.example.dtp.common.ApplicationContextHolder;
import com.example.dtp.entity.ServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @Author chenl
 * @Date 2023/5/12 2:05 下午
 */
@Slf4j
public class CommonUtil {
    private CommonUtil() {
    }

    ;
    private static final ServiceInstance SERVICE_INSTANCE;

    static {
        Environment environment = ApplicationContextHolder.getEnvironment();
        String appName = environment.getProperty("spring.application.name");
        appName = StringUtils.isBlank(appName) ? appName : "application";
        String portStr = environment.getProperty("server.port");
        int port = StringUtils.isBlank(portStr) ? Integer.parseInt(portStr) : 0;
        String address = null;
        try {
            address = getLocalHostExactAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            log.error("get localHost address err.", e);
        }
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length < 1) {
            activeProfiles = environment.getDefaultProfiles();
        }
        SERVICE_INSTANCE = new ServiceInstance(address, port, appName, activeProfiles[0]);

    }

    public static ServiceInstance getServiceInstance() {
        return SERVICE_INSTANCE;
    }


    private static InetAddress getLocalHostExactAddress() throws SocketException, UnknownHostException {
        InetAddress candidateAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                    if (!networkInterface.isPointToPoint()) {
                        return inetAddress;
                    } else {
                        candidateAddress = inetAddress;
                    }
                }
            }
        }
        return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
    }
}
