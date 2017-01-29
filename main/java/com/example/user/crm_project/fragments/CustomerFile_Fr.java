package com.example.user.crm_project.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.crm_project.Activity_Update_Customer;
import com.example.user.crm_project.R;
import com.example.user.crm_project.adapters.CustomerFile_Ad;
import com.example.user.crm_project.database.SQLite_Builder;
import com.example.user.crm_project.interfaces.AutoViewBuilder;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 12/9/2016.
 */

public class CustomerFile_Fr extends DialogFragment implements AutoViewBuilder, View.OnClickListener {

    private String currentTel, currentMail;
    private SharedPreferences toSharedPreference;
    private SQLiteDatabase database;
    private ListView listView;
    private Button back, update, delete, call, sms, mail;
    Wrapper wrapper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toSharedPreference = getActivity().getSharedPreferences("dynamicField", MODE_PRIVATE);
        currentTel = getArguments().getString("currentTel");
        currentMail = getArguments().getString("currentMail");
        int currentVersion = toSharedPreference.getInt("version", 1);
        database = new SQLite_Builder(getActivity(), currentVersion).getWritableDatabase();
        wrapper = new Wrapper();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_file, container, false);
        listView = (ListView) view.findViewById(R.id.customerFile_detailsList);
        autoViewBuilder();
        back = (Button) view.findViewById(R.id.customerFile_backButton);
        back.setOnClickListener(this);
        update = (Button) view.findViewById(R.id.customerFile_updateButton);
        update.setOnClickListener(this);
        delete = (Button) view.findViewById(R.id.customerFile_deleteButton);
        delete.setOnClickListener(this);
        call = (Button) view.findViewById(R.id.customerFile_callButton);
        call.setOnClickListener(this);
        sms = (Button) view.findViewById(R.id.customerFile_smsButton);
        sms.setOnClickListener(this);
        mail = (Button) view.findViewById(R.id.customerFile_mailButton);
        mail.setOnClickListener(this);
        return view;
    }

    public void autoViewBuilder() {
        new AsyncTask<Void, Void, Wrapper>() {
            protected Wrapper doInBackground(Void... params) {
                String select = "SELECT * FROM " + SQLite_Builder.MAIN_TABLE + " WHERE tel=" + "'" + currentTel + "'";
                Cursor cursor = database.rawQuery(select, null);
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String result = cursor.getString(i);
                        String title = cursor.getColumnName(i);
                        wrapper.results.add(result);
                        wrapper.titles.add(title);
                    }
                }
                return wrapper;
            }

            protected void onPostExecute(Wrapper detailsArrays) {
                CustomerFile_Ad adapter = new CustomerFile_Ad(getContext(), detailsArrays.results, detailsArrays.titles);
                listView.setAdapter(adapter);
            }
        }.execute();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.customerFile_updateButton:
                Intent toUpdate = new Intent(getActivity(), Activity_Update_Customer.class);
                toUpdate.putExtra("results", wrapper.results);
                toUpdate.putExtra("titles", wrapper.titles);
                startActivity(toUpdate);
                break;
            case R.id.customerFile_deleteButton:
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.delete_dialog, null);
                dialog.setView(dialogView);
                dialog.show();

                Button delete = (Button) dialogView.findViewById(R.id.deleteButton);
                Button cancel = (Button) dialogView.findViewById(R.id.cancelButton);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                database.delete(SQLite_Builder.MAIN_TABLE, "tel=?", new String[]{currentTel});
                                return null;
                            }
                        }.execute();
                        dialog.dismiss();
                        ViewPager_Fr vp = new ViewPager_Fr();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.mainContainer, vp);
                        transaction.commit();
                        Fragment fr = getActivity().getSupportFragmentManager().findFragmentByTag("customerFile");
                        DialogFragment df = (DialogFragment) fr;
                        transaction.addToBackStack(null);
                        df.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.customerFile_backButton:
                Fragment fr = getActivity().getSupportFragmentManager().findFragmentByTag("customerFile");
                DialogFragment df = (DialogFragment) fr;
                fr.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
                df.dismiss();
                break;
            case R.id.customerFile_callButton:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + currentTel));
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                } else {
                    try {
                        getActivity().startActivity(callIntent);
                    } catch (Exception e) {
                    }

                }
                break;
            case R.id.customerFile_smsButton:
                final AlertDialog smsDialog = new AlertDialog.Builder(getActivity()).create();
                View smsView = getActivity().getLayoutInflater().inflate(R.layout.sms_message, null);
                final EditText smsText = (EditText) smsView.findViewById(R.id.smsText);
                Button send = (Button) smsView.findViewById(R.id.sendButton);
                smsView.findViewById(R.id.sms_closeX).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        smsDialog.dismiss();
                    }
                });
                smsDialog.setView(smsView);
                smsDialog.show();

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = smsText.getText().toString();
                        if (!message.equals("")) {
                            Uri uri = Uri.parse("sms:" + currentTel);

                            Intent intent = new Intent();
                            intent.setData(uri);
                            intent.putExtra(Intent.EXTRA_TEXT, message);
                            intent.putExtra("sms_body", message);
                            intent.putExtra("address", currentTel);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                intent.setAction(Intent.ACTION_SENDTO);
                                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity());
                                if (defaultSmsPackageName != null) {
                                    intent.setPackage(defaultSmsPackageName);
                                }
                            } else {
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setType("vnd.android-dir/mms-sms");
                            }

                            try {
                                getActivity().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        smsDialog.dismiss();
                    }
                });
                break;
            case R.id.customerFile_mailButton:
                final AlertDialog mailDialog = new AlertDialog.Builder(getActivity()).create();
                View mailView = getActivity().getLayoutInflater().inflate(R.layout.mail_message, null);
                final EditText mailTitle = (EditText) mailView.findViewById(R.id.mailTitle);
                final EditText mailText = (EditText) mailView.findViewById(R.id.mailText);
                final Button sendMail = (Button) mailView.findViewById(R.id.sendMailButton);
                mailView.findViewById(R.id.mail_closeX).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mailDialog.dismiss();
                    }
                });
                mailDialog.setView(mailView);
                mailDialog.show();

                sendMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = mailTitle.getText().toString();
                        String text = mailText.getText().toString();
                        if (!mailText.equals("")) {
                            Intent toSendMail = new Intent(Intent.ACTION_SEND);
                            toSendMail.setType("message/text/plain");
                            toSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{currentMail});
                            toSendMail.putExtra(Intent.EXTRA_SUBJECT, title);
                            toSendMail.putExtra(Intent.EXTRA_TEXT, text);
                            try {
                                startActivity(Intent.createChooser(toSendMail, "Send mail..."));
                            } catch (Exception e) {
                            }
                        } else {
                            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mailDialog.dismiss();
                    }
                });
                break;
        }
    }

    private class Wrapper {
        private ArrayList<String> results = new ArrayList();
        private ArrayList<String> titles = new ArrayList();
    }

    public int getCurrentVersion() {
        return 0;
    }

    public SQLite_Builder getBuilder() {
        return null;
    }
}
