package com.kris.prophecy.utils;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

/**
 * @author by Kris on 2018/12/25.
 */
public class HttpHandler {

    public static String get(String url) throws Exception {
        if (url != null) {
            HttpClient httpClient;
            if (url.startsWith("https")) {
                httpClient = getHttpsClient();
            } else {
                httpClient = getHttpClient();
            }

            return get(httpClient, url);
        } else {
            return "url is null";
        }
    }

    public static String post(String url, Map<String, String> params) throws Exception {
        if (url == null) {
            return "url is null";
        } else {
            HttpClient httpClient;
            if (url.startsWith("https")) {
                httpClient = getHttpsClient();
            } else {
                httpClient = getHttpClient();
            }

            List<NameValuePair> paras = new ArrayList();
            Iterator var4 = params.keySet().iterator();

            while (var4.hasNext()) {
                String key = (String) var4.next();
                paras.add(new BasicNameValuePair(key, (String) params.get(key)));
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paras, "UTF-8");
            return post(httpClient, url, entity);
        }
    }

    public static String post(String url, String json) throws Exception {
        if (url != null) {
            HttpClient httpClient;
            if (url.startsWith("https")) {
                httpClient = getHttpsClient();
            } else {
                httpClient = getHttpClient();
            }

            StringEntity entity = new StringEntity(json, "UTF-8");
            return post(httpClient, url, entity);
        } else {
            return "url is null";
        }
    }

    private static String get(HttpClient httpClient, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String response = EntityUtils.toString(httpEntity, "utf-8");
        EntityUtils.consume(httpEntity);
        return response;
    }

    public static String post(HttpClient httpClient, String url, HttpEntity entity) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String response = EntityUtils.toString(httpEntity, "utf-8");
        EntityUtils.consume(httpEntity);
        return response;
    }

    private static HttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    private static HttpClient getHttpsClient() throws Exception {
        SSLContext context = (new SSLContextBuilder()).loadTrustMaterial((KeyStore) null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        return HttpClients.custom().setSSLContext(context).build();
    }
}
