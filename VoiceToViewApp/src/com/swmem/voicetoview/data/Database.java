package com.swmem.voicetoview.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
	public static SQLiteDatabase db;

	public static void openOrCreateDB(Context con) {
		if (db == null)
			db = con.openOrCreateDatabase("UserOption.db", 2, null);
		synchronized (db) {
			Cursor c = db.rawQuery("select name from sqlite_master where type = 'table' and name = 'user'", null);
			
			if (!c.moveToFirst()) {
				createUser();
				insertUser(new User(0, 0, "0"));
			}
		}
	}

	public static void closeDB() {
		db.close();
	}

	public static void createUser() {
		try {
			db.execSQL("create table user(" + "mode integer, "
					+ "textsize integer, " + "teststyle text)");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertUser(User u) {
		try {
			db.execSQL("insert into user values ('" + u.getMode() + "','"
					+ u.getTextSize() + "','" + u.getTextStyle() + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateUser(User u) {
		try {
			db.execSQL("update user set mode = '" + u.getMode()
					+ "', textsize = '" + u.getTextSize() + "', textstyle = '"
					+ u.getTextStyle() + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static User selectUser() {
		Cursor c = null;
		User u = null;
		try {
			c = db.rawQuery("select * from user", null);
			c.moveToNext();
			u = new User();
			u.setMode(c.getInt(0));
			u.setTextSize(c.getInt(1));
			u.setTextStyle(c.getString(2));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return u;
	}
}
