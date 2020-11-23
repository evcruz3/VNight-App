package com.example.vnight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnight.utils.EventsAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsListActivity extends AppCompatActivity {
//    GridView mGridView;
//    ArrayList<HashMap<String, String>> eventsList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ArrayList<HashMap<String, String>> eventsList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("eventsList");
        setContentView(R.layout.events_list_activity);



        GridView mGridView = (GridView)findViewById(R.id.gridview);
        EventsAdapter eventsAdapter = new EventsAdapter(this, eventsList);
        mGridView.setAdapter(eventsAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final HashMap<String, String> event = eventsList.get(position);

                Intent intent = new Intent(EventsListActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventsList", eventsList);
                intent.putExtra("eventDetails", event);
                intent.putExtra("positionID", position);
                startActivity(intent);
                EventsListActivity.this.finish();

            }
        });

    }
}
