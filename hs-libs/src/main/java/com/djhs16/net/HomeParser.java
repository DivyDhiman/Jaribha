package com.djhs16.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeParser {

	public ArrayList<String> getDataList(String param1, String param2) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tagneme1", param1));
		params.add(new BasicNameValuePair("tagneme1", param2));
		try {
			JSONArray jsonArray = new JSONParser().getJsonArrayFromUrl(
					"http://codewaretechnologies.in/flyboard/user/apicontroller/check_login?email=admin@flyboard.com&password=123456", params);
			if ((jsonArray != null)) {
				ArrayList<String> objectList = new ArrayList<String>();
				for (int i = 0; i < jsonArray.length(); i++) {
					String rep_id = jsonArray.getJSONObject(i).getString("");
					objectList.add(rep_id);
				}
				return objectList;
			}
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getData(String url, String param1, String param2) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tagneme1", param1));
		params.add(new BasicNameValuePair("tagneme1", param2));
		try {
			JSONObject jsonObject = new JSONParser().getJsonObjectFromUrl(url, params);
			if ((jsonObject != null)) {
				String rep_id = jsonObject.getString("");

				return rep_id;
			}
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean processPendingRequest(String url) {
		JSONObject responseMsg = new JSONParser().getJsonObjectFromUrl(url, null);
		try {
			if (responseMsg.getInt("success") == 1) {
				return true;
			}
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}