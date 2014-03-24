package com.epicbudget;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epicbudget.BudgetContract.Budget;
import com.epicbudget.EntryContract.Entry;
import com.epicbudget.EntryTypeContract.EntryType;

public class BudgetCursorAdapter extends SimpleCursorAdapter {
	private int layout;
	private LayoutInflater inflater;
	private BudgetActivity budgetActivity;
	private long from, to, userId;
	private String currencySymbol;
	private Context context;

	public BudgetCursorAdapter(Context context, int layout, Cursor data,
			String[] fields, int[] is, int d, BudgetActivity budgetActivity,
			Long from, Long to, Long userId, String currencySymbol) {
		super(context, layout, data, fields, is, d);
		this.context = context;
		this.layout = layout;
		this.inflater = LayoutInflater.from(context);
		this.budgetActivity = budgetActivity;
		this.from = from;
		this.to = to;
		this.userId = userId;
		this.currencySymbol = currencySymbol;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(final View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.budget_row);
		final Long id = cursor
				.getLong(cursor.getColumnIndexOrThrow(Budget._ID));

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNumberPickerDialog(id);
			}

		});

		TextView entryTypeTextView = (TextView) view
				.findViewById(R.id.entry_type_text);
		long entryTypeId = cursor.getLong(cursor
				.getColumnIndexOrThrow(Budget.COLUMN_NAME_ENTRY_TYPE));

		DbHelper dbHelper = new DbHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = EntryTypeContract.get(db, entryTypeId);
		if (c.moveToNext()) {
			String name = c.getString(c
					.getColumnIndexOrThrow(EntryType.COLUMN_NAME_NAME));
			entryTypeTextView.setText(name);
		}
		c.close();

		c = EntryContract.getIntervalInEntryType(db, from, to, userId,
				entryTypeId);

		Double amountSum = 0.0;

		if (c.moveToNext()) {
			amountSum = c.getDouble(c
					.getColumnIndexOrThrow(Entry.COLUMN_NAME_AMOUNT_SUM));
		}

		c.close();

		Double amountMax = cursor.getDouble(cursor
				.getColumnIndexOrThrow(Budget.COLUMN_NAME_AMOUNT));

		TextView amountTextView = (TextView) view
				.findViewById(R.id.amount_text);
		amountTextView.setText(amountSum.toString() + " " + currencySymbol
				+ " of " + amountMax + " " + currencySymbol);

		if (amountSum > amountMax) {
			layout.setBackgroundResource(R.color.red);
		}

	}

	protected void showNumberPickerDialog(final Long id) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Set amount");

		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				Double value = 0d;
				try {
					value = Double.valueOf(input.getText().toString());
				}
				catch (NumberFormatException e) {
					Toast.makeText(context, "Error.", Toast.LENGTH_SHORT).show();
					return;
				}
				JSONObject obj = new JSONObject();
				try {
					obj.put("amount", value);
					obj.put("id", id);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				API api = new API(context) {
					protected void onPostExecute(String result) {
						JSONObject response;
						try {
							response = new JSONObject(result);
							if (response.getBoolean("success")) {
								budgetActivity.getBudgets();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					};
				};
				api.execute("update_budget/", obj.toString());
			}
		});

		alert.show();

	}

}
