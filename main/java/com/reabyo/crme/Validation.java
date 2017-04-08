package com.reabyo.crme;

import android.content.Context;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by user on 12/13/2016.
 */

public class Validation {

    private String name, tel, mail, currentColumn, generalValue;
    private Context context;
    private EditText edName, edTel, edMail;
    private ArrayList<String> tels, mails;
    private String[] columns;

    public Validation(Context context, EditText edName, EditText edTel, EditText edMail) {
        this.context = context;
        this.edName = edName;
        this.edTel = edTel;
        this.edMail = edMail;
    }

    public Validation() {}

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

    public void setGeneralValue(String generalValue) {
        this.generalValue = generalValue;
    }

    public void setTels(ArrayList<String> tels) {
        this.tels = tels;
    }

    public void setMails(ArrayList<String> mails) {
        this.mails = mails;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public String getCurrentColumn() {return currentColumn;}

    public void setCurrentColumn(String currentColumn) {this.currentColumn = currentColumn;}

    public EditText getEdMail() {return edMail;}

    public void setEdMail(EditText edMail) {this.edMail = edMail;}

    public boolean checkMainValidation() {
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

        if (!mail.equals("")) {
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

    public boolean telValidation() {
        for (int i = 0; i < tels.size(); i++) {
            if (tels.get(i).equals(tel)) return false;
        }
        return true;
    }

    public boolean mailValidation() {
        for (int i = 0; i < mails.size(); i++) {
            if (!mails.get(i).equals("")) {
                if (mails.get(i).equals(mail)) return false;
            }
        }
        return true;
    }

    public boolean getColumnsValidation() {
        for (int i = 0; i < columns.length; i++) {
            String checkLowerCase = currentColumn.toLowerCase();
            if (columns[i].equals(currentColumn) || columns[i].equals(checkLowerCase)) return false;
        }
        return true;
    }

    public boolean checkTelAndMail(){
        for (int i = 0; i < tels.size(); i++) {
            if (tels.get(i).equals(generalValue) || mails.get(i).equals(generalValue)) return false;
        }
        return true;
    }

    public boolean checkMail(){
        if (!mail.equals("")) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                edMail.requestFocus();
                edMail.setError("Invalid mail");
                return false;
            }
        }
        return true;
    }

    public boolean checkDynamicField(String field){
        if (!field.matches("[a-z A-Z]+")) return false;
        return true;
    }
}
