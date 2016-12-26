package com.example.user.crm_project.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;
import com.example.user.crm_project.database.SQLite_Builder;
import com.example.user.crm_project.interfaces.AutoViewBuilder;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 11/29/2016.
 */

public class AdvanceSearch_Fr extends Fragment implements View.OnClickListener, AutoViewBuilder {

    private SQLiteDatabase database;
    private LinearLayout dynamicLayout, dynamicAdd;
    private SharedPreferences toSharedPreference;
    private EditText name, tel, mail;
    private List<EditText> edList = new ArrayList();
    private String nameSearch, telSearch, mailSearch;
    private View view;
    private Bundle bundle = new Bundle();
    private String[] results;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toSharedPreference = getActivity().getSharedPreferences("dynamicField", MODE_PRIVATE);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float margin = 7 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int toMargin = (int) margin;
        vParams.setMargins(0, 0, 0, toMargin);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.advance_search, container, false);
        dynamicLayout = (LinearLayout) view.findViewById(R.id.search_dynamicView);
        name = (EditText) view.findViewById(R.id.search_name);
        tel = (EditText) view.findViewById(R.id.search_tel);
        mail = (EditText) view.findViewById(R.id.search_mail);
        autoViewBuilder();
        Button search = (Button) view.findViewById(R.id.search_searchButton);
        search.setOnClickListener(this);
        return view;
    }

    public void autoViewBuilder() {
        new AsyncTask<Void, Void, String[]>() {
            protected String[] doInBackground(Void... params) {
                int currentVersion = toSharedPreference.getInt("version", 1);
                database = new SQLite_Builder(getActivity(), currentVersion).getWritableDatabase();
                String select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE;
                Cursor cursor = database.rawQuery(select, null);
                String[] columnsName = cursor.getColumnNames();
                return columnsName;
            }

            protected void onPostExecute(String[] columnsNames) {
                for (int i = 0; i < columnsNames.length - 3; i++) {
                    TextView autoTextView = new TextView(getActivity());
                    EditText autoEditText = new EditText(getActivity());
                    autoTextView.setText(columnsNames[i + 3] + ":" + " ");
                    autoTextView.setTypeface(null, Typeface.BOLD);
                    autoTextView.setTextSize(18);
                    autoEditText.setId(i + 3);
                    autoTextView.setLayoutParams(tvParams);
                    autoTextView.setBackgroundResource(R.drawable.fields_style);
                    autoEditText.setLayoutParams(edParams);
                    autoEditText.setBackgroundResource(R.drawable.fields_style);

                    dynamicAdd = new LinearLayout(getActivity());
                    dynamicAdd.setId(i + 3);
                    dynamicAdd.setBackgroundResource(R.drawable.fields_style);
                    dynamicAdd.setLayoutParams(vParams);
                    dynamicAdd.setOrientation(LinearLayout.HORIZONTAL);

                    dynamicAdd.addView(autoTextView);
                    dynamicAdd.addView(autoEditText);
                    dynamicLayout.addView(dynamicAdd);
                    edList.add(autoEditText);
                }
            }
        }.execute();
        return;
    }

    public void onClick(View v) {
        filterCustomer();
    }

    private void filterCustomer(){
        results = edSearch();
        new AsyncTask<Void, Void, ArrayList<Customer>>() {
            ArrayList<Customer> itemsArray = new ArrayList();
            ArrayList<String> resultList = new ArrayList();

            protected ArrayList<Customer> doInBackground(Void... params) {
                int currentVersion = toSharedPreference.getInt("version", 1);
                database = new SQLite_Builder(getActivity(), currentVersion).getWritableDatabase();
                Cursor cursor;
                if (!results[1].equals("") || !results[2].equals("")) {
                    if (!results[1].equals("")) {
                        String select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE tel=" + "'" + results[1] + "'";
                        cursor = database.rawQuery(select, null);
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                        }
                    }
                    if (!results[2].equals("")) {
                        String select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE mail=" + "'" + results[2] + "'" ;
                        cursor = database.rawQuery(select, null);
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                        }
                    }
                } else {
                    ArrayList<Integer> positions = new ArrayList();
                    Cursor allCursor;
                    String select;
                    String name = results[0];
                    String dynamic1 = "";
                    String dynamic2 = "";
                    String dynamic3 = "";
                    String dynamic4 = "";
                    String dynamic5 = "";
                    String selectAll = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE;
                    allCursor = database.rawQuery(selectAll, null);
                    String[] columnName = allCursor.getColumnNames();
                    for (allCursor.moveToFirst(); !allCursor.isAfterLast(); allCursor.moveToNext()) {
                        for (int i = 0; i < results.length; i++) {
                            String column = allCursor.getString(i);
                            if (column != null) {
                                if (!column.equals("") && column.equals(results[i]) && resultList.size() <= 5) {
                                    if (!resultList.contains(results[i]))
                                        resultList.add(results[i]);
                                    positions.add(i);
                                }
                            }
                        }
                    }
                    if (!resultList.isEmpty()) {
                        if (resultList.size() >= 1) {
                            if (name.equals("")) dynamic1 = resultList.get(0);
                            if (resultList.size() == 1) {
                                select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + columnName[positions.get(0)]
                                        + "=" + "'" + dynamic1 + "')" +
                                        " OR" +
                                        " (name=" + "'" + name + "')";
                                cursor = database.rawQuery(select, null);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                                            cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
                                }
                            }
                        }
                        if (resultList.size() >= 2) {
                            dynamic2 = resultList.get(1);
                            if (resultList.size() == 2) {
                                select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + columnName[positions.get(0)]
                                        + "=" + "'" + dynamic1 + "' AND " + columnName[positions.get(1)] + "=" + "'" + dynamic2 + "')" +
                                        " OR " +
                                        "(name=" + "'" + name + "' AND " + columnName[positions.get(1)] + "=" + "'" + dynamic2 + "' )";
                                cursor = database.rawQuery(select, null);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                                            cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
                                }
                            }
                        }
                        if (resultList.size() >= 3) {
                            dynamic3 = resultList.get(2);
                            if (resultList.size() == 3) {
                                select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + columnName[positions.get(0)]
                                        + "=" + "'" + dynamic1 + "' AND " + columnName[positions.get(1)] + "=" + "'"
                                        + dynamic2 + "' AND " + columnName[positions.get(2)] + "=" + "'" + dynamic3 + "')" +
                                        " OR" +
                                        "(name=" + "'" + name + "' AND " + columnName[positions.get(1)] + "=" + "'" + dynamic2 + "' AND " +
                                        columnName[positions.get(2)] + "=" + "'" + dynamic3 + "')";
                                cursor = database.rawQuery(select, null);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                                            cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
                                }
                            }
                        }
                        if (resultList.size() >= 4) {
                            dynamic4 = resultList.get(3);
                            if (resultList.size() == 4) {
                                select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + columnName[positions.get(0)]
                                        + "=" + "'" + dynamic1 + "' AND " + columnName[positions.get(1)] + "=" + "'"
                                        + dynamic2 + "' AND " + columnName[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                        + columnName[positions.get(3)] + "=" + "'" + dynamic4 + "')" +
                                        " OR " +
                                        "(name=" + "'" + name + "' AND " + columnName[positions.get(1)] + "=" + "'" + dynamic2 + "' AND " +
                                        columnName[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                        + columnName[positions.get(3)] + "=" + "'" + dynamic4 + "')";
                                cursor = database.rawQuery(select, null);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                                            cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
                                }
                            }
                        }
                        if (resultList.size() == 5) {
                            dynamic5 = resultList.get(4);
                            if (resultList.size() == 5) {
                                select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + columnName[positions.get(0)]
                                        + "=" + "'" + dynamic1 + "' AND " + columnName[positions.get(1)] + "=" + "'"
                                        + dynamic2 + "' AND " + columnName[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                        + columnName[positions.get(3)] + "=" + "'" + dynamic4 + "' AND " +
                                        columnName[positions.get(4)] + "=" + "'" + dynamic5 + ")" +
                                        " OR " +
                                        "(name=" + "'" + name + "' AND " + columnName[positions.get(1)] + "=" + "'" + dynamic2 + "' AND " +
                                        columnName[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                        + columnName[positions.get(3)] + "=" + "'" + dynamic4 + "' AND " +
                                        columnName[positions.get(4)] + "=" + "'" + dynamic5 + "')";
                                cursor = database.rawQuery(select, null);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    itemsArray.add(new Customer(cursor.getString(0), cursor.getString(1),
                                            cursor.getString(2), dynamic1, dynamic2, dynamic3, dynamic4, dynamic5));
                                }
                            }
                        }
                    }
                }
                return itemsArray;
            }

            @Override
            protected void onPostExecute(ArrayList<Customer> itemsArr) {
                getItemsResults(itemsArr);
            }
        }.execute();
    }

    public void getItemsResults(ArrayList<Customer> customers){
        Result_Fr result_fr = new Result_Fr();
        bundle.putSerializable("items", customers);
        result_fr.setArguments(bundle);
        FragmentTransaction listTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        listTransaction.replace(R.id.mainContainer, result_fr);
        listTransaction.addToBackStack("");
        listTransaction.commit();
    }

    private String[] edSearch() {
        String[] searchItems = new String[edList.size() + 3];
        telSearch = tel.getText().toString();
        nameSearch = name.getText().toString();
        mailSearch = mail.getText().toString();
        for (int i = 0; i < searchItems.length; i++) {
            searchItems[0] = nameSearch;
            searchItems[1] = telSearch;
            searchItems[2] = mailSearch;
            if (i > 2) {
                LinearLayout vToRemove = (LinearLayout) dynamicLayout.findViewById(i);
                EditText ed = (EditText) vToRemove.getChildAt(1).findViewById(i);
                searchItems[i] = ed.getText().toString();
            }
        }
        return searchItems;
    }
}
