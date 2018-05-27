package com.example.denmlaa.speeddialerapp.activities;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import com.example.denmlaa.speeddialerapp.util.widget.ContactWidgetProvider;
import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.database.ContactViewModel;
import com.example.denmlaa.speeddialerapp.database.ContactsDatabase;
import com.example.denmlaa.speeddialerapp.database.entity.ContactEntity;
import com.google.gson.Gson;

import java.util.List;

import static com.example.denmlaa.speeddialerapp.activities.MainActivity.CONTACTS_KEY;

public class ContactPickWidget extends AppCompatActivity {

    private static final int PICK_CONTACT_REQUEST = 1;
    //    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ContactViewModel viewModel;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private List<ContactEntity> contactsFromDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        viewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
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

                int contact_id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String contact_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_photo_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                ContactEntity contact = new ContactEntity(contact_id, contact_name, contact_number, contact_photo_uri);
                viewModel.addContact(contact);

                new GetContactsTask().execute();
                cursor.close();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private class GetContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            contactsFromDb = ContactsDatabase.getINSTANCE(ContactPickWidget.this).contactDao().getAllContactsFromDb();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!contactsFromDb.isEmpty()) {
                Gson gson = new Gson();
                String jsonContacts = gson.toJson(contactsFromDb);

                editor.putString(CONTACTS_KEY, jsonContacts);
                editor.apply();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplication());

                int[] ids = appWidgetManager.getAppWidgetIds(
                        new ComponentName(getApplication(), ContactWidgetProvider.class));

                appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.grid_view);

                finish();
            }
        }

    }

}
