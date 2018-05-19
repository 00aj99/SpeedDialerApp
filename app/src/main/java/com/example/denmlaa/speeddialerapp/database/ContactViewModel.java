package com.example.denmlaa.speeddialerapp.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private LiveData<List<Contact>> contacts;
    private ContactsDatabase contactsDatabase;

    public ContactViewModel(@NonNull Application application) {
        super(application);

        contactsDatabase = ContactsDatabase.getINSTANCE(this.getApplication());
        contacts = contactsDatabase.contactDao().getContacts();
    }

    public LiveData<List<Contact>> getContacts() {
        return contacts;
    }

    public void deleteContact(Contact contact) {
        new DeleteAT(contactsDatabase).execute(contact);
    }

    public void addContact(Contact contact) {
        new AddContactAT(contactsDatabase).execute(contact);
    }

    // TODO Only for test purposes
    public void deleteAll() {
        new DeleteAllAT(contactsDatabase).execute();
    }

    private class DeleteAT extends AsyncTask<Contact, Void, Void> {

        private ContactsDatabase contactsDatabase;

        DeleteAT(ContactsDatabase db) {
            this.contactsDatabase = db;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsDatabase.contactDao().deleteContact(contacts[0]);
            return null;
        }
    }

    private class AddContactAT extends AsyncTask<Contact, Void, Void> {

        private ContactsDatabase contactsDatabase;

        AddContactAT(ContactsDatabase db) {
            this.contactsDatabase = db;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsDatabase.contactDao().addContact(contacts[0]);
            return null;
        }
    }

    // TODO Only for test purposes
    private class DeleteAllAT extends AsyncTask<Void, Void, Void> {

        private ContactsDatabase contactsDatabase;

        DeleteAllAT(ContactsDatabase db) {
            this.contactsDatabase = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactsDatabase.contactDao().deleteAll();
            return null;
        }
    }

}