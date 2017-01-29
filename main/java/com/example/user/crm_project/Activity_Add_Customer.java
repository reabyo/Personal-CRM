package com.example.user.crm_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.crm_project.database.SQLite_Builder;
import com.example.user.crm_project.interfaces.AutoViewBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.crm_project.R.id.addField_button;
import static com.example.user.crm_project.R.id.add_closeX;
import static com.example.user.crm_project.R.id.cancelButton;
import static com.example.user.crm_project.R.id.deleteButton;

public class Activity_Add_Customer extends Activity implements View.OnClickListener, AutoViewBuilder {

    private EditText name, tel, mail, autoEditText;
    private TextView autoTextView, hintMassage;
    private Button save, moveToSearch, moveToCalendar, addFields;
    private LinearLayout dynamicLayout, dynamicAdd;
    private View dialogView, removeDialogView, hideKeyBoard;
    private SharedPreferences toSharedPreference;
    SharedPreferences.Editor editor;
    private int currentNumber, currentId;
    private List<EditText> edList;
    private List<TextView> tvList;
    private boolean isRemoved;
    private SQLiteDatabase database;
    private String fieldName;
    private DisplayMetrics displayMetrics;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        edList = new ArrayList();
        tvList = new ArrayList();

        //*Button*
        save = (Button) findViewById(R.id.add_saveButton);
        save.setOnClickListener(this);
        moveToSearch = (Button) findViewById(R.id.moveToSearch);
        moveToSearch.setOnClickListener(this);
        moveToCalendar = (Button) findViewById(R.id.moveToCalendar);
        moveToCalendar.setOnClickListener(this);
        addFields = (Button) findViewById(R.id.add_addFields);
        addFields.setOnClickListener(this);

        //*EditText*
        name = (EditText) findViewById(R.id.add_name);
        tel = (EditText) findViewById(R.id.add_tel);
        mail = (EditText) findViewById(R.id.add_mail);

        //*TextView*
        hintMassage = (TextView) findViewById(R.id.hintMassage);

        //*LinearLayout*
        dynamicLayout = (LinearLayout) findViewById(R.id.dynamicView);
        displayMetrics = this.getResources().getDisplayMetrics();
        float margin = 7 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int toMargin = (int) margin;
        vParams.setMargins(0, 0, 0, toMargin);

        toSharedPreference = getSharedPreferences("dynamicField", MODE_PRIVATE);
        editor = toSharedPreference.edit();
        autoViewBuilder();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_saveButton:
                final String currentName = name.getText().toString().trim();
                final String currentTel = tel.getText().toString().trim();
                final String currentMail = mail.getText().toString().trim();

                //*Validation check*
                final Validation validation = new Validation(this, name, tel, mail);
                validation.setName(currentName);
                validation.setTel(currentTel);
                validation.setMail(currentMail);
                boolean isValidate = validation.checkMainValidation();
                if (isValidate == false) return;

                //*Add Customer*
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        SQLite_Builder builder = getBuilder();
                        database = builder.getWritableDatabase();
                        String[] tv = tvController(tvList);
                        String[] ed = edController(edList);
                        ContentValues main_values = new ContentValues();

                        //*validation check*
                        validation.setTels(builder.getTel());
                        boolean isValidTel = validation.telValidation();
                        if (isValidTel == false) return "Invalid input - Number is already exists";
                        main_values.put("tel", currentTel);

                        validation.setMails(builder.getMail());
                        boolean isValidMail = validation.mailValidation();
                        if (isValidMail == false) return "Invalid input - Mail is already exists";
                        main_values.put("mail", currentMail);

