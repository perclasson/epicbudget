package com.epicbudget;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

class APIController extends AsyncTask<String, String, String> {
	public final static String SERVER_URL = "http://130.229.129.123/api/";
	// public final static String SERVER_URL = "http://192.168.1.2/api/";
	protected static final String PREFS_COOKIES = "cookies";
	private static final String COOKIE_SESSION_NAME = "sessionid";
	private static final String COOKIE_SESSION_VALUE = "sessionid_value";
	private static final String COOKIE_SESSION_DOMAIN = "sessionid_domain";
	private static final String COOKIE_SESSION_EXPIRY_DATE = "sessionid_expiry_date";
	protected Context context;

	public APIController(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		String url = SERVER_URL + params[0]; // URL to call
		String data = params[1]; // JSON data
		String result = "";

		BufferedReader br = null;

		try {
			CookieStore cookieStore = new BasicCookieStore();

			SharedPreferences settings = context.getSharedPreferences(
					PREFS_COOKIES, 0);
			String cookieSessionIdValue = settings.getString(
					COOKIE_SESSION_VALUE, null);

			if (cookieSessionIdValue != null) {
				BasicClientCookie cookie = new BasicClientCookie(
						COOKIE_SESSION_NAME, cookieSessionIdValue);
				cookie.setDomain(settings
						.getString(COOKIE_SESSION_DOMAIN, null));
				Date expiryDate = new SimpleDateFormat(
						"EEE MMM dd HH:mm:ss zzz yyy").parse(settings
						.getString(COOKIE_SESSION_EXPIRY_DATE, null));
				cookie.setExpiryDate(expiryDate);
				cookieStore.addCookie(cookie);
			}

			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			if (data != null) {
				StringEntity se = new StringEntity(data);
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httpPost.setEntity(se);
			}

			HttpResponse httpResponse = httpClient.execute(httpPost,
					localContext);

			InputStream inputStream = httpResponse.getEntity().getContent();

			for (Cookie cookie : cookieStore.getCookies()) {
				if (cookie.getName().equals(COOKIE_SESSION_NAME)) {
					Editor edit = settings.edit();
					edit.putString(COOKIE_SESSION_VALUE, cookie.getValue());
					edit.putString(COOKIE_SESSION_DOMAIN, cookie.getDomain());
					edit.putString(COOKIE_SESSION_EXPIRY_DATE, cookie
							.getExpiryDate().toString());
					edit.commit();
				}
			}

			br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}

		return result;
	}

	protected void onPostExecute(String result) {
		Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
	}

}
