package com.epicbudget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.epicbudget.EntryContract.Entry;

public class OverviewActivity extends Activity {
	private static final String PREFS_SYNC_ENTRIES = "sync_entries";
	public static final String TIME_IN_MILLIS = "timeInMillis";
	private Calendar calendar;
	private String currencySymbol;
	private long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);

		// Set currency symbol
		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_SYNC_USER, 0);
		currencySymbol = settings.getString(
				LoginActivity.PREFS_SYNC_CURRENCY_SYMBOL, "asd");
		userId = settings.getLong(LoginActivity.PREFS_SYNC_USER_ID, 0);
		calendar = getFirstDayInMonthCalendar();
		getEntries();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getEntries();
	}
	
	private Calendar getFirstDayInMonthCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public void fillListView() {
		final ListView listview = (ListView) findViewById(R.id.listview);
		EntryContract entryContract = new EntryContract();
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Calendar from = (Calendar) calendar.clone();
		from.set(Calendar.HOUR_OF_DAY, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		Calendar to = (Calendar) calendar.clone();
		to.add(Calendar.MONTH, 1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		TextView dateRangeText = (TextView) findViewById(R.id.date_range_text);
		dateRangeText.setText(sdf.format(from.getTime()) + " - "
				+ sdf.format(to.getTime()));

		EntryCursorAdapter adapter = new EntryCursorAdapter(this,
				R.layout.entry_row_layout, entryContract.getInterval(db, from, to, userId),
				new String[] { Entry.COLUMN_NAME_AMOUNT },
				new int[] { R.id.amount_text }, 0, this, currencySymbol);

		Cursor c = entryContract.getInterval(db, from, to, userId);

		Double balance = 0d;
		int categoryIndex = c.getColumnIndexOrThrow(Entry.COLUMN_NAME_CATEGORY);
		int amountIndex = c.getColumnIndexOrThrow(Entry.COLUMN_NAME_AMOUNT);

		while (c.moveToNext()) {
			if (c.getString(categoryIndex).equals("expense")) {
				balance -= c.getDouble(amountIndex);
			} else {
				balance += c.getDouble(amountIndex);
			}
		}

		TextView balanceText = (TextView) findViewById(R.id.balance_text);
		balanceText.setText("Balance: " + balance.toString() + " " + currencySymbol);

		listview.setAdapter(adapter);
	}

	private void getEntries() {
		API api = new API(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					DbHelper dbHelper = new DbHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					JSONArray entries = obj.getJSONArray("entries");
					for (int i = 0; i < entries.length(); i++) {
						String entryString = entries.getString(i);
						JSONObject entry = new JSONObject(entryString);
						Boolean isActive = entry.getBoolean("is_active");
						if (isActive) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date dateDate = null;
							try {
								dateDate = sdf.parse(entry.getString("date"));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(dateDate);
							Cursor c = EntryContract.get(db,
									entry.getLong("id"), userId);
							if (!c.moveToNext()) {
								EntryContract.insert(db, entry.getLong("id"),
										entry.getDouble("amount"),
										entry.getString("category"),
										calendar.getTimeInMillis(),
										entry.getString("description"),
										entry.getInt("entry_type"),
										entry.getString("interval"),
										userId);
							} else {
								// TODO: update
							}
							c.close();

						} else {
							long id = entry.getLong("id");
							Cursor c = EntryContract.get(db, id, userId);
							if (c.moveToNext()) {
								EntryContract.delete(db, id);
							}
							c.close();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				fillListView();
				setLastSync(PREFS_SYNC_ENTRIES);

			}
		};
		api.execute("entries/", getLastSyncJSONString(PREFS_SYNC_ENTRIES));
	}

	protected void setLastSync(String prefs) {
		Calendar now = Calendar.getInstance();
		SharedPreferences settings = getSharedPreferences(prefs, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(LoginActivity.PREFS_SYNC_LAST_SYNC, now.getTimeInMillis());
		editor.commit();
	}

	private long getLastSync(String prefs) {
		SharedPreferences settings = getSharedPreferences(prefs, 0);
		return settings.getLong(LoginActivity.PREFS_SYNC_LAST_SYNC, 0);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.overview_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_entry:
			addEntry();
			return true;
		case R.id.action_select_date:
			showDatePickerDialog();
			return true;
		case R.id.action_select_budget:
			seeBudgets();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void seeBudgets() {
		Intent intent = new Intent(this, BudgetActivity.class);
		intent.putExtra(TIME_IN_MILLIS, calendar.getTimeInMillis());
		
		startActivity(intent);
	}

	public void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				Calendar c = Calendar.getInstance();
				c.set(year, month, day);
				calendar = c;
				getEntries();

			}
		};
		newFragment.show(getFragmentManager(), "datePicker");

	}

	public void addEntry() {
		Intent intent = new Intent(this, AddEntryActivity.class);
		startActivity(intent);
	}
}
