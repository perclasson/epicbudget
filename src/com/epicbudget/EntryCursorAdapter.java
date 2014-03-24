package com.epicbudget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epicbudget.EntryContract.Entry;
import com.epicbudget.EntryTypeContract.EntryType;

public class EntryCursorAdapter extends SimpleCursorAdapter {
	private int layout;
	private LayoutInflater inflater;
	private OverviewActivity overviewActivity;
	private String currencySymbol;

	public EntryCursorAdapter(Context context, int layout, Cursor data,
			String[] fields, int[] is, int d, OverviewActivity overviewActivity, String currencySymbol) {
		super(context, layout, data, fields, is, d);
		this.overviewActivity = overviewActivity;
		this.layout = layout;
		this.inflater = LayoutInflater.from(context);
		this.currencySymbol = currencySymbol;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(final View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.row_layout);
		int categoryIndex = cursor
				.getColumnIndexOrThrow(Entry.COLUMN_NAME_CATEGORY);
		if (cursor.getString(categoryIndex).equals("income")) {
			layout.setBackgroundResource(R.color.green);
		} else {
			layout.setBackgroundResource(R.color.red);
		}

		int descriptionIndex = cursor
				.getColumnIndexOrThrow(Entry.COLUMN_NAME_DESCRIPTION);
		final String description = cursor.getString(descriptionIndex);

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (description.length() > 0) {
					Toast.makeText(v.getContext(), description, Toast.LENGTH_SHORT)
					.show();
				}
			}
		});

		TextView amountTextView = (TextView) view
				.findViewById(R.id.amount_text);
		amountTextView.setText(amountTextView.getText() + " " + currencySymbol);

		int entryTypeIndex = cursor
				.getColumnIndexOrThrow(Entry.COLUMN_NAME_ENTRY_TYPE);
		long entryTypeId = (long) cursor.getLong(entryTypeIndex);
		String entryTypeName = null;

		DbHelper dbHelper = new DbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = EntryTypeContract.get(db, entryTypeId);

		if (c.moveToNext()) {
			int entryTypeNameIndex = c
					.getColumnIndexOrThrow(EntryType.COLUMN_NAME_NAME);
			entryTypeName = c.getString(entryTypeNameIndex);
		}

		TextView entryTypeTextView = (TextView) view
				.findViewById(R.id.entry_type_text);

		if (entryTypeName != null) {
			entryTypeTextView.setText(entryTypeName);
		} else {
			entryTypeTextView.setText("-");
		}

		int dateIndex = cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_DATE);
		Long dateLong = cursor.getLong(dateIndex);
		Date date = new Date(dateLong);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		TextView dateText = (TextView) view.findViewById(R.id.date_text);
		dateText.setText(sdf.format(date).toString());

		int idIndex = cursor.getColumnIndexOrThrow(Entry._ID);
		final long id = cursor.getLong(idIndex);

		Button discardButton = (Button) view.findViewById(R.id.discard_button);
		discardButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				DbHelper dbHelper = new DbHelper(v.getContext());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				EntryContract.delete(db, id);
				JSONObject obj = new JSONObject();
				try {
					obj.put("id", id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				API api = new API(view.getContext()) {
					@Override
					protected void onPostExecute(String result) {
						JSONObject response;
						try {
							response = new JSONObject(result);
							if (response.getBoolean("success")) {
								Toast.makeText(view.getContext(),
										"Entry deleted.", Toast.LENGTH_SHORT)
										.show();
								overviewActivity.fillListView();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				};
				api.execute("delete_entry/", obj.toString());
			}
		});
	}
}
