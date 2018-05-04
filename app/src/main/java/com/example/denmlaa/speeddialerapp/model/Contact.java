package com.example.denmlaa.speeddialerapp.model;

public class Contact {

    private int id;
    private String contactName;
    private String contactNumber;
    private int contactImage;

    public Contact(int id, String contactName, String contactNumber, int contactImage) {
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
    }

    public Contact(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
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

    public int getContactImage() {
        return contactImage;
    }

    public void setContactImage(int contactImage) {
        this.contactImage = contactImage;
    }
}
