package com.example.vnight;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
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
import com.example.vnight.services.UsersDatabaseService;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;
import com.example.vnight.utils.UsersDatabase;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    //SharedPreferences sp;
    //SharedPreferences.Editor sp_editor;
    private static final String TAG = "SplashActivity";
    TextView Text;

//    SharedPreferenceHandler mSharedPreferenceHandler;
    Context ctx;
    UserInfo userInfo;
    HashMap<String, HashMap<String, String>> usersDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ctx = this;
//        mSharedPreferenceHandler = new SharedPreferenceHandler(ctx);
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx,"UserInfo", "UserInfo", UserInfo.class);
        TypeToken<HashMap<String,HashMap<String,String>>> token = new TypeToken<HashMap<String,HashMap<String,String>>>(){};
        usersDatabase = SharedPreferenceHandler.getSavedObjectFromPreference(ctx, "UsersDatabase", "users", token.getType());


        //sp = getSharedPreferences("login",MODE_PRIVATE);
        //sp_editor = sp.edit();
        Text = (TextView)findViewById(R.id.textView);
        Intent intent;

        ComponentName componentName = new ComponentName(this, UsersDatabaseService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(30*60*1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job Scheduled");
        } else{
            Log.d(TAG, "Job scheduling failed");
        }


//        if(sp.getBoolean("logged",false)){
//            Text.setText("Logged: True");
//        }
//        else
//            Text.setText("Logged: False");
        Text.setText("Initializing...");

        if(usersDatabase == null){
            final String[] keys = {"entryID", "firstName", "lastName", "batch", "contactNum", "username"};
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
                                    //Integer localChangeID = SharedPreferenceHandler.getSavedObjectFromPreference(ctx, "UsersDatabase", "changeID", Integer.class);

                                    //if((localChangeID == null) || (serverChangeID > localChangeID.intValue())){
                                    SharedPreferenceHandler.saveObjectToSharedPreference(ctx, "UsersDatabase", "users", list);
                                    SharedPreferenceHandler.saveObjectToSharedPreference(ctx, "UsersDatabase", "changeID", serverChangeID);

//                                    UsersDatabase.setUsersDatabase(list);

                                    checkUserInfo();
                                    //}

                                    //Log.d(TAG, "Job finished");

                                    //DatabaseHandler.getItemsFromSheet();

                                    //jobFinished(jobParameters, false);
                                } catch (JSONException e) {
                                    Log.d(TAG, e.getMessage());
                                    //jobFinished(jobParameters, true);
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
                            //jobFinished(jobParameters, false);
                        }
                    }
            );

            UsersDatabaseService.addRequestToQueue(ctx, stringRequest);
        }
        else {
//            UsersDatabase.setUsersDatabase(usersDatabase);
            checkUserInfo();
        }

    }

    protected void checkUserInfo(){
        if (userInfo != null) {

//            if(sp.getString("username","").compareTo("admin") == 0)
            if (userInfo.getUsername().compareTo("admin") == 0)
                startMainActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
            else
                startMainActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
        } else {
            DatabaseHandler.doActionToSheet(ctx, null, "pokeServer", null, new DatabaseHandler.onResponseListener() {
                @Override
                public void processResponse(String response) {
                    Text.setText("All is set!");
                    startMainActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    SplashActivity.this.finish();
                }
            });
        }
    }
    protected void startMainActivity(final Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 0);
    }

}
