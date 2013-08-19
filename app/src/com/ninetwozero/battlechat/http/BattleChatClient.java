/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

package com.ninetwozero.battlechat.http;

import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class BattleChatClient {
    private static final String JSON_ERROR = "{error:'%s'}";
    private static DefaultHttpClient mHttpClient = HttpClientFactory.getThreadSafeClient();

    private BattleChatClient() {
    }

    public static JSONObject get(final String url, final int headerId) throws JSONException {
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Referer", url);

            for (Header header : HttpHeaders.Post.getHeaders(headerId)) {
                httpGet.setHeader(header.getName(), header.getValue());
            }

            return getJsonObjectFromHttpResponse(mHttpClient.execute(httpGet));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JSONObject(String.format(JSON_ERROR, ex.getMessage()));
        }
    }

    public static JSONObject post(final String url, final NameValuePair... data) throws JSONException {
        return post(url, HttpHeaders.Post.NORMAL, data);
    }

    public static JSONObject post(final String url, final int headerId, final NameValuePair... data) throws JSONException {
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Referer", url);
            for (Header header : HttpHeaders.Post.getHeaders(headerId)) {
                httpPost.setHeader(header.getName(), header.getValue());
            }

            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(data), HTTP.UTF_8));
            return getJsonObjectFromHttpResponse(mHttpClient.execute(httpPost));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JSONObject(String.format(JSON_ERROR, ex.getMessage()));
        }
    }

    public static JSONObject getJsonObjectFromHttpResponse(HttpResponse response) throws Exception {
        String message;
        try {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity.getContentLength() > 0) {
                JSONObject object = new JSONObject(EntityUtils.toString(httpEntity));
                if (object.has("data")) {
                    return object.getJSONObject("data");
                } else {
                    message = "Invalid request. Please notify the developer.";
                }
            } else {
                message = "No data found.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message = ex.getMessage();
        }
        return new JSONObject(String.format(JSON_ERROR, message));
    }

    public static void setCookie(Cookie cookie) {
        CookieStore cookieStore = mHttpClient.getCookieStore();
        cookieStore.addCookie(cookie);
        mHttpClient.setCookieStore(cookieStore);
        mHttpClient.getCookieStore().toString();
    }
}
