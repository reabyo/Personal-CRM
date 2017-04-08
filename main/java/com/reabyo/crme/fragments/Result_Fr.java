package com.reabyo.crme.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reabyo.crme.Customer;
import com.reabyo.crme.R;
import com.reabyo.crme.adapters.Result_Ad;

import java.util.ArrayList;

/**
 * Created by user on 12/3/2016.
 */

public class Result_Fr extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<Customer> itemsArray;
    private ArrayList<String> columns, allMail;
    private LinearLayout emptyList;
    private ListView listView;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsArray = (ArrayList<Customer>) getArguments().getSerializable("items"); //*all results*
        columns = (ArrayList<String>) getArguments().getSerializable("columnsNames"); //*column's names*
        allMail = (ArrayList<String>) getArguments().getSerializable("allMails"); //*all mails*
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_list, container, false);

        if (itemsArray.isEmpty()){
            emptyList = (LinearLayout) view.findViewById(R.id.emptyList);
            TextView textView = new TextView(getActivity());
            textView.setHint("No results for this search");
            textView.setTextSize(20);
            emptyList.addView(textView);
        }
        listView = (ListView) view.findViewById(R.id.advanceSearch_list);
        Result_Ad adapter = new Result_Ad(getContext(), itemsArray);
        adapter.setColumns(columns);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Button mailToAll = (Button) view.findViewById(R.id.mailToAll);
        mailToAll.setOnClickListener(this);
        Button back = (Button) view.findViewById(R.id.result_closeX);
        back.setOnClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Customer currentCustomer = (Customer) parent.getAdapter().getItem(position);
        String currentTel = currentCustomer.getTel();
        String currentMail = currentCustomer.getMail();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CustomerFile_Fr customerFile = new CustomerFile_Fr();
        bundle = new Bundle();
        bundle.putString("currentTel", currentTel);
        bundle.putString("currentMail", currentMail);
        customerFile.setArguments(bundle);
        customerFile.show(fragmentManager, "customerFile");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mailToAll:
                sendToAll();
                break;
            case R.id.result_closeX:
                getActivity().onBackPressed();
                break;
        }
    }

    private void sendToAll(){
        final AlertDialog mailDialog = new AlertDialog.Builder(getActivity()).create();
        View mailView = getActivity().getLayoutInflater().inflate(R.layout.mail_message, null);
        final EditText mailTitle = (EditText) mailView.findViewById(R.id.mailTitle);
        final EditText mailText = (EditText) mailView.findViewById(R.id.mailText);
        final Button sendMail = (Button) mailView.findViewById(R.id.sendMailButton);
        Button back = (Button) mailView.findViewById(R.id.mail_closeX);
        back.setOnClickListener(new View.OnClickListener() {
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
                    String[] mails = allMail.toArray(new String[allMail.size()]);
                    toSendMail.putExtra(Intent.EXTRA_EMAIL, mails);
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
    }
}
