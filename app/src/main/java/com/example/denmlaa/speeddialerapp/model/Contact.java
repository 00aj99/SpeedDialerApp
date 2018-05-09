package com.example.denmlaa.speeddialerapp.model;

import java.io.Serializable;

public class Contact implements Serializable {

    private int id;
    private String contactName;
    private String contactNumber;
    private String contactImage;

    public Contact(String contactName, String contactNumber, String contactImage) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }
}
