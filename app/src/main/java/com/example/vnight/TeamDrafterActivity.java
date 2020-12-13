package com.example.vnight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vnight.teamDrafter.SlotItem;
import com.example.vnight.teamDrafter.TeamItem;
import com.example.vnight.utils.ChildItemAdapter;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.ParentItemAdapter;
import com.example.vnight.utils.ReservedPlayersAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vnight.utils.DatabaseHandler.APP_VERSION;
import static com.example.vnight.utils.DatabaseHandler.socketTimeOut;

public class TeamDrafterActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    Button btnCreateNewTeam;
    Button btnSubmitDraft;
    //List<ParentItem> itemList;
    List<TeamItem> teamList;
    ParentItemAdapter parentItemAdapter;
    RecyclerView ParentRecyclerViewItem;
    ArrayList<HashMap<String, String>> reservedPlayers;
    HashMap<String, String> event;
    RecyclerView rvReservedPlayers;
    ReservedPlayersAdapter reservedPlayersAdapter;
    ArrayList<HashMap<String, String>> teamComposition;
    Context ctx;
    int currTeamIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_drafter_actvitiy);

        ctx = this;
        currTeamIndex = 0;

        btnCreateNewTeam = findViewById(R.id.btn_addNewTeam);
        btnCreateNewTeam.setOnClickListener(this);
        btnSubmitDraft = findViewById(R.id.btn_submitDraft);
        btnSubmitDraft.setOnClickListener(this);

        //itemList is list of teams
//        itemList = new ArrayList<>();


        teamComposition = new ArrayList<HashMap<String, String>>();

        teamList = new ArrayList<>();
        event = (HashMap<String, String>) getIntent().getSerializableExtra("event");

        // Populate reserved players pool
        reservedPlayers = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("reservedPlayers");
        rvReservedPlayers = (RecyclerView) findViewById(R.id.gv_reservedPlayers);
        reservedPlayersAdapter = new ReservedPlayersAdapter(this, reservedPlayers);
        rvReservedPlayers.setAdapter(reservedPlayersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvReservedPlayers.setLayoutManager(linearLayoutManager);
        addNewParentItem(teamComposition.size() + 1);

        ParentRecyclerViewItem = findViewById(
                R.id.parent_recyclerview);

        // Initialise the Linear layout manager
        LinearLayoutManager
                layoutManager
                = new LinearLayoutManager(
                TeamDrafterActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SnapHelper snapHelper = new PagerSnapHelper();

        ChildItemAdapter.ChildItemOnDropListener childItemOnDropListener = new ChildItemAdapter.ChildItemOnDropListener() {

            @Override
            public boolean onDropSlotIsEmpty(HashMap<String, String> pendingPlayer, String slotID) {
                teamComposition.get(currTeamIndex).put(slotID, pendingPlayer.get("userID"));
                parentItemAdapter.notifyDataSetChanged();
                reservedPlayers.remove(pendingPlayer);
                reservedPlayersAdapter.notifyDataSetChanged();
                return ChildItemAdapter.ChildItemOnDropListener.DROP_SUCCESS;
            }

            @Override
            public boolean onDropSlotIsNotEmpty(HashMap<String, String> previousPlayer, HashMap<String, String> pendingPlayer, String slotID) {
                teamComposition.get(currTeamIndex).put(slotID, pendingPlayer.get("userID"));
                reservedPlayers.add(previousPlayer);
                reservedPlayers.remove(pendingPlayer);
                parentItemAdapter.notifyDataSetChanged();
                reservedPlayersAdapter.notifyDataSetChanged();
                return ChildItemAdapter.ChildItemOnDropListener.DROP_SUCCESS;
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

        ChildItemAdapter.ChildItemOnDoubleClickListener mChildItemOnDoubleClickListener = new ChildItemAdapter.ChildItemOnDoubleClickListener() {
            @Override
            public boolean onDoubleClick(HashMap<String, String> playerInfo, int teamID, String slotID) {
                Toast.makeText(ctx, "Remove slot detected", Toast.LENGTH_LONG).show();
                teamComposition.get(teamID-1).put(slotID, null);
                parentItemAdapter.notifyDataSetChanged();
                reservedPlayers.add(playerInfo);
                reservedPlayersAdapter.notifyDataSetChanged();
                return true;
            }
        };


        parentItemAdapter = new ParentItemAdapter(ctx, teamList);
        parentItemAdapter.setChildItemOnDoubleClickListener(mChildItemOnDoubleClickListener);
        parentItemAdapter.setChildItemOnDropListener(childItemOnDropListener);
        parentItemAdapter.setParentItemOnDragListener(parentItemOnDragListener);

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
        if (view == btnCreateNewTeam) {
            addNewParentItem(teamComposition.size() + 1);
            parentItemAdapter.notifyDataSetChanged();
            ParentRecyclerViewItem.scrollToPosition(parentItemAdapter.getItemCount() - 1);
        }
        if (view == btnSubmitDraft) {
            sendJSON();
        }
    }

    private void addNewParentItem(int id) {
        // objects
        TeamItem team = new TeamItem(id, String.valueOf(id));
        teamList.add(team);

        // hashmap
        teamComposition.add(ChildItemList(id));
    }

//    private List<SlotItem> slotItemList() {
//        List<SlotItem> slotItemList = new ArrayList<>();
//
//        slotItemList.add(new SlotItem(SlotItem.ID_WING1));
//        slotItemList.add(new SlotItem(SlotItem.ID_MID1));
//        slotItemList.add(new SlotItem(SlotItem.ID_SETTER));
//        slotItemList.add(new SlotItem(SlotItem.ID_WING2));
//        slotItemList.add(new SlotItem(SlotItem.ID_MID2));
//        slotItemList.add(new SlotItem(SlotItem.ID_WING3));
//        slotItemList.add(new SlotItem(SlotItem.ID_LIBERO));
//
//        return slotItemList;
//    }

    // Method to pass the arguments
    // for the elements
    // of child RecyclerView
    private HashMap<String, String> ChildItemList(int id) {
        HashMap<String, String> ChildItemList
                = new HashMap<String, String>();

        ChildItemList.put("teamID", String.valueOf(id));
        ChildItemList.put("wing1", null);
        ChildItemList.put("mid1", null);
        ChildItemList.put("setter", null);
        ChildItemList.put("wing2", null);
        ChildItemList.put("mid2", null);
        ChildItemList.put("wing3", null);
        ChildItemList.put("libero", null);


        return ChildItemList;
    }

    private void sendJSON() {

        List<JSONObject> outputArray = new ArrayList<JSONObject>();

        for (HashMap<String, String> team : teamComposition) {
            JSONObject obj = new JSONObject(team);
            outputArray.add(obj);
        }

        JSONArray ja = new JSONArray(outputArray);

        JSONObject output = new JSONObject();
        try {
            output.put("teamComposition", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("TeamDraftOutput", output.toString());


        JsonObjectRequest JOR = new JsonObjectRequest(
                Request.Method.POST,
                DatabaseHandler.databaseURL + "?action=updateTeamComposition&sheetName=" + event.get("key") + "&appVersion=" + String.valueOf(APP_VERSION),
                output,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("response").compareTo("0") == 0){
                                Toast.makeText(ctx, "Team Draft has been posted", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        JOR.setRetryPolicy(retryPolicy);


        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(JOR);


    }
}
