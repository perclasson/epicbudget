package com.epicbudget;

import com.epicbudget.CurrencyContract.Currency;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class EntryTypeContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public EntryTypeContract() {
	}

	public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ EntryType.TABLE_NAME
			+ " ("
			+ EntryType._ID
			+ " INTEGER PRIMARY KEY, "
			+ EntryType.COLUMN_NAME_NAME
			+ " varchar(255) NOT NULL, "
			+ EntryType.COLUMN_NAME_CATEGORY
			+ " varchar(255) NOT NULL);";
	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ EntryType.TABLE_NAME + ";";

	public static abstract class EntryType implements BaseColumns {
		public static final String TABLE_NAME = "entry_type";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_CATEGORY = "category";
		public static final String[] PROJECTION = { EntryType._ID,
				EntryType.COLUMN_NAME_NAME, EntryType.COLUMN_NAME_CATEGORY };
	}

	public static long insert(SQLiteDatabase db, long id, String name,
			String category) {
		ContentValues values = new ContentValues();
		values.put(EntryType._ID, id);
		values.put(EntryType.COLUMN_NAME_NAME, name);
		values.put(EntryType.COLUMN_NAME_CATEGORY, category);
		return db.insert(EntryType.TABLE_NAME, null, values);
	}

	public static Cursor get(SQLiteDatabase db, Long id) {
		Cursor c = db.query(EntryType.TABLE_NAME, EntryType.PROJECTION,
				EntryType._ID + " = ?", new String[] { id.toString() }, null,
				null, null);
		return c;
	}

	public Cursor getAll(SQLiteDatabase db) {
		Cursor c = db.query(EntryType.TABLE_NAME, EntryType.PROJECTION, null,
				null, null, null, null);
		return c;

	}

	public static Cursor getCategory(SQLiteDatabase db, String category) {
		return db.query(EntryType.TABLE_NAME, EntryType.PROJECTION,
				EntryType.COLUMN_NAME_CATEGORY + " = ?",
				new String[] { category }, null, null, null);
	}

	public static boolean delete(SQLiteDatabase db, Long id) {
		return (db.delete(EntryType.TABLE_NAME, EntryType._ID + " = ?",
				new String[] { id.toString() }) != 0);

	}
}
