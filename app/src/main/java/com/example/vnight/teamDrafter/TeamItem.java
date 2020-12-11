package com.example.vnight.teamDrafter;

import java.util.List;

public class TeamItem {

    // Declaration of the variables 
    private String title;
    private List<SlotItem> SlotItemList;

    // Constructor of the class 
    // to initialize the variables 
    public TeamItem(
            String title,
            List<SlotItem> SlotItemList)
    {

        this.title = title;
        this.SlotItemList = SlotItemList;
    }

    // Getter and Setter methods 
    // for each parameter 
    public String gettitle()
    {
        return title;
    }

    public void settitle(
            String title)
    {
        title = title;
    }

    public List<SlotItem> getSlotItemList()
    {
        return SlotItemList;
    }

    public void setSlotItemList(
            List<SlotItem> SlotItemList)
    {
        SlotItemList = SlotItemList;
    }
}

