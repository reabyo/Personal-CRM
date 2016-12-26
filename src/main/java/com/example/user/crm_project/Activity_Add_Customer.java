package com.example.user.crm_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
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

import static com.example.user.crm_project.R.id.addField_button;
import static com.example.user.crm_project.R.id.cancelButton;
import static com.example.user.crm_project.R.id.deleteButton;

public class Activity_Add_Customer extends Activity implements View.OnClickListener, AutoViewBuilder {

    private EditText name, tel, mail, autoEditText;
    private TextView addFields, autoTextView;
    private Button save, moveToSearch, moveToCalendar;
    private LinearLayout dynamicLayout, dynamicAdd;
    private View dialogView, removeDialogView;
    private SharedPreferences toSharedPreference;
    private int currentNumber, currentId;
    private List<EditText> edList = new ArrayList();
    private List<TextView> tvList = new ArrayList();
    private boolean isRemoved;
    private SQLiteDatabase database;
    private String fieldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        //*Button*
        save = (Button) findViewById(R.id.add_saveButton);
        save.setOnClickListener(this);
        moveToSearch = (Button) findViewById(R.id.moveToSearch);
        moveToSearch.setOnClickListener(this);
        moveToCalendar = (Button) findViewById(R.id.moveToCalendar);
        moveToCalendar.setOnClickListener(this);

        //*EditText*
        name = (EditText) findViewById(R.id.add_name);
        tel = (EditText) findViewById(R.id.add_tel);
        mail = (EditText) findViewById(R.id.add_mail);

        //*TextView*
        addFields = (TextView) findViewById(R.id.add_addFields);
        addFields.setOnClickListener(this);

        //*LinearLayout*
        dynamicLayout = (LinearLayout) findViewById(R.id.dynamicView);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float margin = 7 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int toMargin = (int) margin;
        vParams.setMargins(0, 0, 0, toMargin);

        toSharedPreference = getSharedPreferences("dynamicField", MODE_PRIVATE);
        autoViewBuilder();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_saveButton:
                //*EditText*
                final String currentName = name.getText().toString();
                final String currentTel = tel.getText().toString();
                final String currentMail = mail.getText().toString();

                //*Validation check*
                Validation validation = new Validation(this, name, tel, mail);
                validation.setName(currentName);
                validation.setTel(currentTel);
                validation.setMail(currentMail);
                boolean isValidate = validation.checkValidation();
                if (isValidate == false) return;

                //*Add Customer*
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        int currentVersion = toSharedPreference.getInt("version", 1);
                        database = new SQLite_Builder(Activity_Add_Customer.this, currentVersion).getWritableDatabase();
                        String[] tv = tvController(tvList);
                        String[] ed = edController(edList);
                        ContentValues main_values = new ContentValues();

                        //check if tel / mail already exists in the database
                        String selectTel = "SELECT tel, mail FROM " + SQLite_Builder.MAIN_TABLE;
                        Cursor telCursor = database.rawQuery(selectTel, null);
                        if (telCursor.getCount() > 0) {
                            if (telCursor.moveToFirst()) {
                                do {
                                    String cTel = telCursor.getString(telCursor.getColumnIndex("tel"));
                                    String cMail = telCursor.getString(telCursor.getColumnIndex("mail"));
                                    if (cTel.equals(currentTel) && cMail.equals(currentMail)) return "invalid input";
                                    if (cTel.equals(currentTel)) return "invalid tel";
                                    else main_values.put("tel", currentTel);
                                    if (cMail.equals(currentMail)) return "invalid mail";
                                    else main_values.put("mail", currentMail);
                                } while (telCursor.moveToNext());
                                telCursor.close();
                            }
                        }else {
                            main_values.put("tel", currentTel);
                            main_values.put("mail", currentMail);
                        }

