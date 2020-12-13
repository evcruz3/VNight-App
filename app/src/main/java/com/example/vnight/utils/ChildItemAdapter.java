package com.example.vnight.utils;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vnight.R;
import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.teamDrafter.SlotItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildItemAdapter
        extends RecyclerView
        .Adapter<ChildItemAdapter.ChildViewHolder> {

    private static final String TAG = "ChildItemAdapter";

//    public static final Map<Integer, String> Positions = createMap();



    private List<SlotItem> slotList;

    // NOTE: The static user mapping is updated everytime a new instance of the adapter is made.
    // It this a good design?
    private static HashMap<String,HashMap<String,String>> usersDatabase;


    private ChildItemOnDropListener mChildItemOnDropListener;
    private ChildItemOnDoubleClickListener mChildItemOnDoubleClickListener;
    //private int teamID;

    public interface ChildItemOnDropListener{
        boolean DROP_SUCCESS = true;
        boolean DROP_FAILED = false;

        boolean onDropSlotIsEmpty(HashMap<String, String> pendingPlayer, String slotID);
        boolean onDropSlotIsNotEmpty(HashMap<String, String> previousPlayer, HashMap<String, String> pendingPlayer, String slotID);
    }

    public interface ChildItemOnDoubleClickListener{
        boolean onDoubleClick(HashMap<String,String> playerInfo, int teamID,String slotID );
    }

    public void setChildItemOnDropListener(ChildItemOnDropListener mChildItemOnDropListener){
        this.mChildItemOnDropListener = mChildItemOnDropListener;
    }

    public void setChildItemOnDoubleClickListener(ChildItemOnDoubleClickListener mChildItemOnDoubleClickListener){
        this.mChildItemOnDoubleClickListener = mChildItemOnDoubleClickListener;
    }

    // Constuctor
    ChildItemAdapter(Context context, List<SlotItem> slotList)
    {
        this.slotList = slotList;

        TypeToken<HashMap<String,HashMap<String,String>>> token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        this.usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(context, "UsersDatabase", "users", token.getType());
        //this.teamID = teamID;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup,
            int i)
    {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(
                        R.layout.child_item,
                        viewGroup, false);

        ChildViewHolder holder = new ChildViewHolder(view);

        final SlotItem slot = slotList.get(holder.getAdapterPosition());
        final HashMap<String, String> playerInfo = slot.getPlayerInfo();

        if(mChildItemOnDropListener != null){
            holder.itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    switch (dragEvent.getAction()) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            view.setBackgroundColor(Color.GREEN);
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            view.setBackgroundColor(Color.WHITE);
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            view.setBackgroundColor(Color.WHITE);
                            break;
                        case DragEvent.ACTION_DROP:
                            HashMap<String,String> previousPlayer = slot.getPlayerInfo();
                            String requiredRole = slot.getRole();
                            HashMap<String,String> pendingPlayer = (HashMap<String,String>) dragEvent.getLocalState();

                            if(requiredRole.compareTo(pendingPlayer.get("position")) == 0){
                                if(previousPlayer != null){
                                    if(mChildItemOnDropListener.onDropSlotIsNotEmpty(previousPlayer, pendingPlayer, slot.getId()) == mChildItemOnDropListener.DROP_SUCCESS){
                                        slot.setPlayerInfo(pendingPlayer);
                                    }
                                }else{
                                    if(mChildItemOnDropListener.onDropSlotIsEmpty(pendingPlayer, slot.getId()) == mChildItemOnDropListener.DROP_SUCCESS){
                                        slot.setPlayerInfo(pendingPlayer);
                                    }
                                }
                            }else{
                                // Do Nothing
                            }
                            break;
                        default:
                            break;
                    }


                    //slot.setPlayerInfo((HashMap<String,String>) dragEvent.getLocalState());
                    //return mChildItemOnDropListener.onDrag(view, dragEvent, position);
                    return true;
                }
            });
        }

        if(mChildItemOnDoubleClickListener != null) {
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {

                long prev_clickTime = 0;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    long timestamp = motionEvent.getEventTime();
                    long duration = timestamp - prev_clickTime;
                    //Log.d(TAG, "ActionID: " + String.valueOf(motionEvent.getAction()) + " Duration: " + String.valueOf(duration));
                    if (duration < 500) {
                        Log.d(TAG, "double tapped");
                        if (playerInfo != null) {
                            mChildItemOnDoubleClickListener.onDoubleClick(playerInfo, slot.getTeamID(), slot.getId());
                            slot.setPlayerInfo(null);
                        }

                    }
                    prev_clickTime = timestamp;
                    return false;
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(
            @NonNull final ChildViewHolder childViewHolder,
            final int position)
    {

        // Create an instance of the ChildItem
        // class for the given position
//        final ChildItem childItem
//                = ChildItemList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself

        final SlotItem slot = slotList.get(position);
        final HashMap<String, String> playerInfo = slot.getPlayerInfo();

//        final String positionTitle = Positions.get(position);
//        final String childItem = ChildItemList.getOrDefault(positionTitle, null);

        childViewHolder.ChildItemTitle.setText(slot.getTitle());
        if(playerInfo == null){
            childViewHolder.ChildItemPlayerName.setText(""); // empty position slot
            childViewHolder.itemView.setAlpha(new Float(0.5));
        }
        else{
            //HashMap<String, String> playerInfo = usersDatabase.getOrDefault(childItem, null);
            HashMap<String, String> playerData = usersDatabase.getOrDefault(playerInfo.get("userID"), null);

            if(playerData != null) {
                childViewHolder.ChildItemPlayerName.setText(playerData.get("firstName")); // Registered player
            }
            else{
                childViewHolder.ChildItemPlayerName.setText("unknown user"); // Guest
            }

            childViewHolder.itemView.setAlpha(new Float(1));
        }

        //childViewHolder.itemView.setOnDragListener(this.onDragListener);





    }

    @Override
    public int getItemCount()
    {

        // This method returns the number
        // of items we have added
        // in the ChildItemList
        // i.e. the number of instances
        // of the ChildItemList
        // that have been created
        return slotList.size();
    }

    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class ChildViewHolder
            extends RecyclerView.ViewHolder {

        TextView ChildItemTitle;
        TextView ChildItemPlayerName;
//        Button DeleteEntry;

        ChildViewHolder(View itemView)
        {
            super(itemView);
            ChildItemPlayerName = itemView.findViewById(R.id.child_item_playerName);
            ChildItemTitle
                    = itemView.findViewById(
                    R.id.child_item_title);
//            DeleteEntry = itemView.findViewById(R.id.btn_delete);
        }
    }
}

