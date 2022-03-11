package io.nuls.api.rpc.rest.utils;

import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020/8/10 17:52
 * @Description: 功能描述
 */
public abstract class BasePriceProvider implements PriceProvider {

    public static final int TIMEOUT_MILLIS = 30000;

    protected String url;

    public BasePriceProvider(String url) {
        this.url = url;
    }

    public Map<String, Object> httpRequest(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
//                .setSSLHostnameVerifier((hostName, sslSession) -> {
//            return true; // 证书校验通过
//        })
                .build();

        HttpGet httpGet = new HttpGet(url);
//        本地调试专用
//        HttpHost proxy = new HttpHost("127.0.0.1",1080);
//        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT_MILLIS)
//                .setSocketTimeout(TIMEOUT_MILLIS).setConnectTimeout(TIMEOUT_MILLIS).setProxy(proxy).build();

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT_MILLIS)
                .setSocketTimeout(TIMEOUT_MILLIS).setConnectTimeout(TIMEOUT_MILLIS).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String dataStr = EntityUtils.toString(entity);
                Map<String, Object> data = JSONUtils.jsonToMap(dataStr);
                return data;
            }
            Log.warn("调用接口:{} 异常, StatusCode:{}", url, response.getStatusLine().getStatusCode());
            return null;
        } catch (IOException e) {
            Log.warn("调用接口:{} 异常1, {}", url, e.getMessage());
            return null;
        } catch (Exception e) {
            Log.warn("调用接口:{} 异常2, {}", url, e.getMessage());
            return null;
        }
    }


}
