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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    final static public String databaseURL = "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec";
    final static public String PLAYERS_SHEET_NAME = "Items";
    final static public String EVENTS_SHEET_NAME = "events";
    final static public String reservationListSheetName = "reservationList";
    final static public int    socketTimeOut = 50000;


    static public class WriteReturnCodes{
        static public String DUPLICATE_KEY_DETECTED = "-1";
        static public String ROW_WRITE_SUCCESS = "0";
    }

    public interface onResponseListener{
        void processResponse(final String response);
    }

    public interface onResponseProcessedListener{
        void processList(final ArrayList<HashMap<String, String>> list);
    }

    public interface onDeleteListener{
        void onDelete(final String response);
    }


    static public void doActionToSheet(final Context ctx, final String sheetName, final String action, final Map<String,String> entries, final onResponseListener responseListener){
        final ProgressDialog loading = ProgressDialog.show(ctx,"Performing action...","Please wait");
//        final String action = "addRowToSheet";

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

                params.put("action",action);
//                params.put("key", key);
                params.put("sheetName", sheetName);


                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(stringRequest);
    }

    static public void getItemsFromSheet(final Context ctx, final String sheetName, final String[] keys, final onResponseProcessedListener responseProcessedListener){
        //final ProgressDialog loading =  ProgressDialog.show(ctx,"Fetching Data","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, DatabaseHandler.databaseURL+"?action=getItemsFromSheet&sheetName="+sheetName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<HashMap<String, String>> list = new ArrayList<>();

                        //System.out.println(jsonResponse);
                        //loading.dismiss();

                        try {
                            //JSONObject jobj = new JSONObject(jsonResponse.substring(jsonResponse.indexOf("{"),jsonResponse.lastIndexOf("}")+1));
                            JSONObject jobj = new JSONObject(response);
                            JSONArray jarray = jobj.getJSONArray("items");


                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jo = jarray.getJSONObject(i);
                                HashMap<String, String> item = new HashMap<>();

                                for(int j = 0; j<keys.length; j++){
                                    item.put(keys[j], jo.getString(keys[j]));
                                }

                                list.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        responseProcessedListener.processList(list);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx,"A network problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
        );

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(stringRequest);



    }

    static public void deleteRowFromSheetByID(final Context ctx, final String sheetName, final String entryID, final onDeleteListener deleteListener){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DatabaseHandler.databaseURL+"?action=deleteRowFromSheetByID&sheetName="+sheetName+"&entryID="+entryID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        deleteListener.onDelete(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx,"A network problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
        );

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(stringRequest);
    }

    static public void addRowEntryToSheet(final Context ctx, final String sheetName, final Map<String,String> entries, final onResponseListener responseListener ){
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
//                params.put("key", key);
                params.put("sheetName", sheetName);


                return params;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(stringRequest);


    }
}
