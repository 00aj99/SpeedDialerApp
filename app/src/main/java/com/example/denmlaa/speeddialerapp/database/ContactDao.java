package com.example.denmlaa.speeddialerapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.denmlaa.speeddialerapp.database.entity.ContactEntity;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM ContactEntity")
    LiveData<List<ContactEntity>> getContacts();

    @Query("SELECT * FROM ContactEntity WHERE id = :contact_id")
    ContactEntity getContactById(int contact_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContact(ContactEntity contactEntity);

    @Delete
    void deleteContact(ContactEntity contactEntity);

    @Query("DELETE FROM ContactEntity")
    void deleteAll();

    @Query("SELECT * FROM ContactEntity")
    List<ContactEntity> getAllContactsFromDb();

}
