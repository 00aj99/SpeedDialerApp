package com.example.denmlaa.speeddialerapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.denmlaa.speeddialerapp.database.entity.ContactEntity;

@Database(entities = {ContactEntity.class}, version = 2)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();

    private static ContactsDatabase INSTANCE;

    public static ContactsDatabase getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ContactsDatabase.class, "contacts_db")
                    .build();
        }

        return INSTANCE;
    }

}
