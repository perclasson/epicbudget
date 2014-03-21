package com.epicbudget;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

		// TODO: save user name on login
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// TODO: fix this
		if (requestCode == Activity.RESULT_OK) {
			String message = data.getStringExtra("message");
			if (message != null) {
				Toast toast = Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void login(View view) {
		EditText usernameEditText = (EditText) findViewById(R.id.username_field);
		EditText passwordEditText = (EditText) findViewById(R.id.password_field);
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (username != null && password != null) {
			JSONObject obj = new JSONObject();

			try {
				obj.put("username", username);
				obj.put("password", password);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			APIController api = new APIController(getApplicationContext()) {
				@Override
				protected void onPostExecute(String result) {
					try {
						JSONObject obj = new JSONObject(result);
						boolean isAuthenticated = obj
								.getBoolean("is_authenticated");

						if (isAuthenticated) {
							Intent intent = new Intent(context,
									OverviewActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(context, obj.getString("message"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			api.execute("login/", obj.toString());

		}
	}

	public void register(View view) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

}
