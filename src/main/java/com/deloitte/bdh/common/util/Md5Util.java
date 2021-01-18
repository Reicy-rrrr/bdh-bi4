package com.deloitte.bdh.common.util;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {

    public static String getMD5(String inputPass) {
        return md5(inputPass);
    }

    public static String getMD5(String str, String salt) {
        return salt(getMD5(str), salt);
    }

    private static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static String salt(String formPass, String salt) {
        if (null == salt || "".equals(salt)) {
            throw new RuntimeException("MD5Util.salt.error: salt is empty");
        }
        salt = "" + salt + salt.length();
        String str = "" + salt + salt.length() + formPass + salt.length() + 1;
        return md5(str);
    }

    public static void main(String[] args) {
        System.out.println(getMD5("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(getMD5("123456", "1"));//b7797cce01b4b131b433b6acf4add449
    }
}
