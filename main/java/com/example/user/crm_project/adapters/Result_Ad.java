package com.example.user.crm_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.crm_project.Customer;
import com.example.user.crm_project.R;

import java.util.ArrayList;

/**
 * Created by user on 12/3/2016.
 */

public class Result_Ad extends BaseAdapter {

    private Context context;
    private ArrayList<Customer> itemsArray;
    private Holder holder;
    private ArrayList<String> columns;

    public Result_Ad(Context context, ArrayList itemsArray) {
        this.context = context;
        this.itemsArray = itemsArray;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
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
        holder.name.setText("name: " + customer.getName());
        holder.tel.setText("tel: " + customer.getTel());
        holder.mail.setText("mail: " + customer.getMail());

        if (columns.size() >= 1 && !customer.getDynamic1().equals(customer.getName())){
            holder.dynamic1.setText(columns.get(0) + ": " + customer.getDynamic1());
            if (!customer.getDynamic1().equals(""))
                holder.dynamic1.setVisibility(View.VISIBLE);
        }

        if (columns.size() >= 2){
            holder.dynamic2.setText(columns.get(1) + ": " + customer.getDynamic2());
            if (!customer.getDynamic2().equals(""))
                holder.dynamic2.setVisibility(View.VISIBLE);
        }

        if (columns.size() >= 3){
            holder.dynamic3.setText(columns.get(2) + ": " + customer.getDynamic3());
            if (!customer.getDynamic3().equals(""))
                holder.dynamic3.setVisibility(View.VISIBLE);
        }

        if (columns.size() >= 4){
            holder.dynamic4.setText(columns.get(3) + ": " + customer.getDynamic4());
            if (!customer.getDynamic4().equals(""))
                holder.dynamic4.setVisibility(View.VISIBLE);
        }

        if (columns.size() == 5){
            holder.dynamic5.setText(columns.get(4) + ": " + customer.getDynamic5());
            if (!customer.getDynamic5().equals(""))
                holder.dynamic5.setVisibility(View.VISIBLE);
        }

        if (position == 0 || position % 2 == 0) view.setBackgroundResource(R.drawable.row1_style);
        else if (position % 2 == 1 ) view.setBackgroundResource(R.drawable.row2_style);

        return view;
    }

    private class Holder {
        TextView name, tel, mail, dynamic1, dynamic2, dynamic3, dynamic4, dynamic5;
    }


}
