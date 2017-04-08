package com.reabyo.crme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.reabyo.crme.R;

import java.util.ArrayList;

/**
 * Created by user on 12/9/2016.
 */

public class CustomerFile_Ad extends BaseAdapter {

    private Context context;
    private ArrayList<String> results, titles;
    private Holder holder;

    public CustomerFile_Ad(Context context, ArrayList<String> results, ArrayList<String> titles) {
        this.context = context;
        this.results = results;
        this.titles = titles;
    }


    public int getCount() {
        return results.size();
    }

    public Object getItem(int position) {
        return results.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.customer_file_list_item, parent, false);
            holder = new Holder();
            holder.tv = (TextView) view.findViewById(R.id.tv_list);
            holder.ed = (EditText) view.findViewById(R.id.ed_list);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        String currentFieldName;
        if (titles.get(position).contains("_")) currentFieldName = titles.get(position).replace("_", " ");
        else currentFieldName = titles.get(position);
        holder.tv.setText(currentFieldName + ": ");
        holder.ed.setText(results.get(position));
        holder.ed.setFocusable(false);
        return view;
    }

    private class Holder{
        TextView tv;
        EditText ed;
    }
}
