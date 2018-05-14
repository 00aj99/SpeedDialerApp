package com.example.denmlaa.speeddialerapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import static com.example.denmlaa.speeddialerapp.activities.ContactWidgetConfig.CONTACT_NAME_DEF;
import static com.example.denmlaa.speeddialerapp.activities.ContactWidgetConfig.CONTACT_PHONE_DEFF;
import static com.example.denmlaa.speeddialerapp.activities.ContactWidgetConfig.CONTACT_PHOTO_DEFF;
import static com.example.denmlaa.speeddialerapp.activities.ContactWidgetConfig.SHARED_PREFS;

public class ContactWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String contact_name = prefs.getString(CONTACT_NAME_DEF + appWidgetId, "Name");
            String contact_photot_uri = prefs.getString(CONTACT_PHOTO_DEFF + appWidgetId, null);
            String contact_number = prefs.getString(CONTACT_PHONE_DEFF + appWidgetId, null);

            Intent callContactIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_number));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, callContactIntent, 0);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.contact_widget);
            view.setOnClickPendingIntent(R.id.widget_contact, pendingIntent);
            view.setCharSequence(R.id.widget_contact_name, "setText", contact_name);
            if (contact_photot_uri != null) {
                view.setImageViewUri(R.id.widget_contact_photo, Uri.parse(contact_photot_uri));
            }

            appWidgetManager.updateAppWidget(appWidgetId, view);

        }
    }

}
