package com.example.user.crm_project;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.user.crm_project.adapters.Calendar_Ad;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Activity_Calendar extends FragmentActivity implements EasyPermissions.PermissionCallbacks {

    private GoogleAccountCredential mCredential;

    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    private CaldroidListener listener;
    private CaldroidFragment caldroidFragment;
    private Calendar cal;
    private CalendarView calendarView;
    private FragmentTransaction transaction;
    private ArrayList<DateTime> arrDates;

    private Button moveToCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__calendar);

        moveToCalendar = (Button) findViewById(R.id.moveToCal);

        cal = Calendar.getInstance();
        calendarView = (CalendarView) findViewById(R.id.calendar);
        caldroidFragment = new CaldroidFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, caldroidFragment);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();
    }

    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    public void onPermissionsGranted(int requestCode, List<String> list) {
    }

    public void onPermissionsDenied(int requestCode, List<String> list) {
    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Activity_Calendar.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public void moveToCalendar(View view) {
        Intent toCalendarIntent = new Intent(Intent.ACTION_INSERT);
        toCalendarIntent.setType("vnd.android.cursor.item/event");
        startActivity(toCalendarIntent);
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<Event>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google InnerCalendar API Android CRM_Project")
                    .build();
        }

        protected List<Event> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<Event> getDataFromApi() throws IOException {
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list("primary")
                    .setMaxResults(60)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            return items;
        }

        @Override
        protected void onPostExecute(List<Event> jsonItems) {
            arrDates = new ArrayList<>();
            for (Event event : jsonItems) {
                DateTime dateTime = event.getStart().getDateTime();
                if (dateTime == null) dateTime = event.getStart().getDate();
                arrDates.add(dateTime);
            }

            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            try {
                markDates(month, year, arrDates);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendarEvents(jsonItems, arrDates);

            transaction.commit();
            moveToCalendar.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.VISIBLE);
        }

        private void markDates(int month, int year, ArrayList<DateTime> dates) throws ParseException {
            for (int i = 0; i < dates.size(); i++) {
                String datesToMark = dates.get(i).toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(datesToMark);
                String currentDates = dateFormat.format(date);
                for (int j = 0; j < 60; j++) {
                    try {
                        cal.set(Calendar.DAY_OF_MONTH, j + 1);
                        String newDate = year + "-" + month + "-" + cal.get(Calendar.DAY_OF_MONTH);
                        Date dateToParse = dateFormat.parse(newDate);
                        String dateToFormat = dateFormat.format(dateToParse);
                        if (currentDates.equals(dateToFormat)) {
                            caldroidFragment.setBackgroundDrawableForDate(getDrawable(R.drawable.calendar_selected_date),
                                    date);
                            caldroidFragment.refreshView();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void showEvents(ArrayList<InnerCalendar> items) {
            ListView calendarList = (ListView) findViewById(R.id.calendarList);
            Calendar_Ad adapter = new Calendar_Ad(Activity_Calendar.this.getBaseContext(), items);
            calendarList.setAdapter(adapter);
        }

        private void calendarEvents(final List<Event> jsonItems, final ArrayList<DateTime> dates) {
            listener = new CaldroidListener() {
                @Override
                public void onSelectDate(Date date, View view) {
                    ArrayList<InnerCalendar> calendarItems = new ArrayList<>();
                    for (Event event : jsonItems) {
                        String summary = event.getSummary();
                        DateTime dateTime = event.getStart().getDateTime();
                        if (dateTime == null) dateTime = event.getStart().getDate();
                        DateTime start = event.getStart().getDateTime();
                        if (start == null) start = event.getStart().getDate();
                        //Log.d("myStart" , start.toString());
                        DateTime end = event.getEnd().getDateTime();
                        if (end == null) end = event.getEnd().getDate();
                        //Log.d("myEnd" , end.toString());
                        String location = event.getLocation();
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String dateTimeToFormat = dateTime.toString();
                            Date dateToParse = dateFormat.parse(dateTimeToFormat);
                            String currentDateTime = dateFormat.format(dateToParse);
                            String currentDate = dateFormat.format(date);
                            if (currentDate.equals(currentDateTime)) {
                                calendarItems.add(new InnerCalendar(summary, currentDateTime, start,
                                        end, location));
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    showEvents(calendarItems);
                }

                @Override
                public void onChangeMonth(int month, int year) {
                    try {
                        markDates(month, year, dates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };
            caldroidFragment.setCaldroidListener(listener);

        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Activity_Calendar.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }
}

