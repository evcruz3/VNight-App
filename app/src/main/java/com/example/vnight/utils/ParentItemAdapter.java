package com.example.vnight.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vnight.R;
import com.example.vnight.teamDrafter.TeamItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParentItemAdapter
        extends RecyclerView
        .Adapter<ParentItemAdapter.ParentViewHolder> {

    Context context;
    ChildItemAdapter.ChildItemOnDropListener childItemOnDropListener;
    ChildItemAdapter.ChildItemOnDoubleClickListener mChildItemOnDoubleClickListener;
    ParentItemOnDragListener parentItemOnDragListener;
    List<TeamItem> teamList;

    public interface ParentItemOnDragListener{
        boolean onDrag(View view, DragEvent dragEvent, int position);
    }

    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private RecyclerView.RecycledViewPool
            viewPool
            = new RecyclerView
            .RecycledViewPool();
    //private List<ParentItem> itemList;
    //private ArrayList<HashMap<String,String>> itemList;

    public ParentItemAdapter(Context context, List<TeamItem> teamList)
    {
//        this.itemList = itemList;
        this.teamList = teamList;
        this.context = context;
    }

    public void setChildItemOnDropListener(ChildItemAdapter.ChildItemOnDropListener childItemOnDropListener){
        this.childItemOnDropListener = childItemOnDropListener;
    }

    public void setChildItemOnDoubleClickListener(ChildItemAdapter.ChildItemOnDoubleClickListener mChildItemOnDoubleClickListener){
        this.mChildItemOnDoubleClickListener = mChildItemOnDoubleClickListener;
    }

    public void setParentItemOnDragListener(ParentItemOnDragListener parentItemOnDragListener){
        this.parentItemOnDragListener = parentItemOnDragListener;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup,
            int i)
    {

        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(
                        R.layout.parent_item,
                        viewGroup, false);

        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ParentViewHolder parentViewHolder,
            final int position)
    {

        // Create an instance of the ParentItem
        // class for the given position
//        ParentItem parentItem = itemList.get(position);

//        HashMap<String,String> parentItem = itemList.get(position);
        TeamItem team = teamList.get(position);

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
//        parentViewHolder
//                .ParentItemTitle
//                .setText(parentItem.getParentItemTitle());

        parentViewHolder.ParentItemTitle.setText(team.gettitle());

        // Create a layout manager
        // to assign a layout
        // to the RecyclerView.

        // Here we have assigned the layout
        // as LinearLayout with vertical orientation
        GridLayoutManager layoutManager = new GridLayoutManager(parentViewHolder.ChildRecyclerView.getContext(), 3);
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(
//                parentViewHolder
//                        .ChildRecyclerView
//                        .getContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false);

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
//        layoutManager
//            .setInitialPrefetchItemCount(
//                    parentItem
//                            .getChildItemList()
//                            .size());

        layoutManager.setInitialPrefetchItemCount(team.getSlotItemList().size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
//        ChildItemAdapter childItemAdapter
//                = new ChildItemAdapter(
//                parentItem
//                        .getChildItemList());

        //parentItem.remove("teamID");
        ChildItemAdapter childItemAdapter = new ChildItemAdapter(context,team.getSlotItemList());
        if(childItemOnDropListener != null) {
            childItemAdapter.setChildItemOnDropListener(childItemOnDropListener);
        }
        if(mChildItemOnDoubleClickListener != null) {
            childItemAdapter.setChildItemOnDoubleClickListener(mChildItemOnDoubleClickListener);
        }
        parentViewHolder
                .ChildRecyclerView
                .setLayoutManager(layoutManager);
        parentViewHolder
                .ChildRecyclerView
                .setAdapter(childItemAdapter);
        parentViewHolder
                .ChildRecyclerView
                .setRecycledViewPool(viewPool);

        if(parentItemOnDragListener != null) {
            parentViewHolder.itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    return parentItemOnDragListener.onDrag(view, dragEvent, position);
                }
            });
        }
    }

    // This method returns the number
    // of items we have added in the
    // ParentItemList i.e. the number
    // of instances we have created
    // of the ParentItemList
    @Override
    public int getItemCount()
    {

        return teamList.size();
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class ParentViewHolder
            extends RecyclerView.ViewHolder {

        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView)
        {
            super(itemView);

            ParentItemTitle
                    = itemView
                    .findViewById(
                            R.id.parent_item_title);
            ChildRecyclerView
                    = itemView
                    .findViewById(
                            R.id.child_recyclerview);
        }
    }
}

