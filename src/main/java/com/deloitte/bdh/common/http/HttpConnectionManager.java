package com.deloitte.bdh.common.http;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * httpClient 连接管理器
 *
 * @author dahpeng
 */
public class HttpConnectionManager {

	/**
	 * 普通连接管理器
	 */
	public static final HttpClientConnectionManager BASIC_CONNECTION_MANAGER;

	/**
	 * 连接池管理器
	 */
	public static final HttpClientConnectionManager POOLING_CONNECTION_MANAGER;

	static {
		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLSelfSigned.SSL_CONNECTION_SOCKET_FACTORY).build();
		// 普通连接管理器
		BASIC_CONNECTION_MANAGER = new BasicHttpClientConnectionManager(r);
		// 连接池管理器
		PoolingHttpClientConnectionManager pooling = new PoolingHttpClientConnectionManager(r);
		// 设置最大连接数
		pooling.setMaxTotal(1000);
		// 设置每个路由基础上的最大连接数
		pooling.setDefaultMaxPerRoute(300);
		POOLING_CONNECTION_MANAGER = pooling;
	}

	private HttpConnectionManager() {
	}

	/**
	 * @param max httpClient 最大连接数
	 */
	public static void setMaxTotal(int max) {
		((PoolingHttpClientConnectionManager) POOLING_CONNECTION_MANAGER).setMaxTotal(max);
	}

	/**
	 * @param max 每个路由基础上的最大连接数
	 */
	public static void setDefaultMaxPerRoute(int max) {
		((PoolingHttpClientConnectionManager) POOLING_CONNECTION_MANAGER).setDefaultMaxPerRoute(max);
	}
}
