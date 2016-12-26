package com.example.user.crm_project.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;
import com.example.user.crm_project.adapters.QuickSearch_Ad;
import com.example.user.crm_project.database.SQLite_Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 11/30/2016.
 */

public class QuickSearch_Fr extends Fragment implements AdapterView.OnItemClickListener {

    private EditText search;
    private SQLiteDatabase database;
    private SharedPreferences toSharedPreference;
    private ListView listView;
    private ArrayList<Customer> customers;
    private QuickSearch_Ad customerAdapter;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toSharedPreference = getActivity().getSharedPreferences("dynamicField", MODE_PRIVATE);
        int currentVersion = toSharedPreference.getInt("version", 1);
        database = new SQLite_Builder(getActivity(), currentVersion).getWritableDatabase();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quick_search, container, false);
        search = (EditText) view.findViewById(R.id.search_Ed);
        search.addTextChangedListener(new TextFiltering());
        listView = (ListView) view.findViewById(R.id.quickSearch_list);
        listView.setOnItemClickListener(this);
        customersArray();
        return view;
    }

    private ArrayList<Customer> customersArray() {
        new AsyncTask<Void, Void, ArrayList<Customer>>() {
            protected ArrayList<Customer> doInBackground(Void... params) {
                ArrayList<Customer> customersArray = new ArrayList<>();
                String select = "SELECT name, tel, mail FROM " + SQLite_Builder.MAIN_TABLE;
                Cursor cursor = database.rawQuery(select, null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String tel = cursor.getString(cursor.getColumnIndex("tel"));
                        String mail = cursor.getString(cursor.getColumnIndex("mail"));
                        customersArray.add(new Customer(name, tel, mail));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return customersArray;
            }

            @Override
            protected void onPostExecute(ArrayList<Customer> customersList) {
                customers = customersList;
                textController();
            }
        }.execute();
        return customers;
    }

    private void textController() {
        customerAdapter = new QuickSearch_Ad(getContext(), customers);
        Collections.sort(customers, new Comparator<Customer>() {
            @Override
            public int compare(Customer firstItem, Customer secondItem) {
                return firstItem.getName().compareTo(secondItem.getName());
            }
        });
        listView.setAdapter(customerAdapter);
    }

    private void toSearch(String s) {
        ArrayList<Customer> tempArray = customers;
        Customer[] items = new Customer[tempArray.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = tempArray.get(i);
        }
        for (Customer item : items) {
            if (!item.getName().contains(s) && !item.getTel().contains(s) && !item.getMail().contains(s)) {
                tempArray.remove(item);
            }
        }
        customerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView pickedTel = (TextView) listView.getChildAt(position).findViewById(R.id.list_tel);
        TextView pickedMail = (TextView) listView.getChildAt(position).findViewById(R.id.list_mail);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CustomerFile_Fr customerFile = new CustomerFile_Fr();
        bundle = new Bundle();
        String currentTel = pickedTel.getText().toString();
        String currentMail = pickedMail.getText().toString();
        bundle.putString("currentTel", currentTel);
        bundle.putString("currentMail", currentMail);
        customerFile.setArguments(bundle);
        customerFile.show(fragmentManager, "customerFile");
    }


    private class TextFiltering implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) {
                customers = customersArray();
                textController();
            }
            toSearch(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
