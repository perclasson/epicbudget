package com.epicbudget;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.epicbudget.EntryContract.Entry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.NumberPicker;

public class BudgetActivity extends Activity {
	private long userId;
	private Long fromDate;
	private Long toDate;
	private String currencySymbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_SYNC_USER, 0);
		userId = settings.getLong(LoginActivity.PREFS_SYNC_USER_ID, 0);
		currencySymbol = settings.getString(
				LoginActivity.PREFS_SYNC_CURRENCY_SYMBOL, null);

		Long timeInMillis = null;

		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				timeInMillis = extras.getLong(OverviewActivity.TIME_IN_MILLIS);
			}
		}

		if (timeInMillis == null) {
			timeInMillis = getFirstDayInMonthCalendar().getTimeInMillis();
		}

		fromDate = timeInMillis;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		cal.add(Calendar.MONTH, 1);
		toDate = cal.getTimeInMillis();

		getBudgets();

	}

	public void getBudgets() {
		API api = new API(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					DbHelper dbHelper = new DbHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					JSONArray entries = obj.getJSONArray("budget");
					for (int i = 0; i < entries.length(); i++) {
						String entryString = entries.getString(i);
						JSONObject entry = new JSONObject(entryString);
						Boolean isActive = entry.getBoolean("is_active");
						if (isActive) {
							Cursor c = BudgetContract.get(db,
									entry.getLong("id"), userId);
							if (!c.moveToNext()) {
								BudgetContract.insert(db, entry.getLong("id"),
										entry.getLong("entry_type"),
										entry.getDouble("amount"), userId);
							} else {
								BudgetContract.update(db, entry.getLong("id"),
										entry.getLong("entry_type"),
										entry.getDouble("amount"));
							}
							c.close();
						} else {
							long id = entry.getLong("id");
							Cursor c = BudgetContract.get(db, id, userId);
							if (c.moveToNext()) {
								BudgetContract.delete(db, id);
							}
							c.close();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				fillListViewFromSelectedDate();
			}
		};
		api.execute("budgets/", null);

	}

	protected void fillListViewFromSelectedDate() {
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		BudgetContract budgetContract = new BudgetContract();
		BudgetCursorAdapter adapter = new BudgetCursorAdapter(this,
				R.layout.budget_row_layout, budgetContract.getAll(db, userId),
				new String[] { Entry.COLUMN_NAME_AMOUNT },
				new int[] { R.id.amount_text }, 0, this, fromDate, toDate,
				userId, currencySymbol);
		ListView listview = (ListView) findViewById(R.id.budget_list_view);
		listview.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.budget, menu);
		return true;
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

	private Calendar getFirstDayInMonthCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

}
