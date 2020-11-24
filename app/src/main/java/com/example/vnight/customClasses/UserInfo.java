package com.example.vnight.customClasses;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private int entryID;
    private String firstName, lastName, contactNum, username;
    private int batch;

    public UserInfo (int entryID, String firstName, String lastName, int batch, String contactNum, String username){
        this.entryID = entryID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.batch = batch;
        this.contactNum = contactNum;
        this.username = username;
    }

    public int getEntryID(){
        return this.entryID;
    }

    public int getBatch() {
        return this.batch;
    }
    public void setBatch(int batch) {
        this.batch = batch;
    }

    public String getFirstName(){
        return this.firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getContactNum(){
        return this.contactNum;
    }
    public  void setContactNum(String contactNum){
        this.contactNum = contactNum;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
}
