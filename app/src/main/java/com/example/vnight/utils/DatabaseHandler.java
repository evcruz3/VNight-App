package com.example.vnight.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
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

final public class DatabaseHandler {
    final static public String databaseURL = "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec";
    final static public String PLAYERS_SHEET_NAME = "Items";
    final static public String EVENTS_SHEET_NAME = "events";
    //final static public String reservationListSheetName = "reservationList";
    final static public int    socketTimeOut = 50000;
    final static public double APP_VERSION = 0.20201201;
    final static public String APP_NOT_SUPPORTED = "-3";


    final static public class WriteReturnCodes{
        final static public String DUPLICATE_KEY_DETECTED = "-1";
        final static public String ROW_WRITE_SUCCESS = "0";
    }

    final static public class DeleteReturnCodes{
        final static public String ENTRY_DOES_NOT_EXIST = "-1";
        final static public String DELETE_SUCCESS = "0";
    }

    final static public class EditReturnCodes{
        final static public String ENTRY_DOES_NOT_EXIST = "-1";
        final static public String ENTRY_RENAME_DUPLICATE = "-2";
        final static public String EDIT_SUCCESS = "0";
    }

//    final static public class GetReturnCodes {
//    }

    public interface onResponseListener{
        void processResponse(final String response);
    }

    public interface onResponseProcessedListener{
        void processList(final ArrayList<HashMap<String, String>> list);
    }

//    public interface onDeleteListener{
//        void onDelete(final String response);
//    }

    static private void addRequestToQueue(Context ctx, StringRequest stringRequest){
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(stringRequest);
    }


    static public void doActionToSheet(final Context ctx, final String sheetName, final String action, final Map<String,String> entries, final onResponseListener responseListener){
        final ProgressDialog loading = ProgressDialog.show(ctx,"Performing action...","Please wait");
//        final String action = "addRowToSheet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseHandler.databaseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if(response.compareTo(APP_NOT_SUPPORTED) == 0){
                            Toast.makeText(ctx, "Your app is not supported. Please update", Toast.LENGTH_LONG);
                        }
                        else {
                            responseListener.processResponse(response);
                        }
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

                if(entries != null)
                    params.putAll(entries);
                //here we pass params

                params.put("appVersion", String.valueOf(APP_VERSION));
                params.put("action",action);
//                params.put("key", key);
                if(sheetName != null)
                    params.put("sheetName", sheetName);


                return params;
            }
        };

        addRequestToQueue(ctx, stringRequest);
    }

    static public void editRowFromSheetByID(final Context ctx, final String sheetName, final int entryID, final Map<String,String> entries, final onResponseListener responseListener){
        final ProgressDialog loading = ProgressDialog.show(ctx,"Editing Entry...","Editing entry of "+sheetName+". Please wait");
        final String action = "editRowFromSheetByID";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseHandler.databaseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if(response.compareTo(APP_NOT_SUPPORTED) == 0){
                            Toast.makeText(ctx, "Your app is not supported. Please update", Toast.LENGTH_LONG);
                        }
                        else {
                            responseListener.processResponse(response);
                        }
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
                params.put("appVersion", String.valueOf(APP_VERSION));
                params.put("entryID", ""+entryID);
                //here we pass params

                params.put("action",action);
//                params.put("key", key);
                params.put("sheetName", sheetName);


                return params;
            }
        };

        addRequestToQueue(ctx, stringRequest);
    }

    static public void getItemsFromSheet(final Context ctx, final String dbName, final String sheetName, final String[] keys, final onResponseProcessedListener responseProcessedListener){
        final ProgressDialog loading =  ProgressDialog.show(ctx,"Fetching Data","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, DatabaseHandler.databaseURL+"?action=getItemsFromSheet&dbName="+dbName+"&sheetName="+sheetName+"&appVersion="+String.valueOf(APP_VERSION),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if(response.compareTo(APP_NOT_SUPPORTED) == 0){
                            Toast.makeText(ctx, "Your app is not supported. Please update", Toast.LENGTH_LONG);
                        }
                        else {
                            Log.d("getItemsFromSheet", response);
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();

                            //System.out.println(jsonResponse);
                            //loading.dismiss();

                            try {
                                //JSONObject jobj = new JSONObject(jsonResponse.substring(jsonResponse.indexOf("{"),jsonResponse.lastIndexOf("}")+1));
                                JSONObject jobj = new JSONObject(response);
                                JSONArray jarray = jobj.getJSONArray("items");
//                                int changeID = jobj.getInt("changeID");
//                                Log.d("getItemsFromSheet.changeID", ""+changeID);

                                for (int i = 0; i < jarray.length(); i++) {

                                    JSONObject jo = jarray.getJSONObject(i);
                                    HashMap<String, String> item = new HashMap<>();

                                    for (int j = 0; j < keys.length; j++) {
                                        item.put(keys[j], jo.getString(keys[j]));
                                    }

                                    list.add(item);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            responseProcessedListener.processList(list);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ctx,"A network problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
        );

        addRequestToQueue(ctx, stringRequest);
    }

    static public void deleteRowFromSheetByID(final Context ctx, final String sheetName, final String entryID, final onResponseListener responseListener){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DatabaseHandler.databaseURL+"?action=deleteRowFromSheetByID&sheetName="+sheetName+"&entryID="+entryID+"&appVersion="+String.valueOf(APP_VERSION),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.compareTo(APP_NOT_SUPPORTED) == 0){
                            Toast.makeText(ctx, "Your app is not supported. Please update", Toast.LENGTH_LONG);
                        }
                        else {
                            responseListener.processResponse(response);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx,"A network problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                    }
                }
        );

        addRequestToQueue(ctx, stringRequest);
    }

    static public void addRowEntryToSheet(final Context ctx, final String sheetName, final Map<String,String> entries, final onResponseListener responseListener ){
        final ProgressDialog loading = ProgressDialog.show(ctx,"Adding Entry...","Adding entry to "+sheetName+". Please wait");
        final String action = "addRowToSheet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseHandler.databaseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if(response.compareTo(APP_NOT_SUPPORTED) == 0){
                            Toast.makeText(ctx, "Your app is not supported. Please update", Toast.LENGTH_LONG);
                        }
                        else {
                            responseListener.processResponse(response);
                        }
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
                params.put("appVersion", String.valueOf(APP_VERSION));
                params.put("action","addRowEntryToSheet");
//                params.put("key", key);
                params.put("sheetName", sheetName);


                return params;
            }
        };

        addRequestToQueue(ctx, stringRequest);

    }

}
