package com.example.user.crm_project.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hackeru on 21/03/2016.
 */
public class SQLite_Builder extends SQLiteOpenHelper{

    public static final String MAIN_TABLE = "customers";

    public SQLite_Builder(Context context, int version) {
        super(context, MAIN_TABLE, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mainTable = "CREATE TABLE IF NOT EXISTS " + MAIN_TABLE +
                " (name TEXT, tel TEXT, mail TEXT)";
        db.execSQL(mainTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
