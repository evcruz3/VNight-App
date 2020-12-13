package com.example.vnight.teamDrafter;

import java.util.ArrayList;
import java.util.List;

public class TeamItem {

    // Declaration of the variables 
    private String title;
    private int teamID;
    private List<SlotItem> SlotItemList;

    // Constructor of the class 
    // to initialize the variables 
    public TeamItem(int teamID,
            String title)
    {
        this.teamID = teamID;
        this.title = title;
        this.SlotItemList = generatePlayerSlots(teamID);
    }

    private List<SlotItem> generatePlayerSlots(int teamID){
        List<SlotItem> slotItemList = new ArrayList<>();

        slotItemList.add(new SlotItem(SlotItem.ID_WING1, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_MID1, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_SETTER, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_WING2, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_MID2, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_WING3, teamID));
        slotItemList.add(new SlotItem(SlotItem.ID_LIBERO, teamID));

        return slotItemList;
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

