package com.example.denmlaa.speeddialerapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.adapter.ContactsRVAdapter;
import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking for permissions
        checkForPermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Backup contacts", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                Toast.makeText(this, "Restore contacts", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        while (cursor != null && cursor.moveToNext()) {
            contacts.add(new Contact(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))));
        }

        cursor.close();
        return contacts;
    }

    private void checkForPermissions() {
        // Simple check for permissions
        // Passing requestCode = 1 for all permissions and String[] with permissions.
        final int PERMISSION_ALL = 1;
        final String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            // Simple permission description is shown before we start permission dialog
            AlertDialog.Builder warning_msg = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Allow Speed Dialer to access your contacts and manage calls. This will allow you to add contacts and make quick calls")
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // On confirm we request permissions
                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                    });
            AlertDialog alert = warning_msg.create();
            alert.setTitle("Permissions");
            alert.show();

        } else {
            // If permissions are granted, contacts are loaded
            new GetContactsTask().execute((Void[]) null);
        }

    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // If permissions are granted, contacts are loaded
                    new GetContactsTask().execute((Void[]) null);
                } else {
                    // If permissions are not granted, another dialog is shown and application shuts down
                    AlertDialog.Builder warning_msg = new AlertDialog.Builder(this)
                            .setMessage("In order to use this application, please turn on permissions")
                            .setCancelable(false)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog alert = warning_msg.create();
                    alert.setTitle("Enable permissions");
                    alert.show();
                }
            }
        }
    }

    private class GetContactsTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pd;
        private List<Contact> contacts;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this, "Loading contacts...", "Please Wait", true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contacts = getContacts();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            ContactsRVAdapter adapter = new ContactsRVAdapter(MainActivity.this, contacts);
            recyclerView.setAdapter(adapter);
        }
    }

    // TODO ProgressBar
    // TODO Backup/Export contacts for share
}
