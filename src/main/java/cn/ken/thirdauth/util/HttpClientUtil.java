package cn.ken.thirdauth.util;

import cn.ken.thirdauth.enums.AuthExceptionCode;
import cn.ken.thirdauth.exception.AuthException;
import cn.ken.thirdauth.model.AuthGet;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * http请求工具包
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @date 2023/2/11 15:01
 */
public class HttpClientUtil {

    /**
     * 使用HttpClient发送一个Get方式的请求
     *
     * @param url 请求的路径 请求参数拼接到url后面
     * @return 响应的数据
     */
    public static String doGet(String url) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
    }

    public static String doPost(String url) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
    }
    
    public static String doAuthGet(AuthGet authGet) {
        if (authGet.getHeaders() != null) {
            return doGetWithHeaders(authGet.getUrl(), authGet.getHeaders());
        } else {
            return doGet(authGet.getUrl());
        }
    }

    /**
     * 使用HttpClient发送一个带请求头的Get方式的请求
     *
     * @param url     请求的路径 请求参数拼接到url后面
     * @param headers 需要添加的请求头
     * @return 响应的数据
     */
    public static String doGetWithHeaders(String url, Map<String, String> headers) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                httpGet.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionCode.REQUEST_ERROR);
        }
    }

    /**
     * 参数的封装
     *
     * @param responseEntityStr 请求的返回值
     * @return 封装后的键值对
     */
    public static Map<String, String> parseResponseEntity(String responseEntityStr) {
        if (responseEntityStr == null) {
            throw new NullPointerException();
        }
        Map<String, String> map = new HashMap<>();
        String[] strs = responseEntityStr.split("&");
        for (String str : strs) {
            String[] mapStrs = str.split("=");
            String value = null;
            String key = mapStrs[0];
            if (mapStrs.length > 1) {
                value = mapStrs[1];
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * json返回值转map
     *
     * @param responseEntityStr 待转换的Json字符串
     * @return 封装后的键值对
     */
    public static Map<String, String> parseResponseEntityJson(String responseEntityStr) {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(responseEntityStr);
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            map.put(key, value);
        }
        return map;
    }
}
