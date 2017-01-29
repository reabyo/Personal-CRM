package com.example.user.crm_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.crm_project.InnerCalendar;
import com.example.user.crm_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 12/25/2016.
 */

public class Calendar_Ad extends BaseAdapter {

    private Context context;
    private ArrayList<InnerCalendar> items;
    private Holder holder;

    public Calendar_Ad(Context context, ArrayList items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.calendar_list_item, parent, false);
        holder = new Holder();
        holder.summary = (TextView) view.findViewById(R.id.calendarSummary);
        holder.start = (TextView) view.findViewById(R.id.calendarStart);
        holder.end = (TextView) view.findViewById(R.id.calendarEnd);
        holder.location = (TextView) view.findViewById(R.id.calendarLocation);
        InnerCalendar innerCalendar = items.get(position);
        if (innerCalendar.getSummery() != null) holder.summary.setText("Subject: " + innerCalendar.getSummery());
        else holder.summary.setText("Subject: ");
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
        if (innerCalendar.getStart() != null){
            try {
                String timeStart = innerCalendar.getStart().toString();
                Date sToParse = oldFormat.parse(timeStart);
                String currentStart = newFormat.format(sToParse);
                holder.start.setText("From: " + currentStart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (innerCalendar.getEnd() != null){
            try {
                String timeEnd = innerCalendar.getEnd().toString();
                Date eToParse = oldFormat.parse(timeEnd);
                String currentEnd = newFormat.format(eToParse);
                holder.end.setText("To: " + currentEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (innerCalendar.getLocation() != null) holder.location.setText("Location: " + innerCalendar.getLocation());
        else holder.location.setText("Location: ");
        return view;
    }

    private class Holder{
        TextView summary, start, end, location;
    }
}
