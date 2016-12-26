package com.example.user.crm_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;

import java.util.ArrayList;

/**
 * Created by user on 11/30/2016.
 */

public class QuickSearch_Ad extends BaseAdapter {

    private Context context;
    private ArrayList<Customer> customersArray;
    private Holder holder;

    public QuickSearch_Ad(Context context, ArrayList customersArray) {
        this.context = context;
        this.customersArray = customersArray;
    }

    @Override
    public int getCount() {
        return customersArray.size();
    }

    @Override
    public Object getItem(int position) {
        return customersArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.quick_search_list_item, parent, false);
        holder = new Holder();
        holder.tvName = (TextView) view.findViewById(R.id.list_name);
        holder.tvTel = (TextView) view.findViewById(R.id.list_tel);
        holder.tvMail = (TextView) view.findViewById(R.id.list_mail);

        Customer customer = customersArray.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvTel.setText(customer.getTel());
        holder.tvMail.setText(customer.getMail());

        if(position == 0 || position%2 == 0) view.setBackgroundResource(R.drawable.row1_style);
        //else view.setBackgroundResource(R.drawable.row2_style);
        return view;
    }

    private class Holder {
        TextView tvName;
        TextView tvTel;
        TextView tvMail;
    }
}
