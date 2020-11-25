package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView welcomeText;
//    SharedPreferences sp;
//    SharedPreferences.Editor sp_editor;
    Button buttonLogOut, buttonViewEvents, buttonSeeReservedPlayers, buttonEditProfile;
    String playerName;
//    SharedPreferenceHandler mSharedPreferenceHandler;
    Context ctx;
    UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_activity);
        ctx = this;

//        mSharedPreferenceHandler = new SharedPreferenceHandler(ctx);
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx,"UserInfo","UserInfo", UserInfo.class);
//        sp = getSharedPreferences("login",MODE_PRIVATE);
//        sp_editor = sp.edit();

        buttonLogOut = (Button)findViewById(R.id.btn_logOut);
        buttonLogOut.setOnClickListener(this);
        buttonViewEvents = (Button)findViewById(R.id.btn_viewEvents);
        buttonViewEvents.setOnClickListener(this);
        buttonSeeReservedPlayers = (Button)findViewById(R.id.btn_list_reserved);
        buttonSeeReservedPlayers.setOnClickListener(this);
        buttonEditProfile = (Button)findViewById(R.id.btn_editUserInfo);
        buttonEditProfile.setOnClickListener(this);

//        String username = sp.getString("username", "");
//        playerName = sp.getString("firstName", "");

        String username = userInfo.getUsername();
        playerName = userInfo.getFirstName();


        welcomeText = (TextView)findViewById(R.id.textView2);
        welcomeText.setText("Welcome, "+username);

    }
    @Override
    public void onClick(View v){
        if(v == buttonLogOut){
//            sp_editor.putBoolean("logged", false).apply();
//            sp_editor.putString("username", "").apply();
            SharedPreferenceHandler.removeObjectFromSharedPreference(ctx,"UserInfo", "UserInfo");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            UserHomeActivity.this.finish();
        }

        if(v == buttonViewEvents){
//            Intent intent = new Intent(getApplicationContext(), ReservationForm.class);
//            startActivity(intent);
            //openDialogBox();
            viewEvents();
        }

        if(v == buttonSeeReservedPlayers){
            Intent intent = new Intent(getApplicationContext(), ListReservedPlayers.class);
            startActivity(intent);
        }

        if(v == buttonEditProfile){
            // TODO: complete this
            Intent intent = new Intent(getApplicationContext(), EditUserInfoActivity.class);
            startActivity(intent);
        }
    }

    private void viewEvents(){
        //final ProgressDialog loading = ProgressDialog.show(this,"Loading Events..","Please wait");
        String[] keys = {"entryID", "key", "eventName", "eventDate", "eventTimeStart", "eventTimeEnd", "eventLocation","reservationOn","participants"};
        DatabaseHandler.getItemsFromSheet(UserHomeActivity.this, "events", keys, new DatabaseHandler.onResponseProcessedListener (){
            @Override
            public void processList(final ArrayList<HashMap<String, String>> list){
                //loading.dismiss();
                if(list.isEmpty()){
                    Toast.makeText(UserHomeActivity.this, "No items can be shown", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), EventsListActivity.class);
                    intent.putExtra("eventsList", list);
                    startActivity(intent);
                    //AdminHomeActivity.this.finish();
                }
            }
        });
    }



    private void   addItemToSheet(final String name, final String positionSelected) {

        //final ProgressDialog loading = ProgressDialog.show(this,"Reserving...","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=reserveSlot",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //loading.dismiss();
                        Toast.makeText(UserHomeActivity.this,response,Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
//                        startActivity(intent);
//                        UserHomeActivity.this.finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        Toast.makeText(UserHomeActivity.this,"A problem has occurred. Please try again",Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
//                        startActivity(intent);
//                        ReservationForm.this.finish();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","reserveSlot");
                params.put("name",name);
                params.put("position",positionSelected);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

}
