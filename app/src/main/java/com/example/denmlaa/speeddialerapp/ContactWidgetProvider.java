package com.example.denmlaa.speeddialerapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.denmlaa.speeddialerapp.activities.MainActivity;

public class ContactWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.contact_widget);
            view.setOnClickPendingIntent(R.id.widget_contact, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }
}
