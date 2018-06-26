package com.lumi.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 采用连接池来管理HttpClient
 * 
 * @author xiatiansong
 *
 */
public class HttpClientUtils {

	private static final Log LOG = LogFactory.getLog(HttpClientUtils.class);

	private static final JSONObject JO404 = new JSONObject();
	static {
		JO404.put("code", 404);
		JO404.put("message", "404 error");
	}

	/**
	 * The default timeout for a connected socket.
	 */
	public static final int DEFAULT_SOCKET_TIMEOUT_MS = 50 * 1000;

	/**
	 * The default timeout for establishing a connection.
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 50 * 1000;

	/**
	 * max connections a client can have at same time
	 */
	private static final int DEFAULT_MAX_CONNECTIONS = 100;

	private static final int DEFAULT_MAX_CONNECTIONS_ROUTE = 100;

	private static final String ENCODE = "utf-8";

	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpclient = null;

	static {
		try {
			SSLContext sslContext = SSLContexts.custom().build();
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs,
											   String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs,
											   String authType) {
				}
			} }, null);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https",
							new SSLConnectionSocketFactory(sslContext)).build();
			connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			// 设置超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT_MS)
					.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT_MS)
					.setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT_MS)
					.build();

			// 设置http cleint参数
			httpclient = HttpClients
					.custom()
					.setConnectionManager(connManager)
					.setDefaultRequestConfig(requestConfig)
					.setRetryHandler(
							new DefaultHttpRequestRetryHandler(3, false))
					.build();
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom()
					.setTcpNoDelay(true).build();
			connManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom()
					.setMaxHeaderCount(200).setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.setMessageConstraints(messageConstraints).build();
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
			connManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_ROUTE);
		} catch (KeyManagementException e) {
			LOG.error("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException", e);
		}
	}

	public static CloseableHttpClient getTimeoutHttpClient() {
		return httpclient;
	}

	public static CloseableHttpClient getHttpClient() {
		return httpclient;
	}

	/**
	 * <pre>
	 * 发送请求获取byte数组结果
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static byte[] requestByteArray(String uri,
			Map<String, Object> queryParameters, String method)
			throws Exception {
		return requestByteArray(uri, null, queryParameters, method);
	}

	/**
	 * <pre>
	 * 发送请求获取byte数组结果
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static byte[] requestByteArray(String uri,
			Map<String, String> headers, Map<String, Object> queryParameters,
			String method) throws Exception {
		String requestMethod = method == null ? "get" : method.toLowerCase();
		CloseableHttpResponse httpResponse = null;
		switch (requestMethod) {
		case "get":
			httpResponse = get(uri, headers, queryParameters);
			break;
		case "post":
			httpResponse = post(uri, headers, queryParameters);
			break;
		default:
			break;
		}
		byte[] responseByte = null;
		try {
			if (httpResponse != null) {
				HttpEntity entity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				try {
					if (statusCode == HttpStatus.SC_OK) {
						responseByte = EntityUtils.toByteArray(entity);
					} else {
						LOG.error(String.format(
								"request url: %s return error code: %s", uri,
								statusCode));
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			}
		} catch (Exception e) {
			LOG.error(String.format(
					"[HttpClientsUtils %s] get response error, url:%s", method,
					uri), e);
			return responseByte;
		} finally {
			closeResponseEntity(httpResponse);
		}
		return responseByte;
	}

	/**
	 * <pre>
	 * 发送请求获取string结果
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static String requestString(String uri,
			Map<String, Object> queryParameters, String method)
			throws Exception {
		return requestString(uri, null, queryParameters, method, false);
	}

	/**
	 * <pre>
	 * 发送请求获取string结果
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static String requestString(String uri, Map<String, String> headers,
			Map<String, Object> queryParameters, String method)
			throws Exception {
		return requestString(uri, headers, queryParameters, method, false);
	}

	/**
	 * <pre>
	 * 发送请求获取string结果
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static String requestString(String uri, Map<String, String> headers,
			Map<String, Object> queryParameters, String method, boolean isJson)
			throws Exception {
		String requestMethod = method == null ? "get" : method.toLowerCase();
		CloseableHttpResponse httpResponse = null;
		switch (requestMethod) {
		case "get":
			httpResponse = get(uri, headers, queryParameters);
			break;
		case "post":
			httpResponse = post(uri, headers, queryParameters, isJson);
			break;
		default:
			break;
		}
		String responseString = null;
		try {
			if (httpResponse != null) {
				HttpEntity entity = httpResponse.getEntity();
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				try {
					if (statusCode == HttpStatus.SC_OK) {
						/* 此处有bug，当返回字符串过长时会报异常：Socket is closed，因为提前调用了request.releaseConnection() */
						responseString = EntityUtils.toString(entity, ENCODE);
					} else {
						LOG.error(String.format(
								"request url: %s return error code: %s", uri,
								statusCode));
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			}
		} catch (Exception e) {
			LOG.error(String.format(
					"[HttpClientsUtils Get] get response error, url:%s", uri),
					e);
			return responseString;
		} finally {
			closeResponseEntity(httpResponse);
		}
		return responseString;
	}

	/**
	 * <pre>
	 * get请求获取数据
	 * uri:请求地址
	 * queryParameters:参数
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpResponse get(String uri,
			Map<String, Object> queryParameters) throws Exception {
		return get(uri, null, queryParameters);
	}

	/**
	 * <pre>
	 * get请求获取数据
	 * uri:请求地址
	 * queryParameters:参数
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpResponse get(String uri,
			Map<String, String> headers, Map<String, Object> queryParameters)
			throws Exception {
		HttpGet request = new HttpGet();
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(uri);
			boolean hasParameter = null != queryParameters
					&& !queryParameters.isEmpty();
			if (hasParameter) {
				int i = 0;
				for (Entry<String, Object> entry : queryParameters.entrySet()) {
					if (i == 0 && !uri.contains("?")) {
						buf.append("?");
					} else {
						buf.append("&");
					}
					i++;
					String value = "";
					Object obj = entry.getValue();
					if (!(obj instanceof String)) {
						value = JsonUtil.getJsonFromObj(obj);
					} else {
						value = obj.toString();
					}
					buf.append(entry.getKey()).append('=')
							.append(URLEncoder.encode(value, "UTF-8"));
				}
			}
			request.setURI(new URI(buf.toString()));
			CloseableHttpResponse response = getHttpClient().execute(request);
			return response;
		} catch (ClientProtocolException e) {
			LOG.error(String.format(
					"[HttpClientsUtils Get] get response error, url:%s", uri),
					e);
		} catch (IOException e) {
			LOG.error(String.format(
					"[HttpClientsUtils Get] get response error, url:%s", uri),
					e);
		} finally {
			request.releaseConnection();
		}
		return null;
	}

	/**
	 * <pre>
	 * Post请求获取数据
	 * uri:请求地址
	 * queryParameters:参数static {
	 * 		JO404.put("code", 404);
	 * 		JO404.put("message", "404 error");
	 * 	}
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpResponse post(String uri,
			Map<String, Object> queryParameters) throws Exception {
		return post(uri, null, queryParameters);
	}

	/**
	 * <pre>
	 * Post请求获取数据
	 * uri:请求地址
	 * queryParameters:参数static {
	 * 		JO404.put("code", 404);
	 * 		JO404.put("message", "404 error");
	 * 	}
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpResponse post(String uri,
			Map<String, String> headers, Map<String, Object> queryParameters)
			throws Exception {
		return post(uri, headers, queryParameters, false);
	}

	/**
	 * <pre>
	 * Post请求获取数据
	 * uri:请求地址
	 * queryParameters:参数static {
	 * 		JO404.put("code", 404);
	 * 		JO404.put("message", "404 error");
	 * 	}
	 * </pre>
	 * 
	 * @param uri
	 * @param queryParameters
	 * @return
	 * @throws Exception
	 */
	public static CloseableHttpResponse post(String uri,
			Map<String, String> headers, Map<String, Object> queryParameters,
			boolean isJson) throws Exception {
		HttpPost request = new HttpPost(uri);
		try {
			// 设置头部
			boolean hasHeader = null != headers && !headers.isEmpty();
			if (hasHeader) {
				for (Entry<String, String> entry : headers.entrySet()) {
					request.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			boolean hasParameter = null != queryParameters
					&& !queryParameters.isEmpty();
			if (hasParameter) {
				if (isJson) {
					String json = JsonUtil.getJsonFromObj(queryParameters);
					StringEntity entity = new StringEntity(json, Consts.UTF_8);
					entity.setContentType("application/json");
					entity.setContentEncoding("UTF-8");
					request.setEntity(entity);
				} else {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					for (Entry<String, Object> entry : queryParameters
							.entrySet()) {
						String value = "";
						Object obj = entry.getValue();
						if (!(obj instanceof String)) {
							value = JsonUtil.getJsonFromObj(obj);
						} else {
							value = obj.toString();
						}
						params.add(new BasicNameValuePair(entry.getKey(), value));
					}
					request.setEntity(new UrlEncodedFormEntity(params,
							Consts.UTF_8));
				}
			}
			CloseableHttpResponse response = getHttpClient().execute(request);
			return response;
		} catch (ClientProtocolException e) {
			LOG.error(String.format(
					"[HttpClientsUtils Get] get response error, url:%s", uri),
					e);
		} catch (IOException e) {
			LOG.error(String.format(
					"[HttpClientsUtils Get] get response error, url:%s", uri),
					e);
		} finally {
			request.releaseConnection();
		}
		return null;
	}

	public static void release() {
		if (connManager != null) {
			connManager.shutdown();
		}
	}

	public static void closeResponseEntity(HttpResponse response) {
		if (response == null) {
			return;
		}
		HttpEntity entity = response.getEntity();
		if (entity != null && entity.isStreaming()) {
			try {
				entity.getContent().close();
			} catch (IOException e) {
				LOG.error("close response entity", e);
			}
		}
	}
}