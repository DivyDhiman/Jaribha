package com.djhs16.net;

import android.util.Log;

import com.djhs16.helperbeans.TwoReturnValues;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

	public static final String TAG = "HttpUtils";

	/**
	 * 
	 * To convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means there's no more data to read. We use the StringWriter class to produce the string.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is) throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * Passes the login credentials to the server.
	 * 
	 * @param httpClient
	 * @param url
	 * @param params
	 *            of type List<NameValuePair>
	 * 
	 * @return TwoReturnValues<Ingeget, String> integer, and response string
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static TwoReturnValues<Integer, String> getValuesFromHttp(DefaultHttpClient httpClient, String url, List<NameValuePair> params) throws IllegalStateException, IOException {
		// DefaultHttpClient httpClient = new DefaultHttpClient();

		TwoReturnValues<Integer, String> returnVals = new TwoReturnValues<Integer, String>();
		InputStream is = null;
		String outputString = "";

		HttpPost httpPost = new HttpPost(url);

		if (params != null) {
			UrlEncodedFormEntity entity = null;
			entity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(entity);
		}

		HttpResponse response = null;

		response = httpClient.execute(httpPost);

		Integer statusCode = 0;
		statusCode = response.getStatusLine().getStatusCode();
		Log.v("Status Code", statusCode.toString());
		returnVals.firstVal = statusCode;
		HttpEntity httpEntity = response.getEntity();

		if (httpEntity != null) {
			is = response.getEntity().getContent();
			outputString = convertStreamToString(is);
			returnVals.secondVal = outputString;
			httpEntity.consumeContent();

			Log.i("StatusCode", Integer.toString(statusCode));
		}

		return returnVals;
	}

	public static TwoReturnValues<Integer, String> getValuesFromMultipart(DefaultHttpClient httpClient, String url, MultipartEntityBuilder builder) throws IllegalStateException, IOException {
		// DefaultHttpClient httpClient = new DefaultHttpClient();

		TwoReturnValues<Integer, String> returnVals = new TwoReturnValues<Integer, String>();
		InputStream is = null;
		String outputString = "";

		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(builder.build());

		HttpResponse response = null;

		response = httpClient.execute(httpPost);

		Integer statusCode = 0;
		statusCode = response.getStatusLine().getStatusCode();
		Log.v("Status Code", statusCode.toString());
		returnVals.firstVal = statusCode;
		HttpEntity httpEntity = response.getEntity();

		if (httpEntity != null) {
			is = response.getEntity().getContent();
			outputString = convertStreamToString(is);
			returnVals.secondVal = outputString;
			httpEntity.consumeContent();

			Log.i("StatusCode", Integer.toString(statusCode));
		}

		return returnVals;
	}

	/**
	 * Construct a new HTTP GET request. This will predominately be used for downloading JSON data from the server.
	 * 
	 * @param url
	 * @return
	 */
	public static String getJSONData(DefaultHttpClient httpClient, String url) {
		URI uri;
		InputStream is = null;
		String outputString = "";
		try {
			uri = new URI(url);
			HttpGet httpGet = new HttpGet(uri);
			// httpGet.addHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				is = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(is);
				}
				outputString = convertStreamToString(is);
				// flush http contents
				entity.consumeContent();
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return outputString;

	}

	/**
	 * Construct a new HTTP 'POST' request. This request will be used for posting user generated words to the server and images.
	 * 
	 * @param httpClient
	 * @param postParameters
	 * @param uri
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void executeHttpPost(DefaultHttpClient httpClient, List<NameValuePair> postParameters, String uri) throws UnsupportedEncodingException, IOException {

		HttpResponse response;
		try {
			HttpPost httpPost = new HttpPost(uri);
			// httpPost.addHeader("Accept-Encoding", "gzip");
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters, "UTF-8");

			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();

			// if the server returned anything other than 200 OK status
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
			if (httpEntity != null) {
				httpEntity.consumeContent();

			}
		} finally {

		}
	}

	/**
	 * Construct new multipart post request.
	 * 
	 * @param httpClient
	 * @param uri
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void executeHttpMultipartPost(DefaultHttpClient httpClient, String uri, MultipartEntityBuilder builder) throws UnsupportedEncodingException, IOException {

		HttpResponse response;

		HttpPost httpPost = new HttpPost(uri);
		// httpPost.addHeader("Accept-Encoding", "gzip");
		httpPost.setEntity(builder.build());

		response = httpClient.execute(httpPost);

		if (response != null) {
			Log.d(TAG, Integer.toString(response.getStatusLine().getStatusCode()));
			Log.d(TAG, response.getStatusLine().toString());
			Log.d(TAG, response.getStatusLine().getReasonPhrase().toString());
		}

		// if the server returned anything other than 200 OK status
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			String errorBody = EntityUtils.toString(response.getEntity());

			Log.d(TAG, errorBody);

			throw new IOException("Server returned: " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase() + errorBody);
		}

	}

	/**
	 * Construct a new GET request. This method can be used for downloading images from the web server.
	 * 
	 * @param url
	 * @param filename
	 * @param pathToSave
	 * @return downloaded file
	 */
	public static File getWebImage(String url, String filename, String pathToSave) {
		final String PATH = pathToSave;
		String completedFilename = new String(PATH.concat(filename));
		File file = null;

		try {
			URL myFileUrl = new URL(url);
			file = new File(completedFilename);
			long startTime = System.currentTimeMillis();
			Log.d(TAG, "Starting Download");
			URLConnection urlConnect = myFileUrl.openConnection();

			// define input streams to read data from the url
			InputStream is = urlConnect.getInputStream();
			BufferedInputStream bufferedis = new BufferedInputStream(is);

			// read bytes to the buffer until finished
			ByteArrayBuffer byteBuffer = new ByteArrayBuffer(100);
			int current = 0;
			while ((current = bufferedis.read()) != -1) {
				byteBuffer.append((byte) current);
			}

			// convert byte array to string
			FileOutputStream fileOutStream = new FileOutputStream(file);
			fileOutStream.write(byteBuffer.toByteArray());
			fileOutStream.close();
			Log.d("HttpUtils.class", "Image read in" + ((System.currentTimeMillis() - startTime / 1000) + " sec"));
		} catch (MalformedURLException e) {
			Log.d("MalformedURLException", " when trying to download image, StackTrace follows:");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("IOException", " when trying to download image, StackTrace follows:");
			e.printStackTrace();
		}

		return file;

	}

	public static final String GetHttpStatusMessage(int httpCode) {

		String message = Integer.toString(httpCode) + ": ";

		switch (httpCode) {
		case 200:
			message += "OK";
			break;
		case 400:
			message += "Bad request";
			break;
		case 401:
			message += "Unauthroized";
			break;
		case 402:
			message += "Payment required";
			break;
		case 404:
			message += "Forbidden";
			break;
		case 500:
			message += "Internal server error";
			break;
		case 501:
			message += "Not implemented on server";
			break;
		}

		return message;

	}

}
