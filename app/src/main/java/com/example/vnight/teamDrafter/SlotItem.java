package com.example.vnight.teamDrafter;


import androidx.annotation.NonNull;

import java.util.HashMap;

public class SlotItem {

    // Declaration of the variable 
    private String title; // "Wing 1" or "Player Name"
    private String id; // "wing1"
    private String role; // "Wing"
    private HashMap<String, String> playerInfo;
        // KEYS: {"entryID", "userID", "position"}
    private int teamID;

    public final static String ID_WING1 = "wing1";
    public final static String ID_WING2 = "wing2";
    public final static String ID_WING3 = "wing3";
    public final static String ID_MID1 = "mid1";
    public final static String ID_MID2 = "mid2";
    public final static String ID_SETTER = "setter";
    public final static String ID_LIBERO = "libero";

    public final static String ROLE_WING = "Wing";
    public final static String ROLE_MID = "Mid";
    public final static String ROLE_SETTER = "Setter";
    public final static String ROLE_LIBERO = "Libero";


    // Constructor of the class 
    // to initialize the variable* 
    public SlotItem(@NonNull String id, @NonNull int teamID)
    {
        switch(id){
            case ID_WING1:
                this.title = "Wing 1";
                this.id = id;
                this.role = ROLE_WING;
                break;
            case ID_WING2:
                this.title = "Wing 2";
                this.id = id;
                this.role = ROLE_WING;
                break;
            case ID_WING3:
                this.title = "Wing 3";
                this.id = id;
                this.role = ROLE_WING;
                break;
            case ID_MID1:
                this.title = "Mid 1";
                this.id = id;
                this.role = ROLE_MID;
                break;
            case ID_MID2:
                this.title = "Mid 2";
                this.id = id;
                this.role = ROLE_MID;
                break;
            case ID_SETTER:
                this.title = "Setter";
                this.id = id;
                this.role = ROLE_SETTER;
                break;
            case ID_LIBERO:
                this.title = "Libero";
                this.id = id;
                this.role = ROLE_LIBERO;
                break;
            default:
                throw new IllegalArgumentException("ID " + id + " not recognized");
        }
        this.playerInfo = null;
        this.teamID = teamID;
    }

    // Getter and Setter method 
    // for the parameter 
    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(
            String newTitle)
    {
        this.title = newTitle;
    }

    public String getId(){
        return this.id;
    }

    public int getTeamID(){
        return this.teamID;
    }

    public void setPlayerInfo(HashMap<String, String> playerInfo){
        this.playerInfo = playerInfo;
    }

    public HashMap<String,String> getPlayerInfo(){
        return this.playerInfo;
    }

    public String getRole(){
        return this.role;
    }
} 

