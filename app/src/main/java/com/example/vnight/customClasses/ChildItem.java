package com.example.vnight.customClasses;

public class ChildItem {

    // Declaration of the variable
    private String ChildItemTitle;
    private String position;
    private UserInfo playerInfo;

    // Constructor of the class
    // to initialize the variable*
    public ChildItem(String childItemTitle, String position)
    {
        this.ChildItemTitle = childItemTitle;
        this.position = position;
        this.playerInfo = null;
    }

    // Getter and Setter method
    // for the parameter
    public String getChildItemTitle()
    {
        return ChildItemTitle;
    }

    public void setChildItemTitle(
            String childItemTitle)
    {
        ChildItemTitle = childItemTitle;
    }

    public void setPlayerInfo(UserInfo playerInfo){
        this.playerInfo = playerInfo;
    }

    public String getRequiredPosition(){
        return this.position;
    }

    public UserInfo getPlayerInfo(){
        return this.playerInfo;
    }
} 
