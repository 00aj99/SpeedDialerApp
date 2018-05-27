package com.example.denmlaa.speeddialerapp.util.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.activities.MainActivity;
import com.example.denmlaa.speeddialerapp.database.entity.ContactEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.denmlaa.speeddialerapp.util.widget.ContactWidgetProvider.EXTRA_NUMBER;

public class GridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<ContactEntity> contacts;
    private Context mContext;
    private int mAppWidgetId;


    public GridRemoteViewFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        getContacts();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        getContacts();
    }

    @Override
    public void onDestroy() {
        contacts.clear();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.contact_widget_item);

        rv.setTextViewText(R.id.widget_contact_name, contacts.get(position).getContactName());
        if (contacts.get(position).getContactImage() != null) {
            rv.setImageViewUri(R.id.widget_contact_photo, Uri.parse(contacts.get(position).getContactImage()));
        } else {
            rv.setImageViewResource(R.id.widget_contact_photo, R.drawable.contact_default);
        }

        Bundle extras = new Bundle();
        extras.putString(EXTRA_NUMBER, contacts.get(position).getContactNumber());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.contact_widget_item, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void getContacts() {
        Gson gson = new Gson();
        contacts = new ArrayList<>();
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPrefs.getString(MainActivity.CONTACTS_KEY, "");

        Type type = new TypeToken<List<ContactEntity>>() {
        }.getType();
        contacts = gson.fromJson(jsonPreferences, type);
    }
}
