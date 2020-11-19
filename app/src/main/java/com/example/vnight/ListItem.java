package com.example.vnight;

import android.app.ProgressDialog;
import android.os.Bundle;
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

        getItems();

    }


    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading Players","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    private void parseItems(String jsonResponse) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        //System.out.println(jsonResponse);

        try {
            //JSONObject jobj = new JSONObject(jsonResponse.substring(jsonResponse.indexOf("{"),jsonResponse.lastIndexOf("}")+1));
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String firstName = jo.getString("firstName");
                String lastName = jo.getString("lastName");
                String batch = jo.getString("batch");
                String contactNum = jo.getString("contactNum");
                String username = jo.getString("username");
                String password = jo.getString("password");


                HashMap<String, String> item = new HashMap<>();
                item.put("firstName", firstName);
                item.put("lastName", lastName);
                item.put("batch",batch);
                item.put("contactNum", contactNum);
                item.put("username", "@"+username);
                item.put("password",password);

                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"firstName","lastName","username","batch"},new int[]{R.id.firstName,R.id.lastName,R.id.batch,R.id.username});



        listView.setAdapter(adapter);
        loading.dismiss();
    }


}
