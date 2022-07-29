package io.nuls.api.rpc.rest.utils;

import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
        SSLContext sslcontext = createIgnoreVerifySSL();
// 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);
//        CloseableHttpClient httpClient = HttpClientBuilder.create()
////                .setSSLHostnameVerifier((hostName, sslSession) -> {
////            return true; // 证书校验通过
////        })
//                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();

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
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.warn("调用接口:{} 异常2, {}", url, e.getMessage());
            return null;
        }
    }

    public static SSLContext createIgnoreVerifySSL() {
        SSLContext sslContext = null;// 创建套接字对象
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");//指定TLS版本
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 实现X509TrustManager接口，用于绕过验证
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        try {
            sslContext.init(null, new TrustManager[] { trustManager }, null);//初始化sslContext对象
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }


}
