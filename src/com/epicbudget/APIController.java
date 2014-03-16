package com.epicbudget;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class APIController extends AsyncTask<String, String, String> {
	public final static String SERVER_URL = "http://wproj.nada.kth.se/~pclasson/epicbudget/api.php?";
	private Context context;
	
	public APIController(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		String urlAddress = SERVER_URL + params[0]; // URL to call
		String result = "";

		BufferedReader br = null;
		
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			result = br.readLine(); 
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}

		return result;

	}

	protected void onPostExecute(String result) {
		CharSequence text = result;

		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

}
