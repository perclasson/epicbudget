package com.epicbudget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "EpicBudget.db";
	

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CurrencyContract.SQL_CREATE_ENTRIES);
		db.execSQL(EntryTypeContract.SQL_CREATE_ENTRIES);
		db.execSQL(EntryContract.SQL_CREATE_ENTRIES);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(CurrencyContract.SQL_DELETE_ENTRIES);
		db.execSQL(EntryTypeContract.SQL_DELETE_ENTRIES);
		db.execSQL(EntryContract.SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
	
}
