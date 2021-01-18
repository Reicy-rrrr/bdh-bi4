package com.deloitte.bdh.common.util;

import java.net.InetAddress;

/**
 * UUID工具类
 *
 * @author dahpeng
 * @date 2019/05/22
 */
public class UUIDUtil {

    private static final int IP;
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
    private static short counter = (short) 0;

static {
    int ipadd;
    try {
    ipadd = toInt(InetAddress.getLocalHost().getAddress());
    } catch (Exception e) {
    ipadd = 0;
    }
    IP = ipadd;
    }

/**
 * 产生一个32位的UUID
 */

public static String generate() {
    return new StringBuilder(32).append(format(getIP())).append(
    format(getJVM())).append(format(getHiTime())).append(
    format(getLoTime())).append(format(getCount())).toString();

    }

/***
 * JDK 方式：随机产生32位16进制字符串
 *
 * @return
 */
public static String getRandom32PK()
    {

    return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

private final static String format(int intval) {
    String formatted = Integer.toHexString(intval);
    StringBuilder buf = new StringBuilder("00000000");
    buf.replace(8 - formatted.length(), 8, formatted);
    return buf.toString();
    }

private final static String format(short shortval) {
    String formatted = Integer.toHexString(shortval);
    StringBuilder buf = new StringBuilder("0000");
    buf.replace(4 - formatted.length(), 4, formatted);
    return buf.toString();
    }

private final static int getJVM() {
    return JVM;
    }

private final static short getCount() {
synchronized (UUIDUtil.class) {
    if (counter < 0) {
    counter = 0;
    }
    return counter++;
    }
    }

/**
 * Unique in a local network
 */
private final static int getIP() {
    return IP;
    }

/**
 * Unique down to millisecond
 */
private final static short getHiTime() {
    return (short) (System.currentTimeMillis() >>> 32);
    }

private final static int getLoTime() {
    return (int) System.currentTimeMillis();
    }

private final static int toInt(byte[] bytes) {
    int result = 0;
    for (int i = 0; i < 4; i++) {
    result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
    }
    return result;
    }


    public static void main(String[] args) {
        System.out.println(UUIDUtil.getLoTime());
    }
}
