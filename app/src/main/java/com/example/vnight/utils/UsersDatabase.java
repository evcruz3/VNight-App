package com.example.vnight.utils;

import java.util.HashMap;

public final class UsersDatabase {

    private static HashMap<String, HashMap<String,String>> usersDatabase;

    public static synchronized void setUsersDatabase(HashMap<String, HashMap<String,String>> db){
        usersDatabase = db;
    }

    public static HashMap<String, HashMap<String,String>> getUsersDatabase(){
        if(usersDatabase == null){
            throw new IllegalArgumentException("usersDatabase not initialized");
        }
        else{
            return usersDatabase;
        }
    }
}
