package com.swmem.voicetoview.util;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
	public static SQLiteDatabase db;
	public final int DEFAULT_MODE = 0;
	public final int DEFAULT_TEST_SIZE = 10;
	public final String DEFAULT_TEST_STYLE = "?";

	public static void openOrCreateDB(Context con) {
		if (db == null)
			db = con.openOrCreateDatabase("UserOption.db", 2, null);
		synchronized (db) {
			Cursor c = db
					.rawQuery(
							"select name from sqlite_master where type = 'table' and name = 'user'",
							null);

			if (!c.moveToFirst()) {
				createUser();
				insertUser(new User(Constants.VIEW_OFF, Constants.MALE));
			}
		}
	}

	public static void closeDB() {
		db.close();
	}

	public static void createUser() {
		try {
			db.execSQL("create table user(" + "mode integer, "
					+ "gender integer)");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertUser(User u) {
		try {
			db.execSQL("insert into user values ('" + u.getMode() + "','"
					+ u.getGender() + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateUser(User u) {
		try {
			db.execSQL("update user set mode = '" + u.getMode()
					+ "', gender = '" + u.getGender() + "'");
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
			u.setGender(c.getInt(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return u;
	}
}
