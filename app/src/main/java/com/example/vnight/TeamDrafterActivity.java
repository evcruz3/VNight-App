package com.example.vnight;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.vnight.customClasses.ChildItem;
import com.example.vnight.customClasses.ParentItem;
import com.example.vnight.utils.ParentItemAdapter;
import com.example.vnight.utils.ReservedPlayersAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamDrafterActivity
        extends AppCompatActivity
        implements View.OnClickListener{

    Button btnCreateNewTeam;
    List<ParentItem> itemList;
    ParentItemAdapter parentItemAdapter;
    RecyclerView ParentRecyclerViewItem;
    ArrayList<HashMap<String, String>> reservedPlayers;
    RecyclerView rvReservedPlayers;
    ReservedPlayersAdapter reservedPlayersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_drafter_actvitiy);

        btnCreateNewTeam = findViewById(R.id.btn_addNewTeam);
        btnCreateNewTeam.setOnClickListener(this);
        itemList = new ArrayList<>();
        addNewParentItem(itemList.size() + 1);
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

        // Pass the arguments
        // to the parentItemAdapter.
        // These arguments are passed
        // using a method ParentItemList()
        parentItemAdapter = new ParentItemAdapter(
                itemList);

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
            addNewParentItem(itemList.size() + 1);
            parentItemAdapter.notifyDataSetChanged();
            ParentRecyclerViewItem.scrollToPosition(parentItemAdapter.getItemCount() - 1);
        }
    }

    private void addNewParentItem(int id){
        ParentItem item = new ParentItem("Team "+String.valueOf(id), ChildItemList());
        itemList.add(item);
    }

    // Method to pass the arguments
    // for the elements
    // of child RecyclerView
    private List<ChildItem> ChildItemList()
    {
        List<ChildItem> ChildItemList
                = new ArrayList<>();

        ChildItemList.add(new ChildItem("Wing 1", "Wing"));
        ChildItemList.add(new ChildItem("Mid 1", "Mid"));
        ChildItemList.add(new ChildItem("Setter", "Setter"));
        ChildItemList.add(new ChildItem("Wing 2", "Wing"));
        ChildItemList.add(new ChildItem("Mid 2", "Mid"));
        ChildItemList.add(new ChildItem("Wing 3", "Wing"));
        ChildItemList.add(new ChildItem("Other", null));


        return ChildItemList;
    }
}
