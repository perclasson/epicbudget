package com.epicbudget;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class EntryContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public EntryContract() {
	}

	public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ Entry.TABLE_NAME
			+ " ("
			+ Entry._ID
			+ " INTEGER PRIMARY KEY, "
			+ Entry.COLUMN_NAME_AMOUNT
			+ " REAL NOT NULL, "
			+ Entry.COLUMN_NAME_CATEGORY
			+ " varchar(255) NOT NULL, "
			+ Entry.COLUMN_NAME_DATE
			+ " REAL NOT NULL, "
			+ Entry.COLUMN_NAME_USER_ID
			+ " REAL NOT NULL, "
			+ Entry.COLUMN_NAME_DESCRIPTION
			+ " TEXT, "
			+ Entry.COLUMN_NAME_ENTRY_TYPE
			+ " INTEGER, "
			+ Entry.COLUMN_NAME_INTERVAL + " varchar(255) NOT NULL);";
	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ Entry.TABLE_NAME + ";";

	public static abstract class Entry implements BaseColumns {
		public static final String TABLE_NAME = "entry";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String COLUMN_NAME_AMOUNT_SUM = "amount_sum";
		public static final String COLUMN_NAME_CATEGORY = "category";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_ENTRY_TYPE = "entry_type";
		public static final String COLUMN_NAME_INTERVAL = "interval_text";
		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String[] PROJECTION = { Entry._ID,
				Entry.COLUMN_NAME_AMOUNT, Entry.COLUMN_NAME_ENTRY_TYPE,
				Entry.COLUMN_NAME_CATEGORY, Entry.COLUMN_NAME_DATE,
				Entry.COLUMN_NAME_DESCRIPTION, Entry.COLUMN_NAME_INTERVAL,
				Entry.COLUMN_NAME_USER_ID };
	}

	public static long insert(SQLiteDatabase db, long id, Double amount,
			String category, long date, String description, Integer entryType,
			String interval, long userId) {
		ContentValues values = new ContentValues();
		values.put(Entry._ID, id);
		values.put(Entry.COLUMN_NAME_AMOUNT, amount);
		values.put(Entry.COLUMN_NAME_CATEGORY, category);
		values.put(Entry.COLUMN_NAME_DATE, date);
		values.put(Entry.COLUMN_NAME_DESCRIPTION, description);
		values.put(Entry.COLUMN_NAME_ENTRY_TYPE, entryType);
		values.put(Entry.COLUMN_NAME_INTERVAL, interval);
		values.put(Entry.COLUMN_NAME_USER_ID, userId);
		return db.insert(Entry.TABLE_NAME, null, values);
	}

	public static Cursor get(SQLiteDatabase db, Long id, Long userId) {
		return db.query(Entry.TABLE_NAME, Entry.PROJECTION, Entry._ID
				+ " = ? AND " + Entry.COLUMN_NAME_USER_ID + " = ?",
				new String[] { id.toString(), userId.toString() }, null, null,
				null);
	}

	public static Cursor getAll(SQLiteDatabase db, Long userId) {
		return db.query(Entry.TABLE_NAME, Entry.PROJECTION,
				Entry.COLUMN_NAME_USER_ID + " = ?",
				new String[] { userId.toString() }, null, null,
				Entry.COLUMN_NAME_DATE + " DESC, " + Entry._ID + " DESC");

	}

	public static boolean delete(SQLiteDatabase db, Long id) {
		return (db.delete(Entry.TABLE_NAME, Entry._ID + " = ?",
				new String[] { id.toString() }) != 0);
	}

	public Cursor getInterval(SQLiteDatabase db, Calendar from, Calendar to,
			Long userId) {
		Long fromLong = from.getTimeInMillis();
		Long toLong = to.getTimeInMillis();
		return db.query(
				Entry.TABLE_NAME,
				Entry.PROJECTION,
				Entry.COLUMN_NAME_DATE + " >= ? AND " + Entry.COLUMN_NAME_DATE
						+ " <= ? AND " + Entry.COLUMN_NAME_USER_ID + " = ?",
				new String[] { fromLong.toString(), toLong.toString(),
						userId.toString() }, null, null, Entry.COLUMN_NAME_DATE
						+ " DESC, " + Entry._ID + " DESC");
	}

	public static Cursor getIntervalInEntryType(SQLiteDatabase db, Long from, Long to,
			Long userId, Long entryTypeId) {
		return db.query(
				Entry.TABLE_NAME,
				new String[] { "sum(" + Entry.COLUMN_NAME_AMOUNT + ") as " + Entry.COLUMN_NAME_AMOUNT_SUM },
				Entry.COLUMN_NAME_DATE + " >= ? AND " + Entry.COLUMN_NAME_DATE
						+ " <= ? AND " + Entry.COLUMN_NAME_USER_ID
						+ " = ? AND " + Entry.COLUMN_NAME_ENTRY_TYPE + " = ?",
				new String[] { from.toString(), to.toString(),
						userId.toString(), entryTypeId.toString() }, Entry.COLUMN_NAME_ENTRY_TYPE,
				null, null);
	}

}
