package com.epicbudget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class BudgetContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public BudgetContract() {
	}

	public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ Budget.TABLE_NAME
			+ " ("
			+ Budget._ID
			+ " INTEGER PRIMARY KEY, "
			+ Budget.COLUMN_NAME_ENTRY_TYPE
			+ " REAL NOT NULL, "
			+ Budget.COLUMN_NAME_USER_ID
			+ " REAL NOT NULL, "
			+ Budget.COLUMN_NAME_AMOUNT + " REAL NOT NULL);";
	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ Budget.TABLE_NAME + ";";

	public static abstract class Budget implements BaseColumns {
		public static final String TABLE_NAME = "budget";
		public static final String COLUMN_NAME_ENTRY_TYPE = "expense_type";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String[] PROJECTION = { Budget._ID,
				Budget.COLUMN_NAME_ENTRY_TYPE, Budget.COLUMN_NAME_AMOUNT };
		public static final String COLUMN_NAME_USER_ID = "user_id";
	}

	public static long insert(SQLiteDatabase db, long id, long entryType,
			double amount, long userId) {
		ContentValues values = new ContentValues();
		values.put(Budget._ID, id);
		values.put(Budget.COLUMN_NAME_ENTRY_TYPE, entryType);
		values.put(Budget.COLUMN_NAME_AMOUNT, amount);
		values.put(Budget.COLUMN_NAME_USER_ID, userId);
		return db.insert(Budget.TABLE_NAME, null, values);
	}

	public static Cursor get(SQLiteDatabase db, Long id, Long userId) {
		return db.query(Budget.TABLE_NAME, Budget.PROJECTION, Budget._ID
				+ " = ? AND " + Budget.COLUMN_NAME_USER_ID + " = ?",
				new String[] { id.toString(), userId.toString() }, null, null,
				null);
	}

	public static Cursor getAll(SQLiteDatabase db, Long userId) {
		Cursor c = db.query(Budget.TABLE_NAME, Budget.PROJECTION,
				Budget.COLUMN_NAME_USER_ID + " = ?",
				new String[] { userId.toString() }, null, null, null);
		return c;

	}

	public static boolean delete(SQLiteDatabase db, Long id) {
		return (db.delete(Budget.TABLE_NAME, Budget._ID + " = ?",
				new String[] { id.toString() }) != 0);
	}

	public static void update(SQLiteDatabase db, Long id, long entryType,
			double amount) {
		ContentValues values = new ContentValues();
		values.put(Budget.COLUMN_NAME_ENTRY_TYPE, entryType);
		values.put(Budget.COLUMN_NAME_AMOUNT, amount);
		db.update(Budget.TABLE_NAME, values,
				Budget._ID + " = ?",
				new String[] { id.toString() });
	}
}
