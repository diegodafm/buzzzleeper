package br.com.dafm.android.buzzzleeper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BuzzzleeperDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "buzzzleeper.db";
	public static final int DATABASE_VERSION = 3;

	// SQL query string for creating DATABASE_TABLE
	static final String CREATE_DATABASE_ADDRESS = " create table address ( "
			+ " id integer primary key autoincrement, "
			+ " name text not null, " 
			+ " address text not null, "
			+ " lat double not null, " 
			+ " lng double not null, "
			+ " buffer integer not null, "
			+ " ringtone text not null, " 
			+ " status boolean " 
	+ "); ";

	// Constructor
	public BuzzzleeperDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE_ADDRESS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS address");
		onCreate(db);
	}
}