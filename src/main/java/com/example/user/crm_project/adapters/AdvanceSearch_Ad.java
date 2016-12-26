package com.example.user.crm_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;

import java.util.ArrayList;

/**
 * Created by user on 12/3/2016.
 */

public class AdvanceSearch_Ad extends BaseAdapter {

    private Context context;
    private ArrayList<Customer> itemsArray;
    private Holder holder;

    public AdvanceSearch_Ad(Context context, ArrayList itemsArray) {
        this.context = context;
        this.itemsArray = itemsArray;
    }

    @Override
    public int getCount() {
        return itemsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.result_list_item, parent, false);
        holder = new Holder();
        holder.name = (TextView) view.findViewById(R.id.resultList_name);
        holder.tel = (TextView) view.findViewById(R.id.resultList_tel);
        holder.mail = (TextView) view.findViewById(R.id.resultList_mail);
        holder.dynamic1 = (TextView) view.findViewById(R.id.resultList_dynamic1);
        holder.dynamic2 = (TextView) view.findViewById(R.id.resultList_dynamic2);
        holder.dynamic3 = (TextView) view.findViewById(R.id.resultList_dynamic3);
        holder.dynamic4 = (TextView) view.findViewById(R.id.resultList_dynamic4);
        holder.dynamic5 = (TextView) view.findViewById(R.id.resultList_dynamic5);
        Customer customer = itemsArray.get(position);
        holder.name.setText(customer.getName());
        holder.tel.setText(customer.getTel());
        holder.mail.setText(customer.getMail());
        holder.dynamic1.setText(customer.getDynamic1());
        holder.dynamic2.setText(customer.getDynamic2());
        holder.dynamic3.setText(customer.getDynamic3());
        holder.dynamic4.setText(customer.getDynamic4());
        holder.dynamic5.setText(customer.getDynamic5());
        holder.dynamic2.setBackgroundColor(0 * 12434877); //#BDBDBD
        holder.dynamic3.setBackgroundColor(0 * 12434877);
        holder.dynamic4.setBackgroundColor(0 * 12434877);
        holder.dynamic5.setBackgroundColor(0 * 12434877);

        return view;
    }

    private class Holder {
        TextView name, tel, mail, dynamic1, dynamic2, dynamic3, dynamic4, dynamic5;
    }
}
