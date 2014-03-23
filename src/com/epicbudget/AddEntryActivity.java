package com.epicbudget;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class AddEntryActivity extends Activity {
	Button dateButton;
	SimpleDateFormat sdf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		dateButton = (Button) findViewById(R.id.date_text_button);
		Date date = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateButton.setText(sdf.format(date));

		// Add spinner objects (expense from beginning)
		fillTypeSpinner("expense");
	}

	private void fillTypeSpinner(String category) {
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = EntryTypeContract.getCategory(db, category);
		List<StringWithTag> list = new ArrayList<StringWithTag>();

		while (c.moveToNext()) {
			list.add(new StringWithTag(c.getString(1), c.getLong(0)));
		}

		Spinner entryTypes = (Spinner) findViewById(R.id.type_spinner);
		ArrayAdapter<StringWithTag> adapter = new ArrayAdapter<StringWithTag>(
				this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		entryTypes.setAdapter(adapter);
		c.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_entry_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_save_entry:
			saveEntry();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialog(View view) {
		// TODO: Date should not be reset when opening the date picker
		DialogFragment newFragment = new DatePickerFragment() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				dateButton.setText(sdf.format(cal.getTime()));
			}
		};
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void errorMessage(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	public void changeTypeToExpense(View view) {
		fillTypeSpinner("expense");
	}

	public void changeTypeToIncome(View view) {
		fillTypeSpinner("income");
	}

	public void saveEntry() {
		JSONObject obj = new JSONObject();

		EditText amountText = (EditText) findViewById(R.id.amount_text);
		Double amount = null;

		try {
			amount = Double.parseDouble(amountText.getText().toString());
		} catch (NumberFormatException e) {
			errorMessage("Incorrect amount.");
			return;
		}

		EditText descriptionText = (EditText) findViewById(R.id.description_text);
		String description = null;
		try {
			description = new String(descriptionText.getText().toString()
					.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}

		Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
		String type = typeSpinner.getSelectedItem().toString();

		Button dateButton = (Button) findViewById(R.id.date_text_button);
		Calendar calendar = Calendar.getInstance();

		Date dateDate = null;
		try {
			dateDate = sdf.parse((String) dateButton.getText());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		calendar.setTime(dateDate);
		calendar.add(Calendar.HOUR, 10);
		long date = calendar.getTimeInMillis();

		try {
			obj.put("interval", "once");
			obj.put("amount", amount);
			obj.put("description", description);
			obj.put("entry_type", type);
			obj.put("date", date);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		APIController api = new APIController(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					boolean isSuccess = obj.getBoolean("success");
					if (isSuccess) {
						EditText amountText = (EditText) findViewById(R.id.amount_text);
						amountText.setText("");
						EditText descriptionText = (EditText) findViewById(R.id.description_text);
						descriptionText.setText("");
						Toast.makeText(getApplicationContext(),
								obj.getString("message"), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		api.execute("add_entry/", obj.toString());

	}
}
