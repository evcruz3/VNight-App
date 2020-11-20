package com.example.vnight;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vnight.utils.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListItem extends AppCompatActivity {


    ListView listView;
    ListAdapter adapter;
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        listView = (ListView) findViewById(R.id.lv_items);

        //getItems();
        String[] keys = {"entryID", "firstName", "lastName", "batch", "contactNum", "username"};
        DatabaseHandler.getItemsFromSheet(ListItem.this, "Items", keys, new DatabaseHandler.onResponseProcessedListener (){
            @Override
            public void processList(final ArrayList<HashMap<String, String>> list){
                adapter = new SimpleAdapter(ListItem.this,list,R.layout.list_item_row,
                        new String[]{"firstName","lastName","username","batch"},new int[]{R.id.firstName,R.id.lastName,R.id.batch,R.id.username});

                listView.setAdapter(adapter);
            }
        });

    }

}
