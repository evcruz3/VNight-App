package com.example.vnight.services;

import android.app.ProgressDialog;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;
import com.example.vnight.utils.UsersDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersDatabaseService extends JobService {
    private static final String TAG = "UsersDatabaseService";
    private boolean jobCancelled= false;
    //private static AtomicInteger changeID = new AtomicInteger(0);
    private static final String[] keys = {"entryID", "firstName", "lastName", "batch", "contactNum", "username"};
    //private static final SharedPreferenceHandler sharedPreferenceHandler;

//    public static void increment(int newValue){
//        changeID.getAndSet(newValue);
//    }
//
//    public static int getChangeID(){
//        return changeID.get();
//    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(jobParameters,this);
        return true;
    }

    private void doBackgroundWork(final JobParameters jobParameters, final Context ctx){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, DatabaseHandler.databaseURL+"?action=getItemsFromSheet&sheetName="+"Items"+"&appVersion="+String.valueOf(DatabaseHandler.APP_VERSION),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //loading.dismiss();
                                    if(response.compareTo(DatabaseHandler.APP_NOT_SUPPORTED) == 0){
                                        Log.d(TAG,"Your app is not supported. Please update");
                                    }
                                    else {
                                        Log.d(TAG, "response" + response);
                                        HashMap<String,HashMap<String, String>> list = new HashMap<String,HashMap<String, String>>();

                                        //System.out.println(jsonResponse);
                                        //loading.dismiss();

                                        try {
                                            //JSONObject jobj = new JSONObject(jsonResponse.substring(jsonResponse.indexOf("{"),jsonResponse.lastIndexOf("}")+1));
                                            JSONObject jobj = new JSONObject(response);
                                            JSONArray jarray = jobj.getJSONArray("items");
                                            int serverChangeID = jobj.getInt("changeID");
//                                Log.d("getItemsFromSheet.changeID", ""+changeID);

                                            for (int i = 0; i < jarray.length(); i++) {

                                                JSONObject jo = jarray.getJSONObject(i);
                                                HashMap<String, String> item = new HashMap<>();

                                                for (int j = 0; j < keys.length; j++) {
                                                    item.put(keys[j], jo.getString(keys[j]));
                                                }

                                                list.put(item.get("entryID"),item);
                                            }

                                            //Log.d(TAG, "response: "+response);
                                            Integer localChangeID = SharedPreferenceHandler.getSavedObjectFromPreference(ctx, "UsersDatabase", "changeID", Integer.class);

                                            if((localChangeID == null) || (serverChangeID > localChangeID.intValue())){
                                                SharedPreferenceHandler.saveObjectToSharedPreference(ctx, "UsersDatabase", "users", list);
                                                SharedPreferenceHandler.saveObjectToSharedPreference(ctx, "UsersDatabase", "changeID", serverChangeID);
//                                                UsersDatabase.setUsersDatabase(list);
                                            }

                                            Log.d(TAG, "Job finished");

                                            //DatabaseHandler.getItemsFromSheet();

                                            jobFinished(jobParameters, false);
                                        } catch (JSONException e) {
                                            Log.d(TAG, e.getMessage());
                                            jobFinished(jobParameters, true);
                                            e.printStackTrace();
                                        }
                                        //responseProcessedListener.processList(list);
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //loading.dismiss();
                                    Log.d(TAG,error.networkResponse.toString());
                                    jobFinished(jobParameters, false);
                                }
                            }
                    );

                    addRequestToQueue(ctx, stringRequest);

                }

            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    static public void addRequestToQueue(Context ctx, StringRequest stringRequest){
        RetryPolicy retryPolicy = new DefaultRetryPolicy(DatabaseHandler.socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(ctx);

        queue.add(stringRequest);
    }
}
