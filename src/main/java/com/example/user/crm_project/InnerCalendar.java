package com.example.user.crm_project;

import com.google.api.client.util.DateTime;

/**
 * Created by user on 4/16/2016.
 */
public class InnerCalendar {

    private String summary;
    private String date;
    private DateTime start;
    private DateTime end;
    private String location;

    public InnerCalendar(String summary, String date, DateTime start, DateTime end, String location) {
        this.summary = summary;
        this.date = date;
        this.start = start;
        this.end = end;
        this.location = location;
    }

    public String getSummery() {
        return summary;
    }

    public void setSummery(String summery) {
        this.summary = summery;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
