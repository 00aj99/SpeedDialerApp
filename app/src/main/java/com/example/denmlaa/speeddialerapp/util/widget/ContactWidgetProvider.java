package com.example.denmlaa.speeddialerapp.util.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.activities.ContactPickWidget;

public class ContactWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_NUMBER = "com.example.denmlaa.speeddialerapp.EXTRA_NUMBER";
    public static final String CALL_ACTION = "com.example.denmlaa.speeddialerapp.CALL_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(CALL_ACTION)) {
            String contact_number = intent.getStringExtra(EXTRA_NUMBER);
            Uri number = Uri.parse("tel:" + contact_number);
            Intent callIntent = new Intent(Intent.ACTION_CALL, number);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Application is missing permission to call", Toast.LENGTH_SHORT).show();
                return;
            }
            context.startActivity(callIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, GridWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.contact_widget);
            rv.setRemoteAdapter(R.id.grid_view, intent);
            rv.setEmptyView(R.id.grid_view, R.id.empty_view);

            // Call contact on view click
            Intent callIntent = new Intent(context, ContactWidgetProvider.class);
            callIntent.setAction(ContactWidgetProvider.CALL_ACTION);
            callIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent callPendingIntent = PendingIntent.getBroadcast(context, 0, callIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.grid_view, callPendingIntent);

            // Pick contact from phone app
            Intent pickContactIntent = new Intent(context, ContactPickWidget.class);
            PendingIntent pickContactPendingIntent = PendingIntent.getActivity(context, 0, pickContactIntent, 0);
            rv.setOnClickPendingIntent(R.id.add_new_contact, pickContactPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }


}
