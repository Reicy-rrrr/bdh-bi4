package com.deloitte.bdh.common.http;

import com.deloitte.bdh.common.util.StringUtil;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;


/**
 * Http请求
 *
 * @author mszhou
 */
public class HttpsClientUtil {
    private static final int TIMEOUT = 45000;
    public static final String ENCODING = "UTF-8";

    /**
     * 创建HTTP连接
     *
     * @param url              地址
     * @param method           方法
     * @param headerParameters 头信息
     * @param body             请求内容
     * @return
     * @throws Exception
     */
    private static HttpURLConnection createConnection(String url, String method, Map<String, String> headerParameters, String body, String token)
            throws Exception {
        URL Url = new URL(url);
        trustAllHttpsCertificates();
        HttpURLConnection httpConnection = (HttpURLConnection) Url.openConnection();
        // 设置请求时间
        httpConnection.setConnectTimeout(TIMEOUT);
        // 设置 header
        if (headerParameters != null) {
            Iterator<String> iteratorHeader = headerParameters.keySet()
                    .iterator();
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                httpConnection.setRequestProperty(key, headerParameters.get(key));
            }
        }

        httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + ENCODING);
        if (!StringUtil.isEmpty(token)) {
            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("User-Agent", "imgfornote");
        }
        // 设置请求方法
        httpConnection.setRequestMethod(method);
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // 写query数据流
        if (!(body == null || body.trim().equals(""))) {
            OutputStream writer = httpConnection.getOutputStream();
            try {
                writer.write(body.getBytes(ENCODING));
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }

        // 请求结果
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != 200 && responseCode != 201) {
            throw new Exception(responseCode
                    + ":"
                    + inputStream2String(httpConnection.getErrorStream(),
                    ENCODING));
        }

        return httpConnection;
    }

    /**
     * POST请求
     *
     * @param address          请求地址
     * @param headerParameters 参数
     * @param body
     * @return
     * @throws Exception
     */
    public static String post(String address, Map<String, Object> headerParameters, String body, String token) throws Exception {
        return proxyHttpRequest(address, "POST", null, getRequestBody(headerParameters), token);
    }

    /**
     * GET请求
     *
     * @param address
     * @param headerParameters
     * @param body
     * @return
     * @throws Exception
     */
    public static String get(String address, Map<String, Object> headerParameters, String body, String token) throws Exception {
        return proxyHttpRequest(address + "?" + getRequestBody(headerParameters), "GET", null, null, token);
    }

    /**
     * 读取网络文件
     *
     * @param address
     * @param headerParameters
     * @param file
     * @return
     * @throws Exception
     */
    public static String getFile(String address, Map<String, Object> headerParameters, File file) throws Exception {
        String result = "fail";
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters), null);
            result = readInputStream(httpConnection.getInputStream(), file);

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    public static byte[] getFileByte(String address, Map<String, Object> headerParameters) throws Exception {
        byte[] result = null;
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = createConnection(address, "POST", null,
                    getRequestBody(headerParameters), null);
            result = readInputStreamToByte(httpConnection.getInputStream());

        } catch (Exception e) {
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return result;
    }

    /**
     * 读取文件流
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String readInputStream(InputStream in, File file)
            throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }

            out = new FileOutputStream(file);
            out.write(output.toByteArray());

        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    public static byte[] readInputStreamToByte(InputStream in) throws Exception {
        FileOutputStream out = null;
        ByteArrayOutputStream output = null;
        byte[] byteFile = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            byteFile = output.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (out != null) {
                out.close();
            }
        }

        return byteFile;
    }

    /**
     * HTTP请求
     *
     * @param address          地址
     * @param method           方法
     * @param headerParameters 头信息
     * @param body             请求内容
     * @return
     * @throws Exception
     */
    public static String proxyHttpRequest(String address, String method, Map<String, String> headerParameters, String body, String token) throws Exception {
        String result = null;
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createConnection(address, method, headerParameters, body, token);
            String encoding = "UTF-8";
            if (httpConnection.getContentType() != null
                    && httpConnection.getContentType().indexOf("charset=") >= 0) {
                encoding = httpConnection.getContentType().substring(httpConnection.getContentType().indexOf("charset=") + 8);
            }
            result = inputStream2String(httpConnection.getInputStream(),
                    encoding);
            // logger.info("HTTPproxy response: {},{}", address,
            // result.toString());

        } catch (Exception e) {
            // logger.info("HTTPproxy error: {}", e.getMessage());
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 将参数化为 body
     *
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, Object> params) {
        return getRequestBody(params, true);
    }

    /**
     * 将参数化为 body
     *
     * @param params
     * @return
     */
    public static String getRequestBody(Map<String, Object> params, boolean urlEncode) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        StringBuilder body = new StringBuilder();

        Iterator<String> iteratorHeader = params.keySet().iterator();
        while (iteratorHeader.hasNext()) {
            String key = iteratorHeader.next();
            String value = (String) params.get(key);

            if (urlEncode) {
                try {
                    body.append(key + "=" + URLEncoder.encode(value, ENCODING)
                            + "&");
                } catch (UnsupportedEncodingException e) {
                    // e.printStackTrace();
                }
            } else {
                body.append(key + "=" + value + "&");
            }
        }

        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }

    /**
     * 读取inputStream 到 string
     *
     * @param input
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String inputStream2String(InputStream input, String encoding)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                encoding));
        StringBuilder result = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            result.append(temp);
        }

        return result.toString();

    }


    /**
     * 设置 https 请求
     *
     * @throws Exception
     */
    private static void trustAllHttpsCertificates() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier((str, session) -> true);
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }


    //设置 https 请求证书
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

}
