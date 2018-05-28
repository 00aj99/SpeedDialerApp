package com.example.denmlaa.speeddialerapp;

import android.app.Application;

import com.example.denmlaa.speeddialerapp.database.ContactsDatabase;

public class App extends Application {

    private ContactsDatabase databaseInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseInstance = ContactsDatabase.getINSTANCE(this);
    }

    public ContactsDatabase getDatabaseInstance() {
        return databaseInstance;
    }
}
