package com.example.vnight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.vnight.utils.ChildItemAdapter;
import com.example.vnight.utils.ParentItemAdapter;
import com.example.vnight.utils.ReservedPlayersAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamDrafterActivity
        extends AppCompatActivity
        implements View.OnClickListener{

    Button btnCreateNewTeam;
    //List<ParentItem> itemList;
    ParentItemAdapter parentItemAdapter;
    RecyclerView ParentRecyclerViewItem;
    ArrayList<HashMap<String, String>> reservedPlayers;
    RecyclerView rvReservedPlayers;
    ReservedPlayersAdapter reservedPlayersAdapter;
    ArrayList<HashMap<String,String>> teamComposition;
    Context ctx;
    int currTeamIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_drafter_actvitiy);
        ctx = this;
        currTeamIndex = 0;

        btnCreateNewTeam = findViewById(R.id.btn_addNewTeam);
        btnCreateNewTeam.setOnClickListener(this);

        //itemList is list of teams
//        itemList = new ArrayList<>();
//        addNewParentItem(itemList.size() + 1);

        teamComposition = new ArrayList<HashMap<String,String>>();

        reservedPlayers = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("reservedPlayers");


        rvReservedPlayers = (RecyclerView)findViewById(R.id.gv_reservedPlayers);
        reservedPlayersAdapter = new ReservedPlayersAdapter(this, reservedPlayers);
        rvReservedPlayers.setAdapter(reservedPlayersAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvReservedPlayers.setLayoutManager(linearLayoutManager);


        ParentRecyclerViewItem = findViewById(
                R.id.parent_recyclerview);

        // Initialise the Linear layout manager
        LinearLayoutManager
                layoutManager
                = new LinearLayoutManager(
                TeamDrafterActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SnapHelper snapHelper = new PagerSnapHelper();

//        View.OnDragListener childOnDragListener = new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View view, DragEvent dragEvent) {
//                switch (dragEvent.getAction()) {
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        view.setBackgroundColor(Color.GREEN);
//                        break;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        view.setBackgroundColor(Color.WHITE);
//                        break;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        view.setBackgroundColor(Color.WHITE);
//                        break;
//                    case DragEvent.ACTION_DROP:
//                        //  final float dropX = dragEvent.getX();
//                        //  final float dropY = dragEvent.getY();
//                        DragData data = (DragData) dragEvent.getLocalState();
//                        String requiredPosition = childItem.getRequiredPosition();
//
//                        if(requiredPosition != null) {
//                            if(requiredPosition.compareTo(data.getPlayerPosition()) == 0){
//                                childItem.setPlayerInfo(data.getUserInfo());
//                                childItem.setChildItemTitle(data.getUserInfo().getFirstName());
//                                childViewHolder.ChildItemTitle.setText(data.getUserInfo().getFirstName());
//                                childViewHolder.itemView.setAlpha(new Float(1));
//                            }
//                            else{
////                                Toast.makeText(this, "Position mismatched", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
//                        else{
//                            childItem.setChildItemTitle(data.getUserInfo().getFirstName());
//                            childViewHolder.ChildItemTitle.setText(data.getUserInfo().getFirstName());
//                        }
//
//
//
//
//                    default:
//                        break;
//                }
//                return true;
//            }
//        };

        // Pass the arguments
        // to the parentItemAdapter.
        // These arguments are passed
        // using a method ParentItemList()
        ChildItemAdapter.ChildItemOnDragListener childOnDragListener = new ChildItemAdapter.ChildItemOnDragListener() {

            @Override
            public boolean onDrag(View view, DragEvent dragEvent, int position) {
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
//                            mChildItemOnDragListener.onDragDropped(view, position);
                            HashMap<String,String> player = (HashMap<String,String>) dragEvent.getLocalState();
                            String requiredPosition = ChildItemAdapter.Positions.get(position);

                            switch(requiredPosition){
                                case "wing1":
                                case "wing2":
                                case "wing3":
                                    if(player.get("position").compareTo("Wing") == 0){
                                        teamComposition.get(currTeamIndex).put(requiredPosition, player.get("userID"));
                                        parentItemAdapter.notifyDataSetChanged();
                                        reservedPlayers.remove(player);
                                        reservedPlayersAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case "mid1":
                                case "mid2":
                                    if(player.get("position").compareTo("Mid") == 0){
                                        teamComposition.get(currTeamIndex).put(requiredPosition, player.get("userID"));
                                        parentItemAdapter.notifyDataSetChanged();
                                        reservedPlayers.remove(player);
                                        reservedPlayersAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case "setter":
                                    if(player.get("position").compareTo("Setter") == 0){
                                        teamComposition.get(currTeamIndex).put(requiredPosition, player.get("userID"));
                                        parentItemAdapter.notifyDataSetChanged();
                                        reservedPlayers.remove(player);
                                        reservedPlayersAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case "other":
                                    teamComposition.get(currTeamIndex).put(requiredPosition, player.get("userID"));
                                    parentItemAdapter.notifyDataSetChanged();
                                    reservedPlayers.remove(player);
                                    reservedPlayersAdapter.notifyDataSetChanged();
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                return true;
            }
        };

        ParentItemAdapter.ParentItemOnDragListener parentItemOnDragListener = new ParentItemAdapter.ParentItemOnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent, int position) {
                currTeamIndex = position;
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.YELLOW);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        view.setBackgroundColor(Color.WHITE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        view.setBackgroundColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
        parentItemAdapter = new ParentItemAdapter(ctx, parentItemOnDragListener, childOnDragListener, teamComposition);

        // Set the layout manager
        // and adapter for items
        // of the parent recyclerview
        ParentRecyclerViewItem
                .setAdapter(parentItemAdapter);
        ParentRecyclerViewItem
                .setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(ParentRecyclerViewItem);

    }

    @Override
    public void onClick(View view) {
        if(view == btnCreateNewTeam){
            addNewParentItem(teamComposition.size());
            parentItemAdapter.notifyDataSetChanged();
            ParentRecyclerViewItem.scrollToPosition(parentItemAdapter.getItemCount() - 1);
        }
    }

    private void addNewParentItem(int id){
//        ParentItem item = new ParentItem("Team "+String.valueOf(id), ChildItemList(id));
//        itemList.add(item);

        teamComposition.add(ChildItemList(id));
    }

    // Method to pass the arguments
    // for the elements
    // of child RecyclerView
    private HashMap<String,String> ChildItemList(int id)
    {
        HashMap<String,String> ChildItemList
                = new HashMap<String,String>();

        //ChildItemList.put("teamID", String.valueOf(id));
        ChildItemList.put("wing1", null);
        ChildItemList.put("mid1", null);
        ChildItemList.put("setter", null);
        ChildItemList.put("wing2", null);
        ChildItemList.put("mid2", null);
        ChildItemList.put("wing3", null);
        ChildItemList.put("other", null);


        return ChildItemList;
    }
}
