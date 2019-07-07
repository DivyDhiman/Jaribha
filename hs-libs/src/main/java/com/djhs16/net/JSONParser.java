package com.djhs16.net;

import android.util.Base64;
import android.util.Log;

import com.djhs16.net.toolbox.HttpURLConnectionHelper;
import com.djhs16.utils.AppUtils;
import com.djhs16.utils.StreamUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

public class JSONParser {

    public String getResponse(String url, List<NameValuePair> params) {
        HttpURLConnection conn = null;
        try {
            conn = HttpURLConnectionHelper.makeHTTPConnection(url + "?" + AppUtils.getUrlEncodedString(params));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (conn != null) {
            try {
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    String msg = StreamUtils.readData(isr);
                    isr.close();
                    return msg;
                }
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        }
        return null;
    }

    public synchronized String getResponseFromHttpPost(String url, List<NameValuePair> params) {
        InputStream is = null;
        String response = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ("vcourt" + ":" + "comzoomer").getBytes(),
                    Base64.NO_WRAP);

            httpPost.addHeader(HttpHeaders.AUTHORIZATION, base64EncodedCredentials);
            httpPost.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en_US");

            if (params != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            response = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result ");
            e.printStackTrace();
        }
        return response;
    }

    public synchronized String getResponseFromHttpGet(String url, List<NameValuePair> params) {
        InputStream is = null;
        String response = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);

            if (params != null) {
                String format = URLEncodedUtils.format(params, "UTF-8");
                httpGet = new HttpGet(url + "?" + format);
            }

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            response = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result ");
            e.printStackTrace();
        }
        return response;
    }

    public synchronized JSONObject getJsonObjectFromHttp(String url, List<NameValuePair> params, Http method) {
        String resp;

        if (method == Http.GET) {
            resp = getResponseFromHttpGet(url, params);
        } else {
            resp = getResponseFromHttpPost(url, params);
        }

        JSONObject jObj = null;

        Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + params + "\nREPSONSE: " + resp);

        try {
            jObj = new JSONObject(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing object ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public synchronized JSONArray getJsonArrayFromHttp(String url, List<NameValuePair> params) {

        String resp = getResponse(url, params);
        JSONArray jArray = null;

        Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + params + "\nREPSONSE: " + resp);

        try {
            jArray = new JSONArray(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing array ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jArray;
    }

    public synchronized JSONObject getJsonObjectFromUrl(String url, List<NameValuePair> params) {

        String resp = getResponse(url, params);
        JSONObject jObj = null;

        Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + params + "\nREPSONSE: " + resp);

        try {
            jObj = new JSONObject(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing object ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public synchronized JSONArray getJsonArrayFromUrl(String url, List<NameValuePair> params) {

        String resp = getResponse(url, params);
        JSONArray jArray = null;

        Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + params + "\nREPSONSE: " + resp);

        try {
            jArray = new JSONArray(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing array ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jArray;
    }

    public synchronized String getResponseFromMultipart(String url, MultipartEntityBuilder builder) {
        InputStream is = null;
        String response = "";

        try {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    ("vcourt" + ":" + "comzoomer").getBytes(),
                    Base64.NO_WRAP);

            httpPost.addHeader(HttpHeaders.AUTHORIZATION, base64EncodedCredentials);
            httpPost.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en_US");

            httpPost.setEntity(builder.build());
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            response = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result ");
            e.printStackTrace();
        }
        return response;
    }

    public synchronized JSONObject getJsonObjectFromMultipart(String url, MultipartEntityBuilder builder) {

        String resp = getResponseFromMultipart(url, builder);
        Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + builder.toString() + "\nREPSONSE: " + resp);

        JSONObject jObj = null;

        try {
            jObj = new JSONObject(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing object ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObj;
    }

    public enum Http {
        GET, POST;
    }

    public synchronized JSONObject getJsonObjectFromUrl1(String url, JSONObject params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            post.setHeader("content-type", "application/json");
            HttpConnectionParams.setConnectionTimeout(client.getParams(),
                    10000); // Timeout Limit

            StringEntity entity = new StringEntity(params.toString(),
                    HTTP.UTF_8);
            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            InputStream in = response.getEntity().getContent();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();

            // String resp = getResponse(url, params);
            String resp = stringBuilder.toString();

            Log.d(getClass().getName(), "URL: " + url + "\nPARAMS: " + params + "\nREPSONSE: " + resp);


            return new JSONObject(resp);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing object ");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}