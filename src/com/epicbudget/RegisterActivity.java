package com.epicbudget;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.epicbudget.CurrencyContract.Currency;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
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

		// Enable up button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		fillCurrencySpinner();
	}

	private void fillCurrencySpinner() {
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = CurrencyContract.getAll(db);
		List<StringWithTag> list = new ArrayList<StringWithTag>();
		while (c.moveToNext()) {
			list.add(new StringWithTag(c.getString(1) + " (" + c.getString(2)
					+ ")", c.getLong(0)));
		}

		Spinner currencies = (Spinner) findViewById(R.id.currency_spinner);
		ArrayAdapter<StringWithTag> adapter = new ArrayAdapter<StringWithTag>(
				this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencies.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void register(View view) {
		EditText usernameEditText = (EditText) findViewById(R.id.username_field);
		EditText passwordEditText = (EditText) findViewById(R.id.password_field);
		EditText repeatPasswordEditText = (EditText) findViewById(R.id.repeat_password_field);
		Spinner currencySpinner = (Spinner) findViewById(R.id.currency_spinner);
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String repeatPassword = repeatPasswordEditText.getText().toString();
		StringWithTag currencyTag = (StringWithTag) currencySpinner.getSelectedItem();
		Long currency = (Long) currencyTag.tag;

		if (!password.equals(repeatPassword) || password == null) {
			Toast.makeText(getApplicationContext(),
					R.string.repeat_password_fail, Toast.LENGTH_SHORT).show();
		} else if (username != null && password != null) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("username", username);
				obj.put("password", password);
				obj.put("currency", currency);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			APIController api = new APIController(getApplicationContext()) {
				@Override
				protected void onPostExecute(String result) {
					try {
						JSONObject obj = new JSONObject(result);
						boolean registered = obj.getBoolean("registered");
						String message = obj.getString("message");
						if (registered) {
							Intent returnIntent = new Intent(context,
									LoginActivity.class);
							returnIntent.putExtra("message", message);
							setResult(RESULT_OK, returnIntent);
							finish();
						} else {
							Toast.makeText(context, message, Toast.LENGTH_SHORT)
									.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			};
			api.execute("register/", obj.toString());
		}

	}

}
