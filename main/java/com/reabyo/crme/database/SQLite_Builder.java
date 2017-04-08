package com.reabyo.crme.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.reabyo.crme.Customer;

import java.util.ArrayList;

/**
 * Created by hackeru on 21/03/2016.
 */
public class SQLite_Builder extends SQLiteOpenHelper {

    public static final String MAIN_TABLE = "customers";
    private SQLiteDatabase database = getWritableDatabase();
    private String dynamic1 = "";
    private String dynamic2 = "";
    private String dynamic3 = "";
    private String dynamic4 = "";
    private String dynamic5 = "";
    private ArrayList<String> allMails = new ArrayList<>();

    public SQLite_Builder(Context context, int version) {
        super(context, MAIN_TABLE, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mainTable = "CREATE TABLE IF NOT EXISTS " + MAIN_TABLE +
                " (Name TEXT, Tel TEXT, Email TEXT)";
        db.execSQL(mainTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void setDynamic1(String dynamic1) {
        this.dynamic1 = dynamic1;
    }

    public void setDynamic2(String dynamic2) {
        this.dynamic2 = dynamic2;
    }

    public void setDynamic3(String dynamic3) {
        this.dynamic3 = dynamic3;
    }

    public void setDynamic4(String dynamic4) {
        this.dynamic4 = dynamic4;
    }

    public void setDynamic5(String dynamic5) {
        this.dynamic5 = dynamic5;
    }

    public ArrayList<String> getAllMails() {
        return allMails;
    }

    public ArrayList<String> getTel() {
        String selectTel = "SELECT Tel FROM " + SQLite_Builder.MAIN_TABLE;
        Cursor telCursor = database.rawQuery(selectTel, null);
        ArrayList<String> tels = new ArrayList<>();
        if (telCursor.moveToFirst()) {
            do {
                String cTel = telCursor.getString(telCursor.getColumnIndex("Tel"));
                tels.add(cTel);
            } while (telCursor.moveToNext());
            telCursor.close();
        }
        return tels;
    }

    public ArrayList<String> getMail() {
        String selectMail = "SELECT Email FROM " + SQLite_Builder.MAIN_TABLE;
        Cursor mailCursor = database.rawQuery(selectMail, null);
        ArrayList<String> mails = new ArrayList<>();
        if (mailCursor.moveToFirst()) {
            do {
                String cMail = mailCursor.getString(mailCursor.getColumnIndex("Email"));
                mails.add(cMail);
            } while (mailCursor.moveToNext());
            mailCursor.close();
        }
        return mails;
    }

    public String[] getColumnsNames(){
        String columns = "SELECT * FROM " + MAIN_TABLE;
        Cursor columnsCursor = database.rawQuery(columns, null);
        String[] columnNames = columnsCursor.getColumnNames();
        return columnNames;
    }

    public ArrayList<Customer> getAllData(){
        ArrayList<Customer> customersArray = new ArrayList<>();
        String select = "SELECT Name, Tel, Email FROM " + SQLite_Builder.MAIN_TABLE;
        Cursor cursor = database.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                String tel = cursor.getString(cursor.getColumnIndex("Tel"));
                String mail = cursor.getString(cursor.getColumnIndex("Email"));
                customersArray.add(new Customer(name, tel, mail));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return customersArray;
    }

    public ArrayList<Customer> getAdvancedSearch(String select){
        ArrayList<Customer> itemsArray = new ArrayList<>();
        Cursor cursor = database.rawQuery(select, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
            if (!cursor.getString(2).equals(""))allMails.add(cursor.getString(2));
        }
        return itemsArray;
    }

    public Cursor getCursor(){
        String select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE;
        Cursor cursor = database.rawQuery(select, null);
        return cursor;
    }
}
