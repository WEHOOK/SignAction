package com.smith;

import com.alibaba.fastjson.JSONObject;
import com.smith.util.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignApplication {


    /**
     * 获取日志记录器对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SignApplication.class);

    private static final Integer SUCCESSCODE = 200;
    private static final String STATUSCODE = "StatusCode";
    StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args) {

        // 获取账户String
        String accountStrList = args[0];

        String LOGIN = args[1];
        String SIGN = args[2];
        String sckey = args[3];

        // 通过|分隔，获取每个账户
        String[] accountList = accountStrList.split("\\|");
        SignApplication signApplication = new SignApplication();
        for (String s : accountList) {
//            LOGGER.info("当前签到用户：{}", s);
            signApplication.sign(s, LOGIN, SIGN);
        }
        signApplication.send(sckey);

    }

    public void sign(String s, String LOGIN, String SIGN) {
        // 通过，分隔获取账户用户名与密码
        String[] account = s.split(",");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        parameters.add(new BasicNameValuePair("client", "0"));
        parameters.add(new BasicNameValuePair("nextAutoLogin", "0"));
        parameters.add(new BasicNameValuePair("account", account[0]));
        parameters.add(new BasicNameValuePair("pass", account[1]));


        JSONObject loginRes = Request.post(LOGIN + System.currentTimeMillis(),
                parameters.toString());
        LOGGER.info("登录结果：{}", loginRes);

        if (SUCCESSCODE.equals(loginRes.get(STATUSCODE))) {
            String token = loginRes.getString("tk");
            JSONObject signRes = Request.get(SIGN, token);
            LOGGER.info("签到结果：{}", signRes);
            stringBuilder.append("账号" + account[0] + "登录成功");
            stringBuilder.append("\r\n");
            stringBuilder.append("签到结果" + signRes);
            stringBuilder.append("\r\n");
        } else {
            stringBuilder.append("账号" + account[0] + "登录失败");
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
//        String text = "总: " + followNum + " - ";
//        text += "成功: " + success.size() + " 失败: " + (followNum - success.size());
//        String desp = "共 " + followNum + " 贴吧\n\n";
//        desp += "成功: " + success.size() + " 失败: " + (followNum - success.size());

//        String body = "desp:" + stringBuilder.toString();

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
            LOGGER.error("server酱发送失败 -- " + e);
        }
    }


}
