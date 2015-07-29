package com.swmem.voicetoview.util;

import java.util.ArrayList;
import java.util.List;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;
import com.swmem.voicetoview.data.Talk;
import com.swmem.voicetoview.data.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
	public static SQLiteDatabase db;

	public static void openOrCreateDB(Context con) {
		if (db == null)
			db = con.openOrCreateDatabase("USEROPTION.db", 2, null);
		synchronized (db) {
			Cursor c = db.rawQuery("SELECT NAME FROM sqlite_master WHERE type = 'table' AND name = 'user'", null);

			if (!c.moveToFirst()) {
				createTable();
				insertUser(new User(Constants.VIEW_OFF, Constants.MALE));
			}
		}
	}

	public static void closeDB() {
		db.close();
	}

	public static void createTable() {
		try {
			db.execSQL("CREATE TABLE user(mode INTEGER, gender INTEGER)");
			db.execSQL("CREATE TABLE talk(num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date TEXT, id TEXT)");
			db.execSQL("CREATE TABLE model(num INTEGER, emotiontype INTEGER, textresult TEXT, time TEXT, FOREIGN KEY (num) REFERENCES talk (num))");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static User selectUser() {
		Cursor c = null;
		User u = null;
		try {
			c = db.rawQuery("SELECT * FROM user", null);
			c.moveToNext();
			u = new User(c.getInt(0), c.getInt(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return u;
	}

	public static void insertUser(User u) {
		try {
			db.execSQL("INSERT INTO user VALUES ('" + u.getMode() + "','"
					+ u.getGender() + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateUser(User u) {
		try {
			db.execSQL("UPDATE user SET mode = '" + u.getMode()
					+ "', gender = '" + u.getGender() + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Talk> selectTalkList() {
		Cursor c = null;
		List<Talk> talkList = new ArrayList<Talk>();
		try {
			c = db.rawQuery("SELECT * FROM talk", null);

			for (int i = 0; i < c.getCount(); i++) {
				c.moveToNext();
				talkList.add(new Talk(c.getInt(0), c.getString(1), c
						.getString(2)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return talkList;
	}

	public static Talk selectTalk() {
		Cursor c = null;
		Talk t = null;
		try {
			c = db.rawQuery("SELECT * FROM talk", null);
			c.moveToLast();
			t = new Talk(c.getInt(0), c.getString(1), c.getString(2));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return t;
	}

	public static void insertTalk(Talk t) {
		try {
			db.execSQL("INSERT INTO talk(date, id) VALUES ('" + t.getDate()
					+ "','" + t.getId() + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteTalk(int key) {
		try {
			db.execSQL("DELETE FROM talk WHERE num = " + key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteAllTalk(Talk t) {
		try {
			db.execSQL("DELETE FROM talk");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertModelList(List<Model> modelList, int key) {
		try {
			for (Model model : modelList) {
				if (model.getTextResult() != null) {
					db.execSQL("INSERT INTO model VALUES ('" + key + "','"
							+ model.getEmotionType() + "','"
							+ model.getTextResult() + "','" + model.getTime()
							+ "')");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Model> selectModelList(int num) {
		Cursor c = null;
		List<Model> modelList = new ArrayList<Model>();

		try {
			c = db.rawQuery("SELECT * FROM model WHERE num = " + num, null);
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToNext();
				modelList.add(new Model(c.getInt(1), c.getString(2), c
						.getString(3)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return modelList;
	}

	public static void deleteModel() {
		try {
			db.execSQL("DELETE FROM model");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
