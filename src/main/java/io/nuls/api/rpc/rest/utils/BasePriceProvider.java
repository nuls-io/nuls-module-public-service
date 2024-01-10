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
 * @Description: Function Description
 */
public abstract class BasePriceProvider implements PriceProvider {

    public static final int TIMEOUT_MILLIS = 30000;

    protected String url;

    public BasePriceProvider(String url) {
        this.url = url;
    }

    public Map<String, Object> httpRequest(String url) {
        SSLContext sslcontext = createIgnoreVerifySSL();
// Set ProtocolhttpandhttpsCorresponding processingsocketObject linking factory
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);
//        CloseableHttpClient httpClient = HttpClientBuilder.create()
////                .setSSLHostnameVerifier((hostName, sslSession) -> {
////            return true; // Certificate verification passed
////        })
//                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();

        HttpGet httpGet = new HttpGet(url);
//        Local debugging dedicated
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
            Log.warn("Calling interfaces:{} abnormal, StatusCode:{}", url, response.getStatusLine().getStatusCode());
            return null;
        } catch (IOException e) {
            Log.warn("Calling interfaces:{} abnormal1, {}", url, e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.warn("Calling interfaces:{} abnormal2, {}", url, e.getMessage());
            return null;
        }
    }

    public static SSLContext createIgnoreVerifySSL() {
        SSLContext sslContext = null;// Create socket object
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");//specifyTLSversion
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // achieveX509TrustManagerInterface, used to bypass verification
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
            sslContext.init(null, new TrustManager[] { trustManager }, null);//initializationsslContextobject
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }


}
