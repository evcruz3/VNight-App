package com.example.vnight.customClasses;

public class DragData {
    private UserInfo userInfo;
    private String position;

    public DragData(UserInfo userInfo, String position){
        this.userInfo = userInfo;
        this.position = position;
    }

    public UserInfo getUserInfo(){
        return this.userInfo;
    }

    public String getPlayerPosition(){
        return this.position;
    }
}
