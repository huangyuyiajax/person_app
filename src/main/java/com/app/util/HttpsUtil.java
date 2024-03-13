package com.app.util;

import com.alibaba.fastjson.JSONObject;
import com.app.medel.Lottery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * @description: TODO
 * @author huangyuyi
 * @date 2023/4/21 10:52
 * @version 1.0
 */
@Slf4j
public class HttpsUtil {

    public HttpsUtil() {
    }

    public static JSONObject httpGet(String url) {
        log.info("获取开码地址："+url);
        JSONObject resJson = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("User-Agent", "Mozilla/5.0");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != ConfigUtils.CODE_200) {
                httpGet.abort();
                log.info("异常："+statusCode);
            }

            HttpEntity resEntity = httpResponse.getEntity();
            if (resEntity == null) {
                log.info("没有数据返回");
            }

            String result = EntityUtils.toString(resEntity, "UTF-8");
            log.info("获取开码结果："+result);
            resJson = JSONObject.parseObject(result);
        } catch (ClientProtocolException e) {
            log.info("提交给服务器的请求，不符合HTTP 协议" + e.getMessage(), e);
        } catch (IOException e) {
            log.info("向服务器接口发起http 请求,执行post 请求异常" + e.getMessage(), e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resJson;
    }
    public static void main(String[] args) {
        JSONObject resJon = HttpsUtil.httpGet("https://374445.com/kj/xg.js");
        if(resJon==null){
            return;
        }
        String k = resJon.getString("k");
        if(StringUtils.isEmpty(k)){
            return;
        }
        String nowName = k.split(",")[0];
        Lottery lottery = new Lottery();
        String code = k.split(",")[7];
        lottery.setName(k.split(",")[8]);
        lottery.setDate(k.split(",")[8].substring(0,4)+"-"+k.split(",")[9]+"-"+k.split(",")[10]);
        System.out.println(code);
        System.out.println(nowName);
        System.out.println(lottery);
    }

}