                        main_values.put("name", currentName);
                        if (tv != null && ed != null) {
                            if (tv.length > 0 && ed.length > 0) {
                                for (int i = 0; i < tv.length; i++) {
                                    String fieldName = tv[i];
                                    String[] splitName = fieldName.split(":");
                                    String nameToFormat = splitName[0].replace(" ", "_");
                                    if (!ed[i].equals("")){
                                        validation.setGeneralValue(ed[i]);
                                        boolean isValid = validation.checkTelAndMail();
                                        if (isValid == false || ed[i].equals(currentTel) || ed[i].equals(currentMail))
                                            return "Invalid input";
                                    }
                                    main_values.put(nameToFormat, ed[i]);
                                }
                            }
                        }
                        //TODO: remove spaces at the end of the string - also in search
//                        String s = "   this has spaces at the beginning and at the end      ";
//                        String result = s.replaceAll("\\s+$", "");
                        database.insert(SQLite_Builder.MAIN_TABLE, null, main_values);
                        return null;
                    }

                    protected void onPostExecute(String massage) {
                        if (massage == null) {
                            name.setText("");
                            tel.setText("");
                            mail.setText("");
                            for (int i = 0; i < edList.size(); i++) {
                                edList.get(i).setText("");
                            }
                            Toast.makeText(Activity_Add_Customer.this, "Saved", Toast.LENGTH_LONG).show();
                            hideKeyBoard = getCurrentFocus();
                            inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(hideKeyBoard.getWindowToken(), 0);
                        } else {
                            if (massage.equals("Invalid input - Number is already exists")) {
                                tel.requestFocus();
                                tel.setError("This number is already exists");
                            } else if (massage.equals("Invalid input - Mail is already exists")) {
                                mail.requestFocus();
                                mail.setError("This mail is already exists");
                            }
                            else if (massage.equals("Invalid input")) {
                                Toast.makeText(Activity_Add_Customer.this,
                                        "Invalid data - Check your input", Toast.LENGTH_LONG).show();
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
        final AlertDialog createDialog = new AlertDialog.Builder(this).create();
        dialogView = getLayoutInflater().inflate(R.layout.add_field_dialog, null);
        createDialog.setView(dialogView);
        dialogView.findViewById(addField_button).setOnClickListener(new DialogListener(createDialog));
        dialogView.findViewById(add_closeX).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog.dismiss();
            }
        });
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
        SQLite_Builder builder = getBuilder();
        int columnsNum = builder.getColumnsNames().length;
        if (columnsNum == 3){
            setHintMassage();
            return;
        }
        for (int i = 0; i < columnsNum - 3; i++) {
            String currentName = builder.getColumnsNames()[i+3];
            autoTextView = new TextView(this);
            autoEditText = new EditText(this);
            autoTextView.setTypeface(null, Typeface.BOLD);
            autoTextView.setTextSize(18);
            autoEditText.setId(i);
            String nameToFormat = currentName.replace("_", " ");
            autoTextView.setText(nameToFormat + ":" + " ");
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
        fieldName = add.getText().toString();
        if (fieldName.length() > 20) {
            Toast.makeText(Activity_Add_Customer.this, "Field name cannot be longer than 20 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fieldName.equals("")){
            Toast.makeText(Activity_Add_Customer.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Validation validateName = new Validation();
        validateName.setCurrentColumn(fieldName);
        SQLite_Builder builder = getBuilder();
        validateName.setColumns(builder.getColumnsNames());
        boolean isContain = validateName.getColumnsValidation();
        if (isContain == false) {
            Toast.makeText(Activity_Add_Customer.this, "This field's name is already in use." + "\n" +
                    "Please choose a different name", Toast.LENGTH_SHORT).show();
            return;
        }
        autoTextView.setText(fieldName + ":" + " ");
        autoTextView.setTypeface(null, Typeface.BOLD);
        autoTextView.setTextSize(18);
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
        hintMassage.setVisibility(View.GONE);
        Toast.makeText(Activity_Add_Customer.this, "Field was added", Toast.LENGTH_SHORT).show();
        hideKeyBoard = getCurrentFocus();
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(hideKeyBoard.getWindowToken(), 0);

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
        currentNumber = toSharedPreference.getInt("fieldNumber", 0);
        autoEditText.setId(currentNumber);
        dynamicAdd.setId(currentNumber);
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
        if (tvList.size() == 0)setHintMassage();
        sqliteController();
        isRemoved = true;
        Toast.makeText(Activity_Add_Customer.this, "Field was deleted", Toast.LENGTH_SHORT).show();
    }

    private void setHintMassage(){
        LinearLayout.LayoutParams hintParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        hintMassage.setText(Html.fromHtml("Click " + "<b>" + "<big>" + "' Add fields '" + "</big>" + "</b>" +
                " to create your own fields"));
        float marginTop = 120 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        int mt = (int) marginTop;
        hintParams.setMargins(0, mt, 0, 0);
        hintMassage.setLayoutParams(hintParams);
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

    private void sqliteController() {
        new AsyncTask<Void, Void, Integer>() {
            protected Integer doInBackground(Void... params) {
                int currentVersion = getCurrentVersion();
                currentVersion++;
                database = new SQLite_Builder(Activity_Add_Customer.this, currentVersion).getWritableDatabase();
                ContentValues values = new ContentValues();
                if (isRemoved == true) { //remove field from database
                    String create = "CREATE TABLE IF NOT EXISTS temp_table (name TEXT, tel TEXT, mail TEXT)";
                    database.execSQL(create);
                    for (int i = 0; i < tvList.size(); i++) {
                        String[] tv = tvController(tvList);
                        String fn = tv[i];
                        String[] splitName = fn.split(":");
                        splitName[0].replace(" ", "_");
                        String upgrade = "ALTER TABLE temp_table ADD COLUMN " + "'" + splitName[0] + "'" + " TEXT";
                        database.execSQL(upgrade);
                    }
                    String select = "SELECT * FROM temp_table";
                    String selectMain = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE;
                    Cursor cursor = database.rawQuery(select, null);
                    Cursor cursorMain = database.rawQuery(selectMain, null);
                    if (cursorMain.moveToFirst()) {
                        do {
                            String[] names = cursor.getColumnNames();
                            for (int i = 0; i < names.length; i++) {
                                String title = cursorMain.getString(cursorMain.getColumnIndex(names[i]));
                                values.put(names[i], title);
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
                } else { //*add field to database*
                    fieldName.replace(" ", "_");
                    String upgradeTable = "ALTER TABLE " + SQLite_Builder.MAIN_TABLE + " ADD COLUMN " + "'"+
                            fieldName +"'" + " TEXT";
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

    public int getCurrentVersion() {
        int currentVersion = toSharedPreference.getInt("version", 1);
        return currentVersion;
    }

    public SQLite_Builder getBuilder(){
        SQLite_Builder sqLite_builder = new SQLite_Builder(Activity_Add_Customer.this, getCurrentVersion());
        return sqLite_builder;
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
