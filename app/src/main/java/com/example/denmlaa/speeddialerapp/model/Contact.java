package com.example.denmlaa.speeddialerapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

@Entity
public class Contact {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "contact_name")
    private String contactName;

    @ColumnInfo(name = "contact_number")
    private String contactNumber;

    @ColumnInfo(name = "contact_image")
    @Nullable
    private String contactImage;

    public Contact(long id, String contactName, String contactNumber, @Nullable String contactImage) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    @Ignore
    public Contact(String contactName, String contactNumber, @Nullable String contactImage) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Nullable
    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(@Nullable String contactImage) {
        this.contactImage = contactImage;
    }
}
