package com.smith.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * 封装的网络请求请求工具类
 *
 * @author srcrs
 * @Time 2020-10-31
 */
public class Request {
    /**
     * 获取日志记录器对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    /**
     * 获取Cookie对象
     */
//    private static Cookie cookie = Cookie.getInstance();
    private Request() {
    }

    ;

    /**
     * 发送get请求
     *
     * @param url 请求的地址，包括参数
     * @return JSONObject
     * @author srcrs
     * @Time 2020-10-31
     */
    public static JSONObject get(String url, String token) {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
//        LOGGER.info("请求地址：{}", url);
//        httpGet.addHeader("connection", "keep-alive");
//        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        httpGet.addHeader("charset", "UTF-8");
        httpGet.setHeader("BIGAN_LOGIN_TOKEN", token);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        HttpResponse resp = null;
        String respContent = null;
        try {
            resp = client.execute(httpGet);
            HttpEntity entity = null;
            if (resp.getStatusLine().getStatusCode() < 400) {
                entity = resp.getEntity();
            } else {
                entity = resp.getEntity();
            }
            respContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            LOGGER.info("get请求错误 -- " + e);
        } finally {
            return JSONObject.parseObject(respContent);
        }
    }

    /**
     * 发送post请求
     *
     * @param url  请求的地址
     * @param body 携带的参数
     * @return JSONObject
     * @author srcrs
     * @Time 2020-10-31
     */
    public static JSONObject post(String url, List<NameValuePair> body) throws UnsupportedEncodingException {
        UrlEncodedFormEntity entityBody = new UrlEncodedFormEntity(body, "UTF-8");
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url + System.currentTimeMillis());
        LOGGER.info("请求地址：{}", url + System.currentTimeMillis());
        httpPost.addHeader("connection", "keep-alive");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("charset", "UTF-8");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        httpPost.setEntity(entityBody);
        HttpResponse resp = null;
        String respContent = null;
        try {
            resp = client.execute(httpPost);
            HttpEntity entity = null;
            if (resp.getStatusLine().getStatusCode() < 400) {
                entity = resp.getEntity();
            } else {
                entity = resp.getEntity();
            }
            respContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            LOGGER.info("post请求错误 -- " + e);
        } finally {
            return JSONObject.parseObject(respContent);
        }
    }
}

