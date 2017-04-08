package com.reabyo.crme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Login extends Activity implements View.OnClickListener {

    private EditText userName, password, confirmPasswordEd, securityQed;
    private LinearLayout securityLayout1, securityLayout2, confirmPasswordLayout;
    private TextView forgetP, register;
    private Button login;
    private CheckBox rememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String currentUserName, currentPassword, confirmP, currentQ;
    private String getUserName, getPassword, getQ;
    private boolean isContainData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //*LinearLayout*
        securityLayout1 = (LinearLayout) findViewById(R.id.securityLayout1);
        securityLayout2 = (LinearLayout) findViewById(R.id.securityLayout2);
        confirmPasswordLayout = (LinearLayout) findViewById(R.id.confirmPasswordLayout);

        //*TextView*
        register = (TextView) findViewById(R.id.register);
        forgetP = (TextView) findViewById(R.id.forgetPassword);
        forgetP.setOnClickListener(this);

        //*EditText*
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        securityQed = new EditText(this);
        confirmPasswordEd = new EditText(this);

        //*Button*
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        //*CheckBox*
        rememberMe = (CheckBox) findViewById(R.id.checkBoxSave);
        rememberMe.setTextColor(Color.parseColor("#eef7fc"));

        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isContainData = sharedPreferences.contains("userName");
        if (isContainData == false){
            register.setText("Register:");
            setSecurityView();
        }else register.setText("Login:");

        isRemembered();
    }

    private void setSecurityView() {
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams edParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //*Confirm Password*
        TextView confirmPasswordTv = new TextView(this);
        confirmPasswordTv.setBackgroundColor(Integer.parseInt("464952"));
        confirmPasswordTv.setLayoutParams(tvParams);
        confirmPasswordTv.setText("Confirm Password: ");
        confirmPasswordTv.setTextSize(18);
        confirmPasswordTv.setTypeface(null, Typeface.BOLD);
        confirmPasswordEd.setLayoutParams(edParams);
        confirmPasswordEd.setTextSize(18);
        confirmPasswordEd.setBackground(null);
        confirmPasswordLayout.setBackgroundResource(R.drawable.fields_style);
        confirmPasswordLayout.addView(confirmPasswordTv);
        confirmPasswordLayout.addView(confirmPasswordEd);

        //*Security Question*
        TextView securityT = new TextView(this);
        TextView securityQHint = new TextView(this);
        TextView securityQ = new TextView(this);
        securityT.setText("Security question:   ");
        securityT.setPadding(5, 0, 0, 0);
        securityT.setTextSize(16);
        securityQHint.setHint("(can't be changed)");
        securityQ.setText("Your mother's last name before marriage?");
        securityQ.setTextSize(16);
        securityLayout1.setOrientation(LinearLayout.HORIZONTAL);
        securityLayout1.addView(securityT);
        securityLayout1.addView(securityQHint);
        securityLayout2.addView(securityQ);
        securityLayout2.addView(securityQed);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                currentUserName = userName.getText().toString().trim();
                currentPassword = password.getText().toString().trim();
                confirmP = confirmPasswordEd.getText().toString().trim();
                currentQ = securityQed.getText().toString().trim();
                if (currentUserName.length() > 50 || currentPassword.length() > 50 || currentQ.length() > 50){
                    Toast.makeText(Activity_Login.this, "Input cannot be longer than 50 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean toRemember = rememberMe.isChecked();
                editor.putBoolean("remember", toRemember).commit();

                if (isContainData == false) {
                    if (!currentUserName.equals("") && !currentPassword.equals("") && !confirmP.equals("") && !currentQ.equals("")) {
                        editor.putString("userName", currentUserName);
                        if (currentPassword.equals(confirmP)) {
                            editor.putString("password", currentPassword);
                            editor.putString("confirmP", confirmP);
                        } else {
                            confirmPasswordEd.requestFocus();
                            confirmPasswordEd.setError("Note: You're try to put different password");
                            return;
                        }
                        editor.putString("securityQuestion", currentQ);
                        editor.commit();
                        loginTo();
                    } else {
                        if (currentUserName.equals("")) {
                            userName.requestFocus();
                            userName.setError("Field cannot be empty");
                        } else if (currentPassword.equals("")) {
                            password.requestFocus();
                            password.setError("Field cannot be empty");
                        } else if (confirmP.equals("")) {
                            confirmPasswordEd.requestFocus();
                            confirmPasswordEd.setError("Field cannot be empty");
                        } else if (currentQ.equals("")) {
                            securityQed.requestFocus();
                            securityQed.setError("Field cannot be empty");
                        }
                    }
                } else {
                    boolean isValid = isDataValid(currentUserName, currentPassword);
                    if (isValid == true) loginTo();
                    else return;
                }
                break;
            case R.id.forgetPassword:
                getPassword = sharedPreferences.getString("password", "");
                getQ = sharedPreferences.getString("securityQuestion", "");
                if (!getQ.equals("")) {
                    final AlertDialog questionDialog = new AlertDialog.Builder(this).create();
                    final View dialogView = getLayoutInflater().inflate(R.layout.security_question_dialog, null);
                    final EditText question = (EditText) dialogView.findViewById(R.id.securityQ_dialog);
                    questionDialog.setView(dialogView);
                    questionDialog.show();
                    Button done = (Button) dialogView.findViewById(R.id.questionButton);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (question.getText().toString().equals(getQ)) {
                                password.setText(getPassword);
                                questionDialog.dismiss();
                            } else {
                                question.requestFocus();
                                question.setError("Incorrect answer");
                                question.setText("");
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "First login is required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void isRemembered() {
        getUserName = sharedPreferences.getString("userName", "");
        getPassword = sharedPreferences.getString("password", "");
        boolean rememberCheck = sharedPreferences.getBoolean("remember", rememberMe.isChecked());
        if (rememberCheck == true) {
            loginTo();
        } else {
            userName.setText(getUserName);

        }
    }

    private void loginTo() {
        Intent intentStart = new Intent(Activity_Login.this, Activity_Add_Customer.class);
        startActivity(intentStart);
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
