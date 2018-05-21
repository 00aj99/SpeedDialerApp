package com.example.denmlaa.speeddialerapp.activities;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.denmlaa.speeddialerapp.R;
import com.example.denmlaa.speeddialerapp.adapter.ContactsRVAdapter;
import com.example.denmlaa.speeddialerapp.database.ContactViewModel;
import com.example.denmlaa.speeddialerapp.database.ContactsDatabase;
import com.example.denmlaa.speeddialerapp.model.Contact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private List<Contact> contacts;
    private ContactsRVAdapter adapter;
    private ProgressBar progressBar;
    private ContactViewModel viewModel;
    private List<Contact> contactsFromDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        // Checking for permissions
        checkForPermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        viewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        viewModel.getContacts().observe(MainActivity.this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable List<Contact> contacts) {
                // TODO Set this in widget class so that when contact is removed from db, it is removed from widget also
//                Toast.makeText(MainActivity.this, "List size: " + contacts.size(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        List<Contact> newList = new ArrayList<>();
        for (Contact contact : contacts) {
            String contact_name = contact.getContactName().toLowerCase();
            if (contact_name.contains(newText)) {
                newList.add(contact);
            }
        }

        adapter.setFilter(newList);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup_contacts:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    break;
                } else {
                    new BackupContacts().execute();
                }
                break;
            case R.id.retore_contacts:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                } else {
                    final Intent intent = new Intent();

                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String tmptype = mime.getMimeTypeFromExtension("vcf");
                    final File file = new File(Environment.getExternalStorageDirectory().toString() + "/Contacts.vcf");

                    if (file.exists()) {
                        intent.setDataAndType(Uri.fromFile(file), tmptype);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Export contacts
    public void getVCF() {
        final String vfile = "Contacts.vcf";

        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        if (phones != null && phones.getCount() > 0) {
            phones.moveToFirst();
        }

        for (int i = 0; i < phones.getCount(); i++) {

            String lookupKey = phones.getString(phones
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);

            AssetFileDescriptor fd;
            try {
                fd = getContentResolver().openAssetFileDescriptor(uri,
                        "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                String VCard = new String(buf);
                String path = Environment.getExternalStorageDirectory()
                        .toString() + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(path,
                        true);
                mFileOutputStream.write(VCard.toString().getBytes());
                phones.moveToNext();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    // Export contacts
    private class BackupContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getVCF();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Contacts exported", Toast.LENGTH_SHORT).show();
        }
    }

    // Get contacts from phone
    private List<Contact> getContacts() {
        contacts = new ArrayList<>();

        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        while (cursor != null && cursor.moveToNext()) {
            contacts.add(new Contact(
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
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
            progressBar.setVisibility(View.VISIBLE);
            new GetContactsTask().execute();
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
                    progressBar.setVisibility(View.VISIBLE);
                    new GetContactsTask().execute();
                } else {
                    // If permissions are not granted, another dialog is shown and application shuts down
                    AlertDialog.Builder warning_msg = new AlertDialog.Builder(this)
                            .setMessage("In order to use this application, please turn on permissions")
                            .setCancelable(false)
                            .setIcon(R.drawable.warning_dialog_icon)
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
                break;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permissions are granted, contacts are exported
                    new BackupContacts().execute();
                } else {
                    final AlertDialog.Builder warning_msg = new AlertDialog.Builder(this)
                            .setMessage("In order to export contacts, please turn on permissions")
                            .setCancelable(true)
                            .setIcon(R.drawable.warning_dialog_icon)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                }
                            });
                    AlertDialog alert = warning_msg.create();
                    alert.setTitle("Enable permissions");
                    alert.show();
                }
                break;
            }
            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permissions are granted, contacts are imported
                    final Intent intent = new Intent();

                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String tmptype = mime.getMimeTypeFromExtension("vcf");
                    final File file = new File(Environment.getExternalStorageDirectory().toString() + "/Contacts.vcf");

                    if (file.exists()) {
                        intent.setDataAndType(Uri.fromFile(file), tmptype);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final AlertDialog.Builder warning_msg = new AlertDialog.Builder(this)
                            .setMessage("In order to export contacts, please turn on permission")
                            .setCancelable(true)
                            .setIcon(R.drawable.warning_dialog_icon)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                }
                            });
                    AlertDialog alert = warning_msg.create();
                    alert.setTitle("Enable permissions");
                    alert.show();
                }
                break;
            }
        }
    }

    private class GetContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            contacts = getContacts();
            contactsFromDb = ContactsDatabase.getINSTANCE(MainActivity.this).contactDao().getAllContactsFromDb();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new ContactsRVAdapter(MainActivity.this, contacts, MainActivity.this, contactsFromDb);
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        Contact contact = (Contact) v.getTag();
        ImageView fav = v.findViewById(R.id.contact_favorites);

        if (fav.getDrawable().getConstantState().equals(this.getDrawable(R.drawable.star_white_border).getConstantState())) {
            fav.setImageResource(R.drawable.ic_star_yellow_24dp);
            // Contact is added to database (favorites)
            viewModel.addContact(contact);
        } else {
            fav.setImageResource(R.drawable.star_white_border);
            // Contact is removed from database (favorites)
            viewModel.deleteContact(contact);
        }
    }
}
