package com.deloitte.bdh.common.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.deloitte.bdh.common.json.JsonUtil;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES 工具类
 *
 * @author dahpeng
 */
public class AesUtil {

    private static final Logger logger = LoggerFactory.getLogger(AesUtil.class);

    private static final String KEY_ALGORITHM = "AES";
    // 默认的加密算法
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/GCM/PKCS5Padding";

    /**
     * AES 加密操作
     *
     * @param content     待加密内容
     * @param encryptPass 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String encryptPass) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(encryptPass));
            byte[] iv = cipher.getIV();
            assert iv.length == 12;
            byte[] encryptData = cipher.doFinal(content.getBytes());
            assert encryptData.length == content.getBytes().length + 16;
            byte[] message = new byte[12 + content.getBytes().length + 16];
            System.arraycopy(iv, 0, message, 0, 12);
            System.arraycopy(encryptData, 0, message, 12, encryptData.length);
            return Base64.encodeBase64String(message);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * AES 加密操作(无特殊符号)
     *
     * @param content     待加密内容
     * @param encryptPass 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encryptNoSymbol(String content, String encryptPass) {
        String str = encrypt(content, encryptPass);
        assert str != null;
        return str.replace("=", "O0O0O")
                .replace("+", "o000o")
                .replace("/", "oo00o");
    }

    /**
     * AES 解密操作
     */
    public static String decrypt(String base64Content, String encryptPass) {
        byte[] content = Base64.decodeBase64(base64Content);
        if (content.length < 12 + 16) {
            throw new IllegalArgumentException();
        }
        GCMParameterSpec params = new GCMParameterSpec(128, content, 0, 12);
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(encryptPass), params);
            byte[] decryptData = cipher.doFinal(content, 12, content.length - 12);
            return new String(decryptData);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String decryptNoSymbol(String base64Content, String encryptPass) {
        assert base64Content != null;
        base64Content = base64Content
                .replace("O0O0O", "=")
                .replace("o000o", "+")
                .replace("oo00o", "/");
        return decrypt(base64Content, encryptPass);
    }

    /**
     * 生成加密秘钥
     */
    private static SecretKeySpec getSecretKey(String encryptPass) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
        kg.init(128, new SecureRandom(encryptPass.getBytes()));
        SecretKey secretKey = kg.generateKey();
        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    public static void main(String[] args) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tenantCode", "0001");
        params.put("refId", "123");
        params.put("isEncrypt", "0");

        String pass = "1";
        String encoded = encryptNoSymbol(JsonUtil.readObjToJson(params), pass);

        logger.info("加密之前：{}", JsonUtil.readObjToJson(params));
        logger.info("加密结果：{}", encoded);

        logger.info("解密结果：{}", decryptNoSymbol(encoded, pass));
    }
}
