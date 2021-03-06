package com.smith;

import com.alibaba.fastjson.JSONObject;
import com.smith.util.EncryptUtil;
import com.smith.util.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SignApplication {


    /**
     * 获取日志记录器对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SignApplication.class);

    private static final Integer SUCCESSCODE = 0;
    private static final String STATUSCODE = "code";
    private static final String TOKEN = "tk";
    StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args) throws UnsupportedEncodingException {

        // 获取账户String
        String accountStrList = args[0];

        String LOGIN = args[1];
        String SIGN = args[2];
        String sckey = args[3];

        // 通过|分隔，获取每个账户
        String[] accountList = accountStrList.split("\\|");
        SignApplication signApplication = new SignApplication();
        for (String s : accountList) {

            signApplication.sign(s, LOGIN, SIGN);
        }
        signApplication.send(sckey);


    }

    public void sign(String s, String LOGIN, String SIGN) throws UnsupportedEncodingException {
        // 通过，分隔获取账户用户名与密码
        String[] account = s.split(",");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        parameters.add(new BasicNameValuePair("client", "0"));
        parameters.add(new BasicNameValuePair("nextAutoLogin", "0"));
        parameters.add(new BasicNameValuePair("account", account[0]));
        parameters.add(new BasicNameValuePair("pass", account[1]));
        CloseableHttpClient client = HttpClients.createDefault();


        JSONObject loginRes = Request.post(LOGIN,
                parameters, client);
        if (SUCCESSCODE.equals(loginRes.get(STATUSCODE))) {
            LOGGER.info("{}登录成功", EncryptUtil.alipayAccountEncrypt(account[0]));

            JSONObject dataObject = JSONObject.parseObject(loginRes.getString("data"));
            String token = dataObject.getString(TOKEN);

            JSONObject signRes = Request.get(SIGN, token, client);

            LOGGER.info("账号：{}，签到结果：{}", EncryptUtil.alipayAccountEncrypt(account[0]), signRes);
            stringBuilder.append("\r\n");
            stringBuilder.append("账号" + EncryptUtil.alipayAccountEncrypt(account[0]) + "登录成功");
            stringBuilder.append("\r\n");
            stringBuilder.append("签到结果" + signRes);
            stringBuilder.append("\r\n");
        } else {
            LOGGER.info("{}登录失败",EncryptUtil.alipayAccountEncrypt(account[0]));
            stringBuilder.append("\r\n");
            stringBuilder.append("账号" + EncryptUtil.alipayAccountEncrypt(account[0]) + "登录失败");
            stringBuilder.append("\r\n");
            stringBuilder.append("失败原因" + loginRes);
            stringBuilder.append("\r\n");
        }
    }


    /**
     * 发送运行结果到微信，通过 server 酱
     *
     * @param sckey
     * @author srcrs
     * @Time 2020-10-31
     */
    public void send(String sckey) {
        /** 将要推送的数据 */

        String body = "title=签到结果" + "&desp=" + stringBuilder.toString();
        StringEntity entityBody = new StringEntity(body, "UTF-8");
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://sctapi.ftqq.com/" + sckey + ".send");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
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
            LOGGER.info("server酱推送正常");
        } catch (Exception e) {
            LOGGER.error("server酱发送失败:{}", e.getMessage());
        }
    }


}
