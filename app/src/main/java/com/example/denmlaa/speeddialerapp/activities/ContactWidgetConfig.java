package com.example.denmlaa.speeddialerapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;

public class ContactWidgetConfig extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 1;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String SHARED_PREFS = "com.example.denmlaa.speeddialerapp.activities";
    public static final String CONTACT_NAME_DEF = "Name";
    public static final String CONTACT_PHOTO_DEFF = "Photo";
    public static final String CONTACT_PHONE_DEFF = "Number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent configIntet = getIntent();
        Bundle extras = configIntet.getExtras();

        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resValue = new Intent();
        resValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        pickContact();
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();

                Cursor cursor = getContentResolver()
                        .query(contactUri, null, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }

                String contact_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_photo_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                configuration(contact_name, contact_number, contact_photo_uri);
            }
        }
    }

    public void configuration(String name, String number, String photo_uri) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Intent callContactIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, callContactIntent, 0);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.contact_widget);
        views.setOnClickPendingIntent(R.id.widget_contact, pendingIntent);
        views.setCharSequence(R.id.widget_contact_name, "setText", name);
        if (photo_uri != null) {
            views.setImageViewUri(R.id.widget_contact_photo, Uri.parse(photo_uri));
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CONTACT_NAME_DEF + appWidgetId, name);
        if (photo_uri != null) {
            editor.putString(CONTACT_PHOTO_DEFF + appWidgetId, photo_uri);
        }
        editor.putString(CONTACT_PHONE_DEFF + appWidgetId, number);
        editor.apply();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();

    }


}
