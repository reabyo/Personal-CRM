package com.example.user.crm_project;

import android.content.Context;
import android.widget.EditText;

/**
 * Created by user on 12/13/2016.
 */

public class Validation {

    private String name;
    private String tel;
    private String mail;
    private Context context;
    private EditText edName, edTel, edMail;

    public Validation(Context context, EditText edName, EditText edTel, EditText edMail) {
        this.context = context;
        this.edName = edName;
        this.edTel = edTel;
        this.edMail = edMail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean checkValidation(){
        if (name.equals("") || tel.equals("")) {
            edName.setError("Field cannot be empty");
            edTel.setError("Field cannot be empty");
            return false;
        }


        if (!name.matches("[a-z A-Z]+")) {
            edName.requestFocus();
            edName.setError("Invalid input");
            return false;
        }

        if (mail != null) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                edMail.requestFocus();
                edMail.setError("Invalid characters");
                return false;
            }
        }

        if (!tel.matches("[0-9]+") || tel.length() != 10 || tel.equals("")) {
            edTel.requestFocus();
            edTel.setError("Invalid input");
            return false;
        }
        return true;
    }
}