                        main_values.put("name", currentName);
                        if (tv != null && ed != null) {
                            if (tv.length > 0 && ed.length > 0) {
                                for (int i = 0; i < tv.length; i++) {
                                    String fieldName = tv[i];
                                    String[] splitName = fieldName.split(":");
                                    main_values.put(splitName[0], ed[i]);
                                }
                            }
                        }
                        database.insert(SQLite_Builder.MAIN_TABLE, null, main_values);
                        return null;
                    }
                    protected void onPostExecute(String massage) {
                        if (massage == null){
                            name.setText("");
                            tel.setText("");
                            mail.setText("");
                            for (int i = 0; i < edList.size(); i++) {
                                edList.get(i).setText("");
                            }
                            Toast.makeText(Activity_Add_Customer.this, "Saved", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (massage.equals("invalid input")){
                                tel.requestFocus();
                                tel.setError("This number is already exists");
                                mail.requestFocus();
                                mail.setError("This mail is already exists");
                            }
                            else if (massage.equals("invalid tel")) {
                                tel.requestFocus();
                                tel.setError("This number is already exists");
                            } else if (massage.equals("invalid mail")) {
                                mail.requestFocus();
                                mail.setError("This mail is already exists");
                            }
                        }
                    }
                }.execute();
                break;

            case R.id.add_addFields:
                addFieldDialog();
                break;

            case R.id.moveToSearch:
                Intent toSearch = new Intent(this, Activity_Search_Customer.class);
                startActivity(toSearch);
                break;
            case R.id.moveToCalendar:
                Intent toCalendar = new Intent(this, Activity_Calendar.class);
                startActivity(toCalendar);
                break;
        }
    }

    private void addFieldDialog() {
        AlertDialog createDialog = new AlertDialog.Builder(this).create();
        dialogView = getLayoutInflater().inflate(R.layout.add_field_dialog, null);
        createDialog.setView(dialogView);
        dialogView.findViewById(addField_button).setOnClickListener(new DialogListener(createDialog));
        createDialog.show();
    }

    private void removeFieldDialog() {
        AlertDialog removeDialog = new AlertDialog.Builder(Activity_Add_Customer.this).create();
        removeDialogView = getLayoutInflater().inflate(R.layout.delete_dialog, null);
        removeDialog.setView(removeDialogView);
        removeDialogView.findViewById(deleteButton).setOnClickListener(new DialogListener(removeDialog));
        removeDialogView.findViewById(cancelButton).setOnClickListener(new DialogListener(removeDialog));
        removeDialog.show();
    }

    public void autoViewBuilder() {
        currentNumber = toSharedPreference.getInt("fieldNumber", 0);
        for (int i = 0; i < currentNumber; i++) {
            String currentName = toSharedPreference.getString("fieldName" + i, "");
            if (!toSharedPreference.contains("fieldName" + i)) {
                continue;
            }
            autoTextView = new TextView(this);
            autoEditText = new EditText(this);
            autoTextView.setTypeface(null, Typeface.BOLD);
            autoTextView.setTextSize(18);
            autoEditText.setId(i);
            autoTextView.setText(currentName + ":" + " ");
            autoTextView.setLayoutParams(tvParams);
            autoTextView.setBackgroundResource(R.drawable.fields_style);
            autoEditText.setLayoutParams(edParams);
            autoEditText.setBackgroundResource(R.drawable.fields_style);

            dynamicAdd = new LinearLayout(this);
            dynamicAdd.setId(i);
            dynamicAdd.setBackgroundResource(R.drawable.fields_style);
            dynamicAdd.setLayoutParams(vParams);
            dynamicAdd.setOrientation(LinearLayout.HORIZONTAL);
            dynamicAdd.addView(autoTextView);
            dynamicAdd.addView(autoEditText);
            dynamicLayout.addView(dynamicAdd);
            autoEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    currentId = v.getId();
                    removeFieldDialog();
                    return true;
                }
            });
            tvList.add(autoTextView);
            edList.add(autoEditText);
        }

        return;
    }

    private void addNewField() {
        autoEditText = new EditText(this);
        autoTextView = new TextView(this);
        EditText add = (EditText) dialogView.findViewById(R.id.addField_name);
        add.setText(add.getText().toString().replace(" ", "_"));
        fieldName = add.getText().toString();
        if (fieldName.equals("")){
            return;
        }
        autoTextView.setText(fieldName + ":" + " ");
        autoTextView.setTypeface(null, Typeface.BOLD);
        autoTextView.setTextSize(18);
        for (int i = 0; i < tvList.size(); i++) {
            if (tvList.get(i).getText().toString().equals(fieldName + ":")) {
                Toast.makeText(Activity_Add_Customer.this, "This field's name is already in use." + "\n" +
                        "Please choose a different name", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        autoTextView.setLayoutParams(tvParams);
        autoEditText.setLayoutParams(edParams);
        autoTextView.setBackgroundResource(R.drawable.fields_style);
        autoEditText.setBackgroundResource(R.drawable.fields_style);

        dynamicAdd = new LinearLayout(this);
        dynamicAdd.setBackgroundResource(R.drawable.fields_style);
        dynamicAdd.setLayoutParams(vParams);
        dynamicAdd.setOrientation(LinearLayout.HORIZONTAL);
        dynamicAdd.addView(autoTextView);
        dynamicAdd.addView(autoEditText);
        dynamicLayout.addView(dynamicAdd);
        tvList.add(autoTextView);
        edList.add(autoEditText);
        Toast.makeText(Activity_Add_Customer.this, "Field was added", Toast.LENGTH_SHORT).show();

        autoEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currentId = v.getId();
                removeFieldDialog();
                return true;
            }
        });
        sqliteController();

        //*SharedPreference add*
        SharedPreferences.Editor editor = toSharedPreference.edit();
        currentNumber = toSharedPreference.getInt("fieldNumber", 0);
        autoEditText.setId(currentNumber);
        dynamicAdd.setId(currentNumber);
        editor.putString("fieldName" + currentNumber, fieldName);
        currentNumber++;
        editor.putInt("fieldNumber", currentNumber);
        editor.commit();

        return;
    }

    private void removeField() {
        int vId = currentId;
        LinearLayout vToRemove = (LinearLayout) dynamicLayout.findViewById(vId);

        TextView tvToRemove = (TextView) vToRemove.getChildAt(0);
        EditText edToRemove = (EditText) vToRemove.getChildAt(1);

        dynamicLayout.removeView(vToRemove);
        edList.remove(edToRemove);
        tvList.remove(tvToRemove);
        toSharedPreference.edit().remove("fieldName" + vId).commit();
        sqliteController();
        isRemoved = true;
        Toast.makeText(Activity_Add_Customer.this, "Field was deleted", Toast.LENGTH_SHORT).show();
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

    private void sqliteController() {
        new AsyncTask<Void, Void, Integer>() {
            protected Integer doInBackground(Void... params) {
                int currentVersion = getCurrentVersion();
                database = new SQLite_Builder(Activity_Add_Customer.this, currentVersion).getWritableDatabase();
                ContentValues values = new ContentValues();
                if (isRemoved == true) { //remove field from database
                    String create = "CREATE TABLE IF NOT EXISTS temp_table (name TEXT, tel TEXT, mail TEXT)";
                    database.execSQL(create);
                    for (int i = 0; i < tvList.size(); i++) {
                        String[] tv = tvController(tvList);
                        String fn = tv[i];
                        String[] splitName = fn.split(":");
                        String upgrade = "ALTER TABLE temp_table ADD COLUMN " + splitName[0] + " TEXT";
                        database.execSQL(upgrade);
                    }
                    String select = "SELECT * FROM temp_table";
                    String selectMain = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE;
                    Cursor cursor = database.rawQuery(select, null);
                    Cursor cursorMain = database.rawQuery(selectMain, null);
                    if (cursorMain.moveToFirst()) {
                        do {
                            String[] str = cursor.getColumnNames();
                            for (int i = 0; i < str.length; i++) {
                                String title = cursorMain.getString(cursorMain.getColumnIndex(str[i]));
                                values.put(str[i], title);
                            }
                            database.insert("temp_table", null, values);
                        } while (cursorMain.moveToNext());
                        cursor.close();
                        cursorMain.close();
                    }
                    String drop = "DROP TABLE " + SQLite_Builder.MAIN_TABLE;
                    database.execSQL(drop);
                    String rename = "ALTER TABLE temp_table RENAME TO " + SQLite_Builder.MAIN_TABLE;
                    database.execSQL(rename);
                } else { //add field to database
                    String upgradeTable = "ALTER TABLE " + SQLite_Builder.MAIN_TABLE + " ADD COLUMN " + fieldName + " TEXT";
                    database.execSQL(upgradeTable);
                }
                isRemoved = false;
                return currentVersion;
            }

            protected void onPostExecute(Integer currentV) {
                toSharedPreference.edit().putInt("version", currentV).commit();
            }
        }.execute();
    }

    private int getCurrentVersion() {
        int currentVersion = toSharedPreference.getInt("version", 1);
        currentVersion++;
        return currentVersion;
    }

    private class DialogListener implements View.OnClickListener {
        private AlertDialog dialog;

        public DialogListener(AlertDialog alertDialog) {
            this.dialog = alertDialog;
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case addField_button:
                    addNewField();
                    dialog.dismiss();
                    break;

                case deleteButton:
                    removeField();
                    dialog.dismiss();
                    break;

                case cancelButton:
                    dialog.dismiss();
                    break;
            }
        }
    }
}
