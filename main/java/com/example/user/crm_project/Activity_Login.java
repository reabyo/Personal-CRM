package com.example.user.crm_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Login extends Activity implements View.OnClickListener {

    private EditText userName, password, confirmPassword, mail;
    private TextView forgetP;
    private Button login;
    private CheckBox rememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String currentUserName, currentPassword, confirmP, currentMail;
    private String getUserName, getPassword, getConfirm, getMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //*TextView*
        forgetP = (TextView) findViewById(R.id.forgetPassword);
        forgetP.setOnClickListener(this);

        //*EditText*
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        mail = (EditText) findViewById(R.id.mail);

        //*Button*
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        //*CheckBox*
        rememberMe = (CheckBox) findViewById(R.id.checkBoxSave);

        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isRemembered();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                currentUserName = userName.getText().toString().trim();
                currentPassword = password.getText().toString().trim();
                confirmP = confirmPassword.getText().toString().trim();
                currentMail = mail.getText().toString().trim();
                boolean toRemember = rememberMe.isChecked();
                editor.putBoolean("remember", toRemember);
                if (!currentUserName.equals("") && !currentPassword.equals("") && !confirmP.equals("") && !currentMail.equals("")) {
                    boolean isContain = sharedPreferences.contains("userName");
                    if (isContain == false) {
                        editor.putString("userName", currentUserName);
                        if (currentPassword.equals(confirmP)) {
                            editor.putString("password", currentPassword);
                            editor.putString("confirmP", confirmP);
                        } else {
                            confirmPassword.requestFocus();
                            confirmPassword.setError("Note: You're try to put different password");
                            return;
                        }
                        Validation validation = new Validation();
                        validation.setMail(currentMail);
                        validation.setEdMail(mail);
                        boolean isValidMail = validation.checkMail();
                        if (isValidMail == true) editor.putString("mail", currentMail);
                        else return;
                    }else {
                        boolean isValid = isDataValid(currentUserName, currentPassword);
                        if (isValid == true) loginTo();
                        else return;
                    }
                    editor.commit();
                    loginTo();
                } else if (currentUserName.equals("") || currentPassword.equals("") || confirmP.equals("") || currentMail.equals("")) {
                    if (currentUserName.equals("")) {
                        userName.requestFocus();
                        userName.setError("Field cannot be empty");
                    } else if (currentPassword.equals("")) {
                        password.requestFocus();
                        password.setError("Field cannot be empty");
                    } else if (confirmP.equals("")) {
                        confirmPassword.requestFocus();
                        confirmPassword.setError("Field cannot be empty");
                    } else if (currentMail.equals("")) {
                        mail.requestFocus();
                        mail.setError("Field cannot be empty");
                    }
                }
                break;
            case R.id.forgetPassword:
                getPassword = sharedPreferences.getString("password", "");
                getMail = sharedPreferences.getString("mail", "");
                if (!getMail.equals("")) {
                    String title = "CRMe app - Your password";
                    String text = "Your password is " + "'" + getPassword + "'";
                    Intent toSendMail = new Intent(Intent.ACTION_SEND);
                    toSendMail.setType("message/text/plain");
                    toSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{getMail});
                    toSendMail.putExtra(Intent.EXTRA_SUBJECT, title);
                    toSendMail.putExtra(Intent.EXTRA_TEXT, text);
                    try {
                        startActivity(Intent.createChooser(toSendMail, "Send mail..."));
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(this, "First login is required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void isRemembered() {
        getUserName = sharedPreferences.getString("userName", "");
        getPassword = sharedPreferences.getString("password", "");
        getConfirm = sharedPreferences.getString("confirmP", "");
        getMail = sharedPreferences.getString("mail", "");
        boolean rememberCheck = sharedPreferences.getBoolean("remember", rememberMe.isChecked());
        if (rememberCheck == true) {
            loginTo();
        } else {
            userName.setText(getUserName);
            confirmPassword.setText(getConfirm);
            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mail.setText(getMail);
        }
    }

    private void loginTo() {
        Intent intentStart = new Intent(Activity_Login.this, Activity_Add_Customer.class);
        startActivity(intentStart);
        finish();
    }

    private boolean isDataValid(String name, String pass) {
        if (getUserName.equals(name) && getPassword.equals(pass)) return true;
        else if (!getUserName.equals(name) || !getPassword.equals(pass)) {
            final AlertDialog loginDialog = new AlertDialog.Builder(this).create();
            final View dialogView = getLayoutInflater().inflate(R.layout.login_validation_dialog, null);
            TextView alertMassage = (TextView) dialogView.findViewById(R.id.tvError);
            alertMassage.setTextColor(Color.RED);
            alertMassage.setText(Html.fromHtml("One or more details are incorrect. <br/> Please try again"));
            Button alert = (Button) dialogView.findViewById(R.id.loginAlert);
            alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginDialog.dismiss();
                }
            });
            loginDialog.setView(dialogView);
            loginDialog.show();
            return false;
        }
        return false;
    }
}
