package com.epicbudget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class OverviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		Button month = (Button) findViewById(R.id.month_picker);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM");
		month.setText(sdf.format(date));
	}
	
	public void showDatePickerDialog(View view) {
		DialogFragment newFragment = new DatePickerFragment() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				
			}
		};
	    newFragment.show(getFragmentManager(), "datePicker");

	}
	
	public void addOverview(View view) {
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}
}
