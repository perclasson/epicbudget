package com.epicbudget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void login(View view) {
		EditText usernameEditText = (EditText) findViewById(R.id.username_field);
		EditText passwordEditText = (EditText) findViewById(R.id.password_field);
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (username != null && password != null) {
			String url = null;
			try {
				url = "func=login&username="
						+ URLEncoder.encode(username, "utf-8") + "&password="
						+ URLEncoder.encode(password, "utf-8");
			} catch (UnsupportedEncodingException e) {
				return;
			}

			APIController api = new APIController(getApplicationContext()) {
				@Override
				protected void onPostExecute(String result) {
					if (result.equals("false")) {
						Toast toast = Toast.makeText(context,
								R.string.failed_login, Toast.LENGTH_SHORT);
						toast.show();
					} else {
						SharedPreferences settings = context
								.getSharedPreferences(
										APIController.PREFS_LOGIN_NAME, 0);
						Editor editor = settings.edit();
						editor.putString("auth", result);
						editor.commit();
						Intent intent = new Intent(context,
								OverviewActivity.class);
						startActivity(intent);
					}
				}
			};
			api.execute(url);

		}
	}

	public void register(View view) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

}
