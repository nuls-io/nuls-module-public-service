package io.nuls.api.cache.task.utls;

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
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2020/8/10 17:52
 * @Description: 功能描述
 */
public abstract class BasePriceProvider implements PriceProvider{

    public static final Map<String,String> ALIAS = new HashMap<>();

    static {
        ALIAS.put("CNVT","NVT");
        ALIAS.put("TNVT","NVT");
        ALIAS.put("AETH","ETH");
        ALIAS.put("OETH","ETH");
        ALIAS.put("BCHT","BCH");
    }

    public static final int TIMEOUT_MILLIS = 15000;

    protected String url;

    protected Map<String,Double> weight;

    public Map<String, Object> httpRequest(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(url);
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
            Log.error("调用接口:{} 异常, StatusCode:{}", url, response.getStatusLine().getStatusCode());
            return null;
        } catch (IOException e) {
            Log.error("调用接口:{} 异常, {}", url, e.getMessage());
            return null;
        } catch (Exception e){
            Log.error("调用接口:{} 异常, {}", url, e.getMessage());
            return null;
        }
    }
    public String realHttpRequest(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT_MILLIS)
                .setSocketTimeout(TIMEOUT_MILLIS).setConnectTimeout(TIMEOUT_MILLIS).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return  EntityUtils.toString(entity);
            }
            Log.error("调用接口:{} 异常, StatusCode:{}", url, response.getStatusLine().getStatusCode());
            return null;
        } catch (IOException e) {
            Log.error("调用接口:{} 异常, {}", url, e.getMessage());
            return null;
        } catch (Exception e){
            Log.error("调用接口:{} 异常, {}", url, e.getMessage());
            return null;
        }
    }
    @Override
    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public void setWeight(Map<String, Double> weight) {
        this.weight = weight;
    }

    @Override
    public Double getWeight(String symbol){
        return weight.getOrDefault(symbol,1D);
    }


}
