package com.epicbudget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		Spinner currencies = (Spinner) findViewById(R.id.currency_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.currencies, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencies.setAdapter(adapter);
	}

	public void register(View view) {
		EditText usernameEditText = (EditText) findViewById(R.id.username_field);
		EditText passwordEditText = (EditText) findViewById(R.id.password_field);
		EditText repeatPasswordEditText = (EditText) findViewById(R.id.repeat_password_field);
		Spinner currencySpinner = (Spinner) findViewById(R.id.currency_spinner);
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String repeatPassword = repeatPasswordEditText.getText().toString();
		String currency = currencySpinner.getSelectedItem().toString();
		
		if (!password.equals(repeatPassword) || password == null) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.repeat_password_fail, Toast.LENGTH_SHORT);
			toast.show();
		}
		else if (username != null && password != null) {
			String url = null;
			try {
				url = "func=register&username="
						+ URLEncoder.encode(username, "utf-8") + "&password="
						+ URLEncoder.encode(password, "utf-8") + "&currency="
								+ URLEncoder.encode(currency, "utf-8");
			} catch (UnsupportedEncodingException e) {
				return;
			}
			APIController api = new APIController(getApplicationContext());
			api.execute(url);
		}

	}

}