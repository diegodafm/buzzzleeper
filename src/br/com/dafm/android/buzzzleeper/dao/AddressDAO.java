package br.com.dafm.android.buzzzleeper.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import br.com.dafm.android.buzzzleeper.database.BuzzzleeperDBHelper;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

public class AddressDAO {

	// Database fields
	private SQLiteDatabase database;
	private BuzzzleeperDBHelper dbHelper;

	private String[] allColumns = { "id", "name", "address", "lat", "lng",
			"buffer", "ringtone", "status" };

	public AddressDAO(Context context) {
		dbHelper = new BuzzzleeperDBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public BlrAddress save(BlrAddress address) {
		ContentValues values = new ContentValues();
		values.put("name", address.getName());
		values.put("address", address.getAddress());
		values.put("lat", address.getLat());
		values.put("lng", address.getLng());
		values.put("buffer", address.getBuffer());
		values.put("ringtone", address.getRingtone());
		values.put("status", address.getStatus());

		BlrAddress newAddress = null;
		try {
			this.open();
			long insertId = database.insert("address", null, values);
			Cursor cursor = database.query("address", allColumns, "id = " + insertId, null, null, null, null);
			cursor.moveToFirst();
			newAddress = cursorToAddress(cursor);
			cursor.close();
			this.close();

		} catch (Exception e) {
			new RuntimeException();
			e.getMessage();
		}
		return newAddress;
	}
	
	public BlrAddress update(BlrAddress address){
		String strFilter = "id=" + address.getId();
		ContentValues args = new ContentValues();
		args.put("name", address.getName());
		args.put("address", address.getAddress());
		args.put("lat", address.getLat());
		args.put("lng", address.getLng());
		args.put("buffer", address.getBuffer());
		args.put("ringtone", address.getRingtone());
		args.put("status", address.getStatus());
		
		BlrAddress newAddress = null;
		try {
			this.open();
			database.update("address", args, strFilter, null);
			Cursor cursor = database.query("address", allColumns, strFilter , null, null, null, null);
			cursor.moveToFirst();
			newAddress = cursorToAddress(cursor);
			this.clone();
		} catch (Exception e) {
			new RuntimeException();
			e.getMessage();
		}
		return newAddress;
	}

	public void delete(BlrAddress address) {
		long id = address.getId();
		System.out.println("Comment deleted with id: " + id);
		this.open();
		database.delete("address", "id  = " + id, null);
		this.close();
	}

	public List<BlrAddress> getAllAddress() {
		List<BlrAddress> addresses = new ArrayList<BlrAddress>();

		try {
			this.open();

			Cursor cursor = database.query("address", allColumns, null, null,
					null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				BlrAddress address = cursorToAddress(cursor);
				addresses.add(address);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			new RuntimeException();
			e.getMessage();
		}
		return addresses;
	}

	private BlrAddress cursorToAddress(Cursor cursor) {
		BlrAddress address = new BlrAddress();
		address.setId(cursor.getLong(0));
		address.setName(cursor.getString(1));
		address.setAddress(cursor.getString(2));
		address.setLat(Double.valueOf(cursor.getString(3)));
		address.setLng(Double.valueOf(cursor.getString(4)));
		address.setBuffer(Integer.parseInt(cursor.getString(5)));
		address.setRingtone(cursor.getString(6));
		address.setStatus(Boolean.valueOf(cursor.getString(7)));
		return address;
	}

	public BlrAddress findById(Integer id) {
		BlrAddress address = new BlrAddress();
		try {
			this.open();
			Cursor cursor = database.rawQuery(
					"select * from address where id =" + id, null);
			cursor.moveToFirst();
			address = cursorToAddress(cursor);
			cursor.close();
		} catch (Exception e) {
			new RuntimeException();
			e.getMessage();
		}

		return address;
	}
}