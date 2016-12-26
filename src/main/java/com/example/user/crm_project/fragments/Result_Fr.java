package com.example.user.crm_project.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;
import com.example.user.crm_project.adapters.AdvanceSearch_Ad;

import java.util.ArrayList;

/**
 * Created by user on 12/3/2016.
 */

public class Result_Fr extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Customer> itemsArray;
    LinearLayout emptyList;
    private ListView listView;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsArray = (ArrayList<Customer>) getArguments().getSerializable("items");
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
        AdvanceSearch_Ad adapter = new AdvanceSearch_Ad(getContext(), itemsArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView pickedTel = (TextView) listView.getChildAt(position).findViewById(R.id.resultList_tel);
        TextView pickedMail = (TextView) listView.getChildAt(position).findViewById(R.id.resultList_mail);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CustomerFile_Fr customerFile = new CustomerFile_Fr();
        bundle = new Bundle();
        String currentTel = pickedTel.getText().toString();
        String currentMail = pickedMail.getText().toString();
        bundle.putString("currentTel", currentTel);
        bundle.putString("currentMail", currentMail);
        customerFile.setArguments(bundle);
        customerFile.show(fragmentManager, "customerFile");
    }
}
