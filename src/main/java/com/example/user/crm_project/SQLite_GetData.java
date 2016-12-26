package com.example.user.crm_project;

import android.content.Context;

import com.example.user.crm_project.database.SQLite_Builder;

import java.util.ArrayList;

/**
 * Created by user on 5/15/2016.
 */
public class SQLite_GetData extends SQLite_Builder {

    String query_name;
    String query_last;
    String query_tel;
    long query_reservation;
    ArrayList<Customer> arrayList_Data;
    //ArrayList<Reservation> arrayList_ReservationTable;
    int counter;
    long row_id;

    public SQLite_GetData(Context context, int version) {
        super(context, version);
    }

    public String getName(String name) {
        this.query_name = name;
        return query_name;
    }

    public String getLast(String last) {
        this.query_last = last;
        return query_last;
    }

    public String getTel(String tel) {
        this.query_tel = tel;
        return query_tel;
    }

    public long getReservation(long reservation) {
        this.query_reservation = reservation;
        return query_reservation;
    }

    public int getCounter(int count) {
        this.counter = count;
        return counter;
    }

    public ArrayList getData() {
//        SQLiteDatabase database = getWritableDatabase();
//        Cursor cursor = database.query(MAIN_TABLE, new String[]{"reservation_id", "name", "last", "tel"},
//                null, null, null, null, null);
//        this.arrayList_Data = new ArrayList<>();
//        ArrayList<Reservation> array_res = new ArrayList<>();
//        if (cursor.moveToFirst()) {
//            do {
//                row_id = cursor.getLong(cursor.getColumnIndex("reservation_id"));
//                String name = cursor.getString(cursor.getColumnIndex("name"));
//                String last = cursor.getString(cursor.getColumnIndex("last"));
//                String tel = cursor.getString(cursor.getColumnIndex("tel"));
//                long reservation = 0;
//
//                //***Reservation Cursor***
//                Cursor reservations_Cursor = database.query("reservation", null, "user_reservation_id= ?",
//                        new String[]{"" + row_id}, null, null, null);
//                String free_text = null;
//                String payment = null;
//                String branch = null;
//                String seller = null;
//                if (reservations_Cursor.moveToFirst()) {
//                    do {
//                        reservation = reservations_Cursor.getLong(reservations_Cursor.getColumnIndex("user_reservation_id"));
//                        free_text = reservations_Cursor.getString(reservations_Cursor.getColumnIndex("free_text"));
//                        payment = reservations_Cursor.getString(reservations_Cursor.getColumnIndex("payment"));
//                        branch = reservations_Cursor.getString(reservations_Cursor.getColumnIndex("branch"));
//                        seller = reservations_Cursor.getString(reservations_Cursor.getColumnIndex("the_seller"));
//                    } while (reservations_Cursor.moveToNext());
//                    reservations_Cursor.close();
//                }
//
//                if (counter == 4) {
//                    if (query_name.equals(name) && query_last.equals(last) && query_tel.equals(tel) &&
//                            query_reservation == row_id) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }
//                }
//                if (counter == 3) {
//                    if (query_name.equals(name) && query_last.equals(last) && query_tel.equals(tel)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_name.equals(name) && query_tel.equals(tel) && query_reservation == row_id) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_last.equals(last) && query_tel.equals(tel) && query_reservation == row_id) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }else if (query_name.equals(name) && query_last.equals(last) && query_reservation == row_id){
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }
//                }
//                if (counter == 2) {
//                    if (query_name.equals(name) && query_last.equals(last)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_name.equals(name) && query_tel.equals(tel)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_name.equals(name) && query_reservation == row_id) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_reservation == row_id && query_tel.equals(tel)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }else if (query_last.equals(last) && query_tel.equals(tel)){
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }else if (query_last.equals(last) && query_reservation == row_id){
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }
//                }
//                if (counter == 1) {
//                    if (query_name.equals(name)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_last.equals(last)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_tel.equals(tel)) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    } else if (query_reservation == row_id) {
//                        arrayList_Data.add(new Customer(name, last, tel, row_id));
//                        array_res.add(new Reservation(reservation, free_text, payment,
//                                branch, seller));
//                    }
//                }
//
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//        this.arrayList_ReservationTable = array_res;
//        Log.d("myArr", array_res.toString());
        return arrayList_Data;
    }
}
