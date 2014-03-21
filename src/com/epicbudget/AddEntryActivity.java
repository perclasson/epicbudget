package com.epicbudget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
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
		fillSpinner(R.array.expense_types);
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
		fillSpinner(R.array.expense_types);
	}

	public void changeTypeToIncome(View view) {
		fillSpinner(R.array.income_types);
	}

	private void fillSpinner(int arrayType) {
		Spinner types = (Spinner) findViewById(R.id.type_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, arrayType, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		types.setAdapter(adapter);
	}

	public void saveEntry() {
		JSONObject obj = new JSONObject();
		RadioGroup typeRadioGroup = (RadioGroup) findViewById(R.id.type_radio_group);
		Integer typeIdRadioButton = typeRadioGroup.getCheckedRadioButtonId();

		RadioGroup intervalRadioGroup = (RadioGroup) findViewById(R.id.interval_radio_group);
		Integer intervalIdRadioButton = intervalRadioGroup
				.getCheckedRadioButtonId();

		EditText amountText = (EditText) findViewById(R.id.amount_text);
		Double amount = null;

		try {
			amount = Double.parseDouble(amountText.getText().toString());
		} catch (NumberFormatException e) {
			errorMessage("Incorrect amount.");
			return;
		}

		EditText descriptionText = (EditText) findViewById(R.id.description_text);
		String description = descriptionText.getText().toString();

		Spinner typeSpinner = (Spinner) findViewById(R.id.type_spinner);
		String type = typeSpinner.getSelectedItem().toString();

		Button dateButton = (Button) findViewById(R.id.date_text_button);
		String date = dateButton.getText().toString();

		try {
			if (typeIdRadioButton == R.id.expense_radio_button) {
				obj.put("entry_type", "expense");
			} else if (typeIdRadioButton == R.id.income_radio_button) {
				obj.put("entry_type", "income");
			}

			if (intervalIdRadioButton == R.id.once_radio_button) {
				obj.put("interval", "once");
			} else if (intervalIdRadioButton == R.id.monthly_radio_button) {
				obj.put("interval", "monthly");
			}

			obj.put("amount", amount);
			obj.put("description", description);
			obj.put("type", type);
			obj.put("date", date);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		APIController api = new APIController(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				System.out.println(result);
			}
		};

		api.execute("add_entry/", obj.toString());

	}
}
