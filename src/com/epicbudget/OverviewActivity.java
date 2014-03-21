package com.epicbudget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class OverviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM");
		Button month = (Button) findViewById(R.id.date_button);
		if (month != null)
			month.setText(sdf.format(date));
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showDatePickerDialog(View view) {
		DialogFragment newFragment = new DatePickerFragment() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {

			}
		};
		newFragment.show(getFragmentManager(), "datePicker");

	}

	public void addEntry() {
		Intent intent = new Intent(this, AddEntryActivity.class);
		startActivity(intent);
	}
}
