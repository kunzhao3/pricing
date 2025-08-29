package com.parses.server.util;
import com.parses.server.bean.dto.DataTransDTO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("httpPostUtil")
public class HttpPostUtil {

	private  static final CloseableHttpClient httpClient;

	static{
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
				SSLContexts.createDefault(),
				null,
				null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https",sslConnectionSocketFactory)
				.build();
		PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		poolConnManager.setMaxTotal(200);
		poolConnManager.setDefaultMaxPerRoute(200);
		CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(poolConnManager).build();
		httpClient=	InTraceCloseableHttpClient.wrapUp(closeableHttpClient);
	}
	public DataTransDTO callPayOfHttp(String jsonObj, String url) {
		DataTransDTO result = new DataTransDTO();
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse httpResponse=null;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000)
					.setConnectionRequestTimeout(20000).setSocketTimeout(15000).build();
			httpPost.setConfig(requestConfig);
			//将参数转换为字符串
			StringEntity entity = new StringEntity(jsonObj, "utf-8");
			httpPost.setEntity(entity);
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpResponse = httpClient.execute(httpPost);
			if (httpResponse == null) {
				throw new RuntimeException("http post failed");
			}
			int retCode = httpResponse.getStatusLine().getStatusCode();
			if (retCode != HttpStatus.SC_OK) {
				result.setRespCode(HttpCallEnum.SEND_FAILED.getCode());
			} else {
				HttpEntity responseEntity = httpResponse.getEntity();
				String resultJson = EntityUtils.toString(responseEntity, "UTF-8");
				result.setRespCode(HttpCallEnum.SEND_SUCCESS.getCode());
				result.setResultJson(resultJson);
			}
		} catch (Exception e){
			result.setRespCode(HttpCallEnum.SEND_EXCEPTION.getCode());
		} finally {
            httpPost.releaseConnection();
            if (null != httpResponse) {
				try {
					httpResponse.close();
				} catch (Exception e) {
					result.setRespCode(HttpCallEnum.SEND_EXCEPTION.getCode());
				}
			}
		}
		return result;
	}
}
