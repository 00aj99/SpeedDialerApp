package com.example.denmlaa.speeddialerapp.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.example.denmlaa.speeddialerapp.model.Contact;

@Entity
public class ContactEntity implements Contact {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "contact_name")
    private String contactName;

    @ColumnInfo(name = "contact_number")
    private String contactNumber;

    @ColumnInfo(name = "contact_image")
    @Nullable
    private String contactImage;

    public ContactEntity(int id, String contactName, String contactNumber, @Nullable String contactImage) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    @Ignore
    public ContactEntity(String contactName, String contactNumber, @Nullable String contactImage) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    @Nullable
    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(@Nullable String contactImage) {
        this.contactImage = contactImage;
    }


}
