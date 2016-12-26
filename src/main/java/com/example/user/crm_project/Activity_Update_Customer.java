package com.example.user.crm_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.crm_project.database.SQLite_Builder;
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
        final String[] currentEdList = edController(edList);
        final String[] currentTvList = tvController(tvList);
        Validation validation = new Validation(Activity_Update_Customer.this, edList.get(0), edList.get(1), edList.get(2));
        validation.setName(currentEdList[0]);
        validation.setTel(currentEdList[1]);
        validation.setMail(currentEdList[2]);
        boolean isValidate = validation.checkValidation();
        if (isValidate == false) return;
            new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                int currentVersion = toSharedPreference.getInt("version", 1);
                database = new SQLite_Builder(Activity_Update_Customer.this, currentVersion).getWritableDatabase();
                ContentValues updatedValues = new ContentValues();
                for (int i = 0; i < currentEdList.length; i++) {
                    String fieldName = currentTvList[i];
                    String[] splitName = fieldName.split(":");
                    updatedValues.put(splitName[0], currentEdList[i]);
                }
                database.update(SQLite_Builder.MAIN_TABLE, updatedValues, "tel=?", new String[]{results.get(1)});
                return null;
            }
            }.execute();
        Toast.makeText(this, "Details was updated", Toast.LENGTH_LONG).show();
    }

    public String[] edController(List<EditText> list) {
        String[] viewList = new String[list.size()];
        for (int i = 0; i < viewList.length; i++) {
            EditText e = list.get(i);
            viewList[i] = e.getText().toString();
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
}
