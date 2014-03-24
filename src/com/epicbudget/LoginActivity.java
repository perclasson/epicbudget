package com.epicbudget;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String PREFS_SYNC_CURRENCIES = "sync_currencies";
	private static final String PREFS_SYNC_ENTRY_TYPES = "sync_entry_types";
	public static final String PREFS_SYNC_LAST_SYNC = "lastSync";
	public static final String PREFS_SYNC_USER = "login";
	public static final String PREFS_SYNC_CURRENCY_SYMBOL = "currency_symbol";
	public static final String PREFS_SYNC_USER_ID = "user_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// TODO: sync the data instead
		// this.deleteDatabase(DbHelper.DATABASE_NAME);

		getCurrencies();
		getEntryTypes();
		// TODO: save user name on login
	}

	protected void setLastSync(String prefs) {
		Calendar now = Calendar.getInstance();
		SharedPreferences settings = getSharedPreferences(prefs, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(PREFS_SYNC_LAST_SYNC, now.getTimeInMillis());
		editor.commit();
	}

	private long getLastSync(String prefs) {
		SharedPreferences settings = getSharedPreferences(prefs, 0);
		return settings.getLong(PREFS_SYNC_LAST_SYNC, 0);
	}

	private String getLastSyncJSONString(String prefs) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("last_sync", getLastSync(prefs));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return obj.toString();
	}

	private void getEntryTypes() {

		API api = new API(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					DbHelper dbHelper = new DbHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					JSONArray entryTypes = obj.getJSONArray("entry_types");
					for (int i = 0; i < entryTypes.length(); i++) {
						String entryTypeString = entryTypes.getString(i);
						JSONObject entryType = new JSONObject(entryTypeString);
						Boolean isActive = entryType.getBoolean("is_active");

						if (isActive) {
							Cursor c = EntryTypeContract.get(db,
									entryType.getLong("id"));
							if (!c.moveToNext()) {
								EntryTypeContract.insert(db,
										entryType.getLong("id"),
										entryType.getString("name"),
										entryType.getString("category"));
							} else {
								// TODO: update
							}
						} else {
							long id = entryType.getLong("id");
							Cursor c = EntryTypeContract.get(db, id);
							if (c.moveToNext()) {
								EntryTypeContract.delete(db, id);
							}
						}
						setLastSync(PREFS_SYNC_ENTRY_TYPES);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		api.execute("entry_types/",
				getLastSyncJSONString(PREFS_SYNC_ENTRY_TYPES));
	}

	private void getCurrencies() {
		API api = new API(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					DbHelper dbHelper = new DbHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					JSONArray currencies = obj.getJSONArray("currencies");
					for (int i = 0; i < currencies.length(); i++) {
						String currencyString = currencies.getString(i);
						JSONObject currency = new JSONObject(currencyString);
						Boolean isActive = currency.getBoolean("is_active");

						if (isActive) {
							Cursor c = CurrencyContract.get(db,
									currency.getLong("id"));
							if (!c.moveToNext()) {
								CurrencyContract.insert(db,
										currency.getLong("id"),
										currency.getString("name"),
										currency.getString("symbol"));
							} else {
								// TODO: update
							}

						} else {
							long id = currency.getLong("id");
							Cursor c = CurrencyContract.get(db, id);
							if (c.moveToNext()) {
								CurrencyContract.delete(db, id);
							}
						}
						setLastSync(PREFS_SYNC_CURRENCIES);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		};
		api.execute("currencies/", getLastSyncJSONString(PREFS_SYNC_CURRENCIES));

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

			API api = new API(getApplicationContext()) {
				@Override
				protected void onPostExecute(String result) {
					try {
						JSONObject obj = new JSONObject(result);
						boolean isAuthenticated = obj
								.getBoolean("is_authenticated");
						String currencySymbol = obj
								.getString("currency_symbol");
						long userId = obj.getLong("user_id");
						
						if (isAuthenticated) {
							SharedPreferences settings = getSharedPreferences(
									PREFS_SYNC_USER, 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString(PREFS_SYNC_CURRENCY_SYMBOL,
									currencySymbol);
							editor.putLong(PREFS_SYNC_USER_ID, userId);
							editor.commit();
							
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
