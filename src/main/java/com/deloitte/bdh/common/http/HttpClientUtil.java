package com.deloitte.bdh.common.http;


import com.deloitte.bdh.common.json.JsonUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * function: APACHE HTTP-CLIENT 封装工具
 *
 * @author dahpeng
 */
public class HttpClientUtil {

    private static final int timeout = 10000;
    private static final boolean defaultRedirectsEnabled = false;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";

    private HttpClientUtil() {

    }


    /**
     * function:发送get请求
     *
     * @param url     请求地址
     * @param headers 头信息
     * @param params  参数
     * @return String
     */
    public static String get(String url, Map<String, Object> headers, Map<String, Object> params) throws IOException {
        HttpGet httpGet = new HttpGet(createParamUrl(url, params));
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return getResult(httpGet);
    }


    /**
     * function:创建带参数的URL
     *
     * @param url    无参URL
     * @param params 参数
     * @return String 带参数URL
     */
    private static String createParamUrl(String url, Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            return url;
        }
        Iterator<String> it = params.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        boolean isIncludeQuestionMark = url.contains("?");
        if (!isIncludeQuestionMark) {
            sb.append("?");
        }
        while (it.hasNext()) {
            String key = it.next();
            String value = (String) params.get(key);
            sb.append("&");
            sb.append(key);
            sb.append("=");
            sb.append(value);
        }
        url += sb.toString();
        return url;
    }

    /**
     * function:post请求访问
     *
     * @param url    地址
     * @param params 参数
     * @return String
     */
    public static String httpPostRequest(String url, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw e;
        }
        return getResult(httpPost);
    }



    /**
     * function:post以JSON格式发送
     *
     * @param url     地址
     * @param headers 头信息
     * @param params  参数
     * @return String
     */
    public static String post(String url, Map<String, Object> headers, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, Object> headerParam : headers.entrySet()) {
                httpPost.addHeader(headerParam.getKey(), String.valueOf(headerParam.getValue()));
            }
        }

        try {
            String json = JsonUtil.readMapToJson(params);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
            throw e;
        }
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        return getResult(httpPost);
    }

    /**
     * function:put以JSON格式发送
     *
     * @param url     地址
     * @param headers 头信息
     * @param params  参数
     * @return String
     */
    public static String put(String url, Map<String, Object> headers,
                             Map<String, Object> params) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        for (Map.Entry<String, Object> headerParam : headers.entrySet()) {
            httpPut.addHeader(headerParam.getKey(), String.valueOf(headerParam.getValue()));
        }
        try {
            String json = JsonUtil.readMapToJson(params);
            httpPut.setEntity(new StringEntity(json, "UTF-8"));
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
            throw e;
        }
        httpPut.setHeader("Content-Type", "application/json; charset=UTF-8");
        return getResult(httpPut);
    }

    /**
     * function:DELETE以JSON格式发送
     *
     * @param url     地址
     * @param headers 头信息
     * @param params  参数
     * @return String
     */
    public static String delete(String url, Map<String, Object> headers) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        for (Map.Entry<String, Object> headerParam : headers.entrySet()) {
            httpDelete.addHeader(headerParam.getKey(), String.valueOf(headerParam.getValue()));
        }
        httpDelete.setHeader("Content-Type", "application/json; charset=UTF-8");
        return getResult(httpDelete);
    }


    /**
     * post以JSON格式发送,返回 HTTPResponse
     *
     * @param url
     * @param headers
     * @param jsonParam
     * @return
     */
    public static CloseableHttpResponse httpPostRequestByJsonAndReturnResponse(String url, Map<String, Object> headers,
                                                                               String jsonParam) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, Object> headerParam : headers.entrySet()) {
            httpPost.addHeader(headerParam.getKey(), String.valueOf(headerParam.getValue()));
        }
        try {
            httpPost.setEntity(new StringEntity(jsonParam, "UTF-8"));
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
            throw e;
        }
        return getResponseResult(httpPost);
    }

    /**
     * @param url
     * @param headers
     * @return
     */
    public static CloseableHttpResponse httpGetRequestWithHeadersAndReturnResponse(String url, Map<String, Object> headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResponseResult(httpGet);
    }

    /**
     * function:post以JSON格式发送
     *
     * @param url     地址
     * @param headers 头信息
     * @param params  参数
     * @return HttpResponse
     */
    public static CloseableHttpResponse httpPostRequestByJsonAndReturnResponse(String url,
                                                                               Map<String, Object> headers,
                                                                               Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, Object> headerParam : headers.entrySet()) {
                httpPost.addHeader(headerParam.getKey(), String.valueOf(headerParam.getValue()));
            }
        }

        try {
            String json = JsonUtil.readMapToJson(params);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
            throw e;
        }
        return getResponseResult(httpPost);
    }

    /**
     * function:把参数转换为名值对数组
     *
     * @param params 参数
     * @return ArrayList<NameValuePair>
     */
    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }


    /**
     * 执行 HTTP请求
     *
     * @return String 若重定向返回重定向地址
     */
    private static String getResult(HttpRequestBase request) throws IOException {
        String result = EMPTY_STR;
        request.setConfig(createConfig(timeout, defaultRedirectsEnabled));
//        request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        CloseableHttpClient httpClient = getHttpClient();

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            result = getEntityData(response);


            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                result = getRedirectedUrl(response);
            } else if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
//                result = getEntityData(response);
            } else {
                throw new RuntimeException(String.format("调用外部接口报错,错误码:%s,描述:%s", statusCode, result));
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 执行 HTTP请求
     *
     * @return String 若重定向返回重定向地址
     */
    private static CloseableHttpResponse getResponseResult(HttpRequestBase request) throws IOException {
        request.setConfig(createConfig(timeout, defaultRedirectsEnabled));
        request.setHeader("Content-Type", "application/json; charset=UTF-8");
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return response;
    }

    /**
     * function:创建HTTP请求配置
     *
     * @param timeout          超时
     * @param redirectsEnabled 是否开启重定向
     * @return RequestConfig
     */
    private static RequestConfig createConfig(int timeout, boolean redirectsEnabled) {
        return RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setRedirectsEnabled(redirectsEnabled)
                .build();
    }

    /**
     * 通过连接池获取HttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(
                HttpConnectionManager.POOLING_CONNECTION_MANAGER).build();
    }


    /**
     * function:判断发送请求是否重定向跳转过
     *
     * @param response 请求响应
     * @return boolean
     */
    private static boolean isRedirected(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY;
    }

    /**
     * function:获得重定向跳转地址
     *
     * @param response 请求响应
     * @return String 重定向地址
     */
    private static String getRedirectedUrl(CloseableHttpResponse response) {
        String result = EMPTY_STR;
        Header[] hs = response.getHeaders("Location");
        if (hs.length > 0) {
            result = hs[0].getValue();
        }
        return result;
    }

    /**
     * function:获得响应实体信息
     *
     * @param response 请求响应
     * @return String 消息实体信息
     */
    private static String getEntityData(CloseableHttpResponse response)
            throws ParseException, IOException {
        String result = EMPTY_STR;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            result = EntityUtils.toString(entity);
            response.close();
        }
        return result;
    }

}