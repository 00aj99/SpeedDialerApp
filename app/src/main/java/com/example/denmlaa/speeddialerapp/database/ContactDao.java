package com.example.denmlaa.speeddialerapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.denmlaa.speeddialerapp.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM Contact")
    LiveData<List<Contact>> getContacts();

    @Query("SELECT * FROM Contact WHERE id = :contact_id")
    Contact getContactById(int contact_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Query("DELETE FROM Contact")
    void deleteAll();

    @Query("SELECT * FROM Contact")
    List<Contact> getAllContactsFromDb();

}
