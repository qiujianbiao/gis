package com.ericsson.fms.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttlUtil {
	
	public static Log log = LogFactory.getLog(HttlUtil.class);
	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, boolean useProxy, String proxyIp, Integer proxyPort) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL realUrl = new URL(url + "?" + param);
			log.info("HttlUtil sendGet realUrl="+realUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) realUrl.openConnection();

			httpUrlConn.setDoOutput(false);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestMethod("GET");
			if(useProxy){//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyIp, proxyPort));
                httpUrlConn = (HttpURLConnection) realUrl.openConnection(proxy);
            }else{
            	httpUrlConn = (HttpURLConnection) realUrl.openConnection();
            }
			
			httpUrlConn.connect();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
			log.warn("HttlUtil sendGet error",e);
		}
		return buffer.toString();
	}


	public static String postRequest(String url,String index,String type,String json) throws Exception{
		StringBuffer sb = new StringBuffer(url);
		sb.append("/").append(index).append("/").append(type).append("/").append("_search");
		HttpClient client = HttpClients.createDefault();
		URI uri = new URI(sb.toString());
		HttpPost post = new HttpPost(uri);
		post.addHeader(HTTP.CONTENT_TYPE,"application/json");
		StringEntity se = new StringEntity(json);
		post.setEntity(se);
		HttpResponse response = client.execute(post);
		return EntityUtils.toString(response.getEntity());
	}

	public static String putIndexMaxNum(String url,String json) throws Exception{
		StringBuffer sb = new StringBuffer(url);
		sb.append("/_settings?preserve_existing=true");
		HttpClient client = HttpClients.createDefault();
		URI uri = new URI(sb.toString());
		HttpPut put = new HttpPut(uri);
		put.addHeader(HTTP.CONTENT_TYPE,"application/json");
		StringEntity se = new StringEntity(json);
		put.setEntity(se);
		HttpResponse response = client.execute(put);
		return EntityUtils.toString(response.getEntity());
	}


	public static String putIndexMapping(String url,String index,String type,String json) throws Exception{
		StringBuffer sb = new StringBuffer(url);
		sb.append("/").append(index).append("/").append(type).append("/").append("_mapping");
		HttpClient client = HttpClients.createDefault();
		URI uri = new URI(sb.toString());
		HttpPut put = new HttpPut(uri);
		put.addHeader(HTTP.CONTENT_TYPE,"application/json");
		StringEntity se = new StringEntity(json);
		put.setEntity(se);
		HttpResponse response = client.execute(put);
		return EntityUtils.toString(response.getEntity());
	}
}
