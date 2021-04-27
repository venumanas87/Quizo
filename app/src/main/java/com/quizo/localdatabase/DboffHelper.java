package com.quizo.localdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DboffHelper extends SQLiteOpenHelper {

    private static final String name ="OFFLINE" ;
    private static final int version = 1;

    public DboffHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      String sql = "CREATE TABLE NEWS(_id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, DATE TEXT, AUTHOR TEXT, IMAGEURL TEXT)";
      sqLiteDatabase.execSQL(sql);
      String rsql = "CREATE TABLE STORY(_id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, DATE TEXT, AUTHOR TEXT, IMAGEURL TEXT)";
        sqLiteDatabase.execSQL(rsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
