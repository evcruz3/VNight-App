package com.example.vnight.utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vnight.R;
import com.example.vnight.customClasses.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class ChildItemAdapter
        extends RecyclerView
        .Adapter<ChildItemAdapter.ChildViewHolder> {

    //private List<ChildItem> ChildItemList;
    public static HashMap<Integer, String> Positions;
    private HashMap<String, String> ChildItemList;
    private final HashMap<String,HashMap<String,String>> usersDatabase;
    private ChildItemOnDragListener mChildItemOnDragListener;

    public interface ChildItemOnDragListener{
        boolean onDrag(View view, DragEvent dragEvent, int position);
    }
    // Constuctor
    ChildItemAdapter(Context context, ChildItemOnDragListener mChildItemOnDragListener, HashMap<String, String> childItemList)
    {
        this.ChildItemList = childItemList;
        Positions = new HashMap<Integer, String>();
        Positions.put(0, "wing1");
        Positions.put(1, "mid1");
        Positions.put(2, "setter");
        Positions.put(3, "wing2");
        Positions.put(4, "mid2");
        Positions.put(5, "wing3");
        Positions.put(6, "other");
        TypeToken<HashMap<String,HashMap<String,String>>> token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        this.usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(context, "UsersDatabase", "users", token.getType());
        this.mChildItemOnDragListener = mChildItemOnDragListener;
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

        return new ChildViewHolder(view);
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

        final String positionTitle = Positions.get(position);
        final String childItem = ChildItemList.getOrDefault(positionTitle, null);

        if(childItem == null){
            childViewHolder.ChildItemTitle.setText(positionTitle); // empty position slot
            childViewHolder.itemView.setAlpha(new Float(0.5));
        }
        else{
            HashMap<String, String> playerInfo = usersDatabase.getOrDefault(childItem, null);
            UserInfo playerData;

            if(playerInfo != null) {
                childViewHolder.ChildItemTitle.setText(playerInfo.get("firstName")); // Registered player
            }
            else{
                childViewHolder.ChildItemTitle.setText(childItem); // Guest
            }

            childViewHolder.itemView.setAlpha(new Float(1));
        }

        //childViewHolder.itemView.setOnDragListener(this.onDragListener);
        childViewHolder.itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return mChildItemOnDragListener.onDrag(view, dragEvent, position);
            }
        });
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
        return ChildItemList.size();
    }

    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class ChildViewHolder
            extends RecyclerView.ViewHolder {

        TextView ChildItemTitle;

        ChildViewHolder(View itemView)
        {
            super(itemView);
            ChildItemTitle
                    = itemView.findViewById(
                    R.id.child_item_title);
        }
    }
}

