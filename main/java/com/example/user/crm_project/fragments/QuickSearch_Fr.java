package com.example.user.crm_project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private SQLite_Builder builder;
    private SharedPreferences toSharedPreference;
    private ListView listView;
    private ArrayList<Customer> customers;
    private QuickSearch_Ad customerAdapter;
    ArrayList<Customer> customerArray;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toSharedPreference = getActivity().getSharedPreferences("dynamicField", MODE_PRIVATE);
        int currentVersion = toSharedPreference.getInt("version", 1);
        builder = new SQLite_Builder(getActivity(), currentVersion);
        customerArray = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quick_search, container, false);
        search = (EditText) view.findViewById(R.id.search_Ed);
        search.addTextChangedListener(new TextFiltering());
        listView = (ListView) view.findViewById(R.id.quickSearch_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    public void onStart() {
        super.onStart();
        customersArray();
        String updatedTel = getActivity().getIntent().getStringExtra("updatedTel");
        if (updatedTel != null){
            bundle = new Bundle();
            bundle.putString("currentTel", updatedTel);
            startFragment(bundle);
        }
    }

    private void customersArray() {
        new AsyncTask<Void, Void, ArrayList<Customer>>() {
            protected ArrayList<Customer> doInBackground(Void... params) {
                return builder.getAllData();
            }

            protected void onPostExecute(ArrayList<Customer> customersArr) {
                customers = customersArr;
                sortArray(customers);
                showCustomers(customers);
            }
        }.execute();
    }

    private void showCustomers(ArrayList<Customer> customersList) {
        customerAdapter = new QuickSearch_Ad(getContext(), customersList);
        listView.setAdapter(customerAdapter);
    }

    private void sortArray(ArrayList<Customer> customersArr){
        Collections.sort(customersArr, new Comparator<Customer>() {
            @Override
            public int compare(Customer firstItem, Customer secondItem) {
                return firstItem.getName().compareTo(secondItem.getName());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Customer currentCustomer = (Customer) parent.getAdapter().getItem(position);
        bundle = new Bundle();
        String currentTel = currentCustomer.getTel();
        String currentMail = currentCustomer.getMail();
        bundle.putString("currentTel", currentTel);
        bundle.putString("currentMail", currentMail);
        startFragment(bundle);
    }

    private void startFragment(Bundle currentBundle){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CustomerFile_Fr customerFile = new CustomerFile_Fr();
        customerFile.setArguments(currentBundle);
        //customerFile.setTargetFragment(this, 1);
        customerFile.show(fragmentManager, "customerFile");
        getActivity().getIntent().removeExtra("updatedTel");
    }

    private void typeToSearch(String s, int start, int charS) {
        Customer[] items = new Customer[customers.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = customers.get(i);
        }
        for (Customer item : items) {
            if (!item.getName().contains(s) && !item.getTel().contains(s) && !item.getMail().contains(s)) {
                customers.remove(item);
                if (!customerArray.contains(item)) {
                    customerArray.add(item);
                }
            }
        }
        int pos = customerArray.size();
        if (charS == start) {
            for (int i = 0; i < pos; i++) {
                int tempSize = customers.size();
                for (int j = 0; j < tempSize; j++) {
                    if (customerArray.get(i).getTel().equals(customers.get(j).getTel())) break;
                    if (customerArray.get(i).getName().contains(s) || customerArray.get(i).getTel().contains(s) ||
                            customerArray.get(i).getMail().contains(s)) {
                        if (!customerArray.get(i).getTel().equals(customers.get(j).getTel()) && tempSize == (j + 1))
                            customers.add(customerArray.get(i));
                    }
                }
                if (tempSize == 0){
                    if (customerArray.get(i).getName().contains(s) || customerArray.get(i).getTel().contains(s) ||
                            customerArray.get(i).getMail().contains(s)) customers.add(customerArray.get(i));
                }
            }
        }
        sortArray(customers);
        customerAdapter.notifyDataSetChanged();
    }

    private class TextFiltering implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            typeToSearch(s.toString(), start, s.length());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
