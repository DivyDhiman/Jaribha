package com.djhs16.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionHelper {

	public static int HTTP_CONNECTION_TIMEOUT = 30000;

	public static HttpURLConnection makeHTTPConnection(String url) throws IOException {
		URL imageUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		conn.setReadTimeout(HTTP_CONNECTION_TIMEOUT);
		conn.setInstanceFollowRedirects(true);
		return conn;
	}
}
