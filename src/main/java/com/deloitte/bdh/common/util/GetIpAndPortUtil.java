package com.deloitte.bdh.common.util;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class GetIpAndPortUtil {

    //获取ip
    public static String getLocalIP() throws SocketException {
        for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    return "http://" + address.getHostAddress();
                }
            }
        }
        throw new RuntimeException("获取IP失败");
    }

    //通过request获取ip
    public static String getIp() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServerName();
    }

    //通过request获取端口
    public static String getLocalPort() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServerPort() + "";
    }


    public static String getIpAndPort() throws SocketException {
        return getLocalIP() + ":" + getLocalPort();
    }

}
