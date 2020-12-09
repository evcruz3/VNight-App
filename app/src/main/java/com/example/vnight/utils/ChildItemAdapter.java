package com.example.vnight.utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vnight.R;
import com.example.vnight.customClasses.ChildItem;
import com.example.vnight.customClasses.DragData;
import com.example.vnight.customClasses.UserInfo;

import java.util.HashMap;
import java.util.List;

public class ChildItemAdapter
        extends RecyclerView
        .Adapter<ChildItemAdapter.ChildViewHolder> {

    private List<ChildItem> ChildItemList;

    // Constuctor
    ChildItemAdapter(List<ChildItem> childItemList)
    {
        this.ChildItemList = childItemList;
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
            int position)
    {

        // Create an instance of the ChildItem
        // class for the given position
        final ChildItem childItem
                = ChildItemList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself
        childViewHolder
                .ChildItemTitle
                .setText(childItem.getChildItemTitle());

        if(childItem.getPlayerInfo() == null) {
            childViewHolder.itemView.setAlpha(new Float(0.5));
        }
        else{
            childViewHolder.itemView.setAlpha(new Float(1));
        }

        childViewHolder.itemView.setOnDragListener(new View.OnDragListener() {
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
                        //  final float dropX = dragEvent.getX();
                        //  final float dropY = dragEvent.getY();
                        DragData data = (DragData) dragEvent.getLocalState();
                        String requiredPosition = childItem.getRequiredPosition();

                        if(requiredPosition != null) {
                            if(requiredPosition.compareTo(data.getPlayerPosition()) == 0){
                                childItem.setPlayerInfo(data.getUserInfo());
                                childItem.setChildItemTitle(data.getUserInfo().getFirstName());
                                childViewHolder.ChildItemTitle.setText(data.getUserInfo().getFirstName());
                                childViewHolder.itemView.setAlpha(new Float(1));
                            }
                            else{
//                                Toast.makeText(this, "Position mismatched", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        else{
                            childItem.setChildItemTitle(data.getUserInfo().getFirstName());
                            childViewHolder.ChildItemTitle.setText(data.getUserInfo().getFirstName());
                        }

                    default:
                        break;
                }
                return true;
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

