package com.example.user.crm_project.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 11/29/2016.
 */

public class AdvanceSearch_Fr extends Fragment implements View.OnClickListener, AutoViewBuilder {

    private SQLite_Builder builder;
    private LinearLayout dynamicLayout, dynamicAdd;
    private SharedPreferences toSharedPreference;
    private List<TextView> tvList = new ArrayList<>();
    private List<EditText> edList = new ArrayList();
    private View view;
    private TextView hintMassage;
    private Bundle bundle = new Bundle();
    private String[] results, titles;
    private DisplayMetrics displayMetrics;
    private ArrayList<Customer> itemsArray = new ArrayList();
    private ArrayList<Customer> allData = new ArrayList();
    private ArrayList<String> allMails = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toSharedPreference = getActivity().getSharedPreferences("dynamicField", MODE_PRIVATE);
        builder = getBuilder();
        displayMetrics = this.getResources().getDisplayMetrics();
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
        hintMassage = (TextView) view.findViewById(R.id.advancedHint);
        autoViewBuilder();
        Button search = (Button) view.findViewById(R.id.search_searchButton);
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        allData.clear();
        itemsArray.clear();
        allMails.clear();
    }

    private void setHintMassage() {
        LinearLayout.LayoutParams hintParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        hintMassage.setText(Html.fromHtml("You can add fields " + "<b>" + "according to your business needs" + "</b>" + " by " +
                "clicking " + "<b>" + "<big>" + "Add -->  Add fields" + "</big>" + "</b>"));
        float marginTop = 120 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int mt = (int) marginTop;
        hintParams.setMargins(0, mt, 0, 0);
        hintMassage.setLayoutParams(hintParams);
    }

    public void autoViewBuilder() {
        new AsyncTask<Void, Void, String[]>() {
            protected String[] doInBackground(Void... params) {
                Cursor cursor = builder.getCursor();
                String[] columnsName = cursor.getColumnNames();
                return columnsName;
            }

            protected void onPostExecute(String[] columnsNames) {
                ArrayList<String> columnsToShow = new ArrayList<>(Arrays.asList(columnsNames));
                columnsToShow.remove(2);
                columnsToShow.remove(1);
                for (int i = 0; i < columnsToShow.size(); i++) {
                    final TextView autoTextView = new TextView(getActivity());
                    EditText autoEditText = new EditText(getActivity());
                    Button allButton = new Button(getActivity());
                    autoTextView.setText(columnsToShow.get(i) + ":" + " ");
                    autoTextView.setTypeface(null, Typeface.BOLD);
                    autoTextView.setTextSize(18);
                    autoTextView.setId(i);
                    autoEditText.setId(i);
                    autoTextView.setLayoutParams(tvParams);
                    autoTextView.setBackgroundResource(R.drawable.fields_style);
                    autoEditText.setLayoutParams(edParams);
                    autoEditText.setBackgroundResource(R.drawable.fields_style);
                    allButton.setText("All");
                    allButton.setId(i);
                    allButton.setAllCaps(false);
                    allButton.setLayoutParams(bParams);
                    allButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String fieldName = autoTextView.getText().toString();
                            String[] splitName = fieldName.split(":");
                            getAllData(splitName[0]);
                        }
                    });

                    dynamicAdd = new LinearLayout(getActivity());
                    dynamicAdd.setId(i);
                    dynamicAdd.setBackgroundResource(R.drawable.fields_style);
                    dynamicAdd.setLayoutParams(vParams);
                    dynamicAdd.setOrientation(LinearLayout.HORIZONTAL);

                    dynamicAdd.addView(autoTextView);
                    dynamicAdd.addView(autoEditText);
                    dynamicAdd.addView(allButton);
                    dynamicLayout.addView(dynamicAdd);
                    tvList.add(autoTextView);
                    if (tvList.size() == 1) setHintMassage();
                    else hintMassage.setVisibility(View.GONE);
                    edList.add(autoEditText);
                }
            }
        }.execute();
        return;
    }

    public void onClick(View v) {
        filterCustomer();
    }

    private void filterCustomer() {
        titles = tvSearch();
        final ArrayList<String> currentTitles = new ArrayList<>();
        results = edSearch();
        new AsyncTask<Void, Void, ArrayList<Customer>>() {
            protected ArrayList<Customer> doInBackground(Void... params) {
                Cursor cursor = builder.getCursor();
                ArrayList<String> resultList = new ArrayList();
                ArrayList<Integer> positions = new ArrayList();
                String select;
                String dynamic1 = "";
                String dynamic2 = "";
                String dynamic3 = "";
                String dynamic4 = "";
                String dynamic5 = "";
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    for (int i = 0; i < titles.length; i++) {
                        String column = cursor.getString(cursor.getColumnIndex(titles[i]));
                        if (column != null) {
                            if (!column.equals("") && column.equals(results[i]) && resultList.size() <= 5) {
                                if (!resultList.contains(results[i])) {
                                    resultList.add(results[i]);
                                    currentTitles.add(titles[i]);
                                    positions.add(i);
                                }
                            }
                        }
                    }
                }
                if (!resultList.isEmpty()) {
                    if (resultList.size() >= 1) {
                        dynamic1 = resultList.get(0);
                        builder.setDynamic1(dynamic1);
                        if (resultList.size() == 1) {
                            select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + titles[positions.get(0)]
                                    + "=" + "'" + dynamic1 + "')";
                            itemsArray = builder.getAdvancedSearch(select);
                            allMails = builder.getAllMails();
                        }
                    }
                    if (resultList.size() >= 2) {
                        dynamic2 = resultList.get(1);
                        builder.setDynamic2(dynamic2);
                        if (resultList.size() == 2) {
                            select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + titles[positions.get(0)]
                                    + "=" + "'" + dynamic1 + "' AND " + titles[positions.get(1)] + "=" + "'" + dynamic2 + "')";
                            itemsArray = builder.getAdvancedSearch(select);
                            allMails = builder.getAllMails();
                        }
                    }
                    if (resultList.size() >= 3) {
                        dynamic3 = resultList.get(2);
                        builder.setDynamic3(dynamic3);
                        if (resultList.size() == 3) {
                            select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + titles[positions.get(0)]
                                    + "=" + "'" + dynamic1 + "' AND " + titles[positions.get(1)] + "=" + "'"
                                    + dynamic2 + "' AND " + titles[positions.get(2)] + "=" + "'" + dynamic3 + "')";
                            itemsArray = builder.getAdvancedSearch(select);
                            allMails = builder.getAllMails();
                        }
                    }
                    if (resultList.size() >= 4) {
                        dynamic4 = resultList.get(3);
                        builder.setDynamic4(dynamic4);
                        if (resultList.size() == 4) {
                            select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + titles[positions.get(0)]
                                    + "=" + "'" + dynamic1 + "' AND " + titles[positions.get(1)] + "=" + "'"
                                    + dynamic2 + "' AND " + titles[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                    + titles[positions.get(3)] + "=" + "'" + dynamic4 + "')";
                            itemsArray = builder.getAdvancedSearch(select);
                            allMails = builder.getAllMails();
                        }
                    }
                    if (resultList.size() == 5) {
                        dynamic5 = resultList.get(4);
                        builder.setDynamic5(dynamic5);
                        if (resultList.size() == 5) {
                            select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE (" + titles[positions.get(0)]
                                    + "=" + "'" + dynamic1 + "' AND " + titles[positions.get(1)] + "=" + "'"
                                    + dynamic2 + "' AND " + titles[positions.get(2)] + "=" + "'" + dynamic3 + "' AND "
                                    + titles[positions.get(3)] + "=" + "'" + dynamic4 + "' AND " +
                                    titles[positions.get(4)] + "=" + "'" + dynamic5 + "')";
                            itemsArray = builder.getAdvancedSearch(select);
                            allMails = builder.getAllMails();
                        }
                    }
                }
                return itemsArray;
            }

            @Override
            protected void onPostExecute(ArrayList<Customer> result) {
                getItemsResults(result, currentTitles);
            }
        }.execute();
    }

    public void getItemsResults(ArrayList<Customer> toListView, ArrayList<String> colNames) {
        Result_Fr result_fr = new Result_Fr();
        bundle.putSerializable("items", toListView);
        bundle.putSerializable("columnsNames", colNames);
        bundle.putSerializable("allMails", allMails);
        result_fr.setArguments(bundle);
        FragmentTransaction listTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        listTransaction.replace(R.id.mainContainer, result_fr);
        listTransaction.addToBackStack("");
        listTransaction.commit();
    }

    private String[] tvSearch() {
        String[] columnsTitles = new String[tvList.size()];
        for (int i = 0; i < columnsTitles.length; i++) {
            String text = tvList.get(i).getText().toString();
            String[] toSplit = text.split(":");
            columnsTitles[i] = toSplit[0];
        }
        return columnsTitles;
    }

    private String[] edSearch() {
        String[] searchItems = new String[edList.size()];
        for (int i = 0; i < searchItems.length; i++) {
            LinearLayout vToSearch = (LinearLayout) dynamicLayout.findViewById(i);
            EditText edText = (EditText) vToSearch.getChildAt(1).findViewById(i);
            searchItems[i] = edText.getText().toString().trim();
        }
        return searchItems;
    }

    private void getAllData(final String fieldName) {
        new AsyncTask<Void, Void, ArrayList<Customer>>() {
            protected ArrayList<Customer> doInBackground(Void... params) {
                Cursor cursor = builder.getCursor();
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    int columnNum = cursor.getColumnIndex(fieldName);
                    if (cursor.getString(columnNum) != null) {
                        if (!cursor.getString(columnNum).equals("")) {
                            allData.add(new Customer(cursor.getString(0), cursor.getString(1),
                                    cursor.getString(2), cursor.getString(columnNum), "", "", "", ""));
                            if (!cursor.getString(2).equals("")) allMails.add(cursor.getString(2));
                        }
                    }
                }
                return allData;
            }

            protected void onPostExecute(ArrayList<Customer> customersAllData) {
                ArrayList<String> currentTitles = new ArrayList<>();
                currentTitles.add(fieldName);
                getItemsResults(customersAllData, currentTitles);
            }
        }.execute();
    }

    public int getCurrentVersion() {
        int currentVersion = toSharedPreference.getInt("version", 1);
        return currentVersion;
    }

    public SQLite_Builder getBuilder() {
        SQLite_Builder sqLite_builder = new SQLite_Builder(getActivity(), getCurrentVersion());
        return sqLite_builder;
    }
}
