package com.epicbudget;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class CurrencyContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public CurrencyContract() {
	}

	public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ Currency.TABLE_NAME
			+ " ("
			+ Currency._ID
			+ " INTEGER PRIMARY KEY, "
			+ Currency.COLUMN_NAME_NAME
			+ " varchar(3) NOT NULL, "
			+ Currency.COLUMN_NAME_SYMBOL
			+ " varchar(5) NOT NULL);";
	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ Currency.TABLE_NAME + ";";

	// TODO: refactor away this class
	public static abstract class Currency implements BaseColumns {
		public static final String TABLE_NAME = "currency";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_SYMBOL = "symbol";
		public static final String[] PROJECTION = { Currency._ID,
				Currency.COLUMN_NAME_NAME, Currency.COLUMN_NAME_SYMBOL };
	}

	public static long insert(SQLiteDatabase db, long id, String name,
			String symbol) {
		ContentValues values = new ContentValues();
		values.put(Currency._ID, id);
		values.put(Currency.COLUMN_NAME_NAME, name);
		values.put(Currency.COLUMN_NAME_SYMBOL, symbol);
		return db.insert(Currency.TABLE_NAME, null, values);
	}

	public static Cursor get(SQLiteDatabase db, Long id) {
		Cursor c = db.query(Currency.TABLE_NAME, Currency.PROJECTION,
				Currency._ID + " = " + id.toString(), null, null, null, null);
		return c;
	}

	public static Cursor getAll(SQLiteDatabase db) {
		Cursor c = db.query(Currency.TABLE_NAME, Currency.PROJECTION, null,
				null, null, null, null);
		return c;

	}

	public static boolean delete(SQLiteDatabase db, Long id) {
		return (db.delete(Currency.TABLE_NAME, Currency._ID + " = ?",
				new String[] { id.toString() }) != 0);
	}
}
