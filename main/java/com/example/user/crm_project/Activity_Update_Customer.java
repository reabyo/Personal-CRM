package com.example.user.crm_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.crm_project.database.SQLite_Builder;
import com.example.user.crm_project.fragments.CustomerFile_Fr;
import com.example.user.crm_project.interfaces.AutoViewBuilder;

import java.util.ArrayList;
import java.util.List;

public class Activity_Update_Customer extends Activity implements View.OnClickListener, AutoViewBuilder {

    private LinearLayout containerLayout, updateLayout;
    private ArrayList<String> results, titles;
    private TextView tv;
    private EditText ed;
    private List<EditText> edList;
    private List<TextView> tvList;
    private SharedPreferences toSharedPreference;
    private SQLiteDatabase database;
    private String[] currentEdList, currentTvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__update__customer);

        Button saveButton = (Button) findViewById(R.id.update_saveButton);
        saveButton.setOnClickListener(this);

        edList = new ArrayList();
        tvList = new ArrayList();

        toSharedPreference = getSharedPreferences("dynamicField", MODE_PRIVATE);

        results = (ArrayList<String>) this.getIntent().getSerializableExtra("results");
        titles = (ArrayList<String>) this.getIntent().getSerializableExtra("titles");

        containerLayout = (LinearLayout) findViewById(R.id.dynamicUpdate);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float marginBottom = 7 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int toBottom = (int) marginBottom;
        vParams.setMargins(0, 0, 0, toBottom);
        autoViewBuilder();
    }

    public void autoViewBuilder() {
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                tv = new TextView(new ContextThemeWrapper(this, R.style.TextStyle));
                tv.setText(titles.get(i) + ":" + " ");
                tv.setLayoutParams(tvParams);
                ed = new EditText(new ContextThemeWrapper(this, R.style.TextStyle));
                ed.setText(results.get(i));
                ed.setLayoutParams(edParams);
                if (i == 1) ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                ed.setId(i);
                tv.setLayoutParams(tvParams);
                tv.setBackgroundResource(R.drawable.fields_style);
                ed.setLayoutParams(edParams);
                ed.setBackgroundResource(R.drawable.fields_style);

                updateLayout = new LinearLayout(this);
                updateLayout.setLayoutParams(vParams);
                updateLayout.setOrientation(LinearLayout.HORIZONTAL);
                updateLayout.addView(tv);
                updateLayout.addView(ed);
                containerLayout.addView(updateLayout);

                tvList.add(tv);
                edList.add(ed);
            }
        }
    }

    public void onClick(View v) {
        currentEdList = edController(edList);
        currentTvList = tvController(tvList);
        final Validation validation = new Validation(Activity_Update_Customer.this, edList.get(0), edList.get(1), edList.get(2));
        validation.setName(currentEdList[0]);
        validation.setTel(currentEdList[1]);
        validation.setMail(currentEdList[2]);
        boolean isValidate = validation.checkMainValidation();
        if (isValidate == false) return;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                SQLite_Builder builder = getBuilder();
                database = builder.getWritableDatabase();
                ContentValues updatedValues = new ContentValues();
                for (int i = 0; i < currentEdList.length; i++) {

                    //*validation check*
                    validation.setTels(builder.getTel());
                    validation.setMails(builder.getMail());
                    if (!results.get(1).equals(currentEdList[1])){
                        boolean isValidTel = validation.telValidation();
                        if (isValidTel == false) return "Invalid input - Number is already exists";
                    }
                    else if (!results.get(2).equals(currentEdList[2])){
                        boolean isValidMail = validation.mailValidation();
                        if (isValidMail == false) return "Invalid input - Mail is already exists";
                    }
                    else if (i > 2){
                        if (!currentEdList[i].equals("")){
                            validation.setGeneralValue(currentEdList[i]);
                            boolean isValid = validation.checkTelAndMail();
                            if (isValid == false || currentEdList[i].equals(currentEdList[1])
                                    || currentEdList[i].equals(currentEdList[2]))
                                return "Invalid input";
                        }
                    }

                    String fieldName = currentTvList[i];
                    String[] splitName = fieldName.split(":");
                    updatedValues.put(splitName[0], currentEdList[i]);
                }
                database.update(SQLite_Builder.MAIN_TABLE, updatedValues, "tel=?", new String[]{results.get(1)});
                return null;
            }

            protected void onPostExecute(String massage) {
                if (massage != null) {
                    if (massage.equals("Invalid input - Number is already exists")) {
                        edList.get(1).requestFocus();
                        edList.get(1).setError("This number is already exists");
                    } else if (massage.equals("Invalid input - Mail is already exists")) {
                        edList.get(2).requestFocus();
                        edList.get(2).setError("This mail is already exists");
                    } else if (massage.equals("Invalid input")) {
                        Toast.makeText(Activity_Update_Customer.this,
                                "Invalid data - Check your input", Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(Activity_Update_Customer.this, "Details was updated", Toast.LENGTH_LONG).show();
                View hideKeyBoard = getCurrentFocus();
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(hideKeyBoard.getWindowToken(), 0);
            }
        }.execute();
        if (currentEdList != null){
            Intent updateCustomerFile = new Intent(this, Activity_Search_Customer.class);
            updateCustomerFile.putExtra("updatedTel", currentEdList[1]);
            updateCustomerFile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(updateCustomerFile);
        }
    }

    public String[] edController(List<EditText> list) {
        String[] viewList = new String[list.size()];
        for (int i = 0; i < viewList.length; i++) {
            EditText e = list.get(i);
            viewList[i] = e.getText().toString().trim();
        }
        return viewList;
    }

    public String[] tvController(List<TextView> list) {
        String[] viewList = new String[list.size()];
        for (int i = 0; i < viewList.length; i++) {
            TextView t = list.get(i);
            viewList[i] = t.getText().toString();
        }
        return viewList;
    }

    public int getCurrentVersion() {
        int currentVersion = toSharedPreference.getInt("version", 1);
        return currentVersion;
    }

    public SQLite_Builder getBuilder() {
        SQLite_Builder sqLite_builder = new SQLite_Builder(Activity_Update_Customer.this, getCurrentVersion());
        return sqLite_builder;
    }
}
