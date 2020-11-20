package com.example.vnight.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vnight.AdminHomeActivity;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    static public String databaseURL = "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec";
    static public String PLAYERS_SHEET_NAME = "Items";
    static public String EVENTS_SHEET_NAME = "events";
    static public String reservationListSheetName = "reservationList";


    static public class WriteReturnCodes{
        static public String DUPLICATE_KEY_DETECTED = "-1";
        static public String ROW_WRITE_SUCCESS = "0";
    }

    public interface onResponseListener{
        void processResponse(final String response);
    }



    static public void addRowEntryToSheet(final Context ctx, final String sheetName, final Map<String,String> entries, final String key, final onResponseListener responseListener ){
        if (key.isEmpty()){
            throw new RuntimeException("key must not be empty");
        }
        final ProgressDialog loading = ProgressDialog.show(ctx,"Adding Entry...","Adding entry to "+sheetName+". Please wait");
        final String action = "addRowToSheet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseHandler.databaseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        responseListener.processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ctx,"A network problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.putAll(entries);
                //here we pass params

                params.put("action","addRowEntryToSheet");
                params.put("key", key);
                params.put("sheetName", sheetName);


                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(stringRequest);


    }
}
