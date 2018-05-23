package com.example.denmlaa.speeddialerapp.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.denmlaa.speeddialerapp.database.entity.ContactEntity;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private LiveData<List<ContactEntity>> contacts;
    private ContactsDatabase contactsDatabase;

    public ContactViewModel(@NonNull Application application) {
        super(application);

        contactsDatabase = ContactsDatabase.getINSTANCE(this.getApplication());
        contacts = contactsDatabase.contactDao().getContacts();
    }

    public LiveData<List<ContactEntity>> getContacts() {
        return contacts;
    }

    public void deleteContact(ContactEntity contactEntity) {
        new DeleteAT(contactsDatabase).execute(contactEntity);
    }

    public void addContact(ContactEntity contactEntity) {
        new AddContactAT(contactsDatabase).execute(contactEntity);
    }

    private class DeleteAT extends AsyncTask<ContactEntity, Void, Void> {

        private ContactsDatabase contactsDatabase;

        DeleteAT(ContactsDatabase db) {
            this.contactsDatabase = db;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            contactsDatabase.contactDao().deleteContact(contactEntities[0]);
            return null;
        }
    }

    private class AddContactAT extends AsyncTask<ContactEntity, Void, Void> {

        private ContactsDatabase contactsDatabase;

        AddContactAT(ContactsDatabase db) {
            this.contactsDatabase = db;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            contactsDatabase.contactDao().addContact(contactEntities[0]);
            return null;
        }
    }

}