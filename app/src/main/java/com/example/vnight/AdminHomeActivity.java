package com.example.vnight;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonCreateNewEvent;
    Button buttonLogOut;
    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        buttonCreateNewEvent = (Button)findViewById(R.id.btn_createEvent);
        buttonCreateNewEvent.setOnClickListener(this);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();
        buttonLogOut = (Button)findViewById(R.id.btn_LogOutAdmin);
        buttonLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == buttonCreateNewEvent){
            openDialogBox();
        }
        if(v == buttonLogOut){
            sp_editor.putBoolean("logged", false).apply();
            sp_editor.putString("username", "").apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            AdminHomeActivity.this.finish();
        }
    }

    private void openDialogBox(){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflater.inflate(R.layout.add_event_dialog,null);
        final AlertDialog alertDialog = new AlertDialog.Builder(AdminHomeActivity.this).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Sample");

        final EditText eventName = (EditText)view.findViewById(R.id.eventName);
        final EditText eventLocation = (EditText)view.findViewById(R.id.eventLocation);
        final EditText eventDate = (EditText)view.findViewById(R.id.eventDate);
        final EditText eventTimeStart = (EditText)view.findViewById(R.id.eventTimeStart);
        final EditText eventTimeEnd = (EditText)view.findViewById(R.id.eventTimeEnd);
        final EditText eventPlayers = (EditText)view.findViewById(R.id.eventPlayers);
        final Switch   allowReservation = (Switch)view.findViewById(R.id.allowReservation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            eventDate.setShowSoftInputOnFocus(false);
            eventTimeStart.setShowSoftInputOnFocus(false);
            eventTimeEnd.setShowSoftInputOnFocus(false);
        }


        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminHomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        eventDate.setText( (monthOfYear + 1) + "/" +dayOfMonth + "/" + year);
                    };
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        eventTimeStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminHomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        eventTimeStart.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        eventTimeEnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminHomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        eventTimeEnd.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        //final ScrollView scrollView = (ScrollView) view.findViewById(R.id.createEventDialog);

        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String name = eventName.getText().toString().trim();
                final String date = eventDate.getText().toString().trim();
                final String location = eventLocation.getText().toString().trim();
                final String timeStart = eventTimeStart.getText().toString().trim();
                final String timeEnd = eventTimeEnd.getText().toString().trim();
                final String players = eventPlayers.getText().toString().trim();
                final String reservationOn = allowReservation.isChecked() ? "TRUE":"FALSE";

                Map <String, String> params = new HashMap<>();

                //params.put("action","createNewEvent");
                params.put("eventName",name);
                params.put("eventDate",date);
                params.put("eventTimeStart",timeStart);
                params.put("eventTimeEnd", timeEnd);
                params.put("eventLocation",location);
                params.put("participants", players);
                params.put("reservationOn", reservationOn);

//                final ProgressDialog loading = ProgressDialog.show(AdminHomeActivity.this,"Creating Event","Please wait");
//                final String key = name.replaceAll("\\s+","");
                DatabaseHandler.addRowEntryToSheet(AdminHomeActivity.this, DatabaseHandler.EVENTS_SHEET_NAME, params, new DatabaseHandler.onResponseListener() {
                    @Override
                    public void processResponse(String response) {
                        if(response.compareTo(DatabaseHandler.WriteReturnCodes.ROW_WRITE_SUCCESS) == 0){
                            Toast.makeText(AdminHomeActivity.this,"New event has been created",Toast.LENGTH_LONG).show();
                        }
                        else if(response.compareTo(DatabaseHandler.WriteReturnCodes.DUPLICATE_KEY_DETECTED) == 0){
                            Toast.makeText(AdminHomeActivity.this, "Event '"+name+"' already exists",Toast.LENGTH_LONG).show();
                        }
                    }
                });

//                loading.dismiss();
                //addItemToSheet(name, date, location, timeStart, timeEnd, players, reservationOn);
            }
        });

        alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }

//    private void   addItemToSheet(final String name, final String date, final String location, final String timeStart, final String timeEnd, final String players, final String reservationOn) {
//
//        final ProgressDialog loading = ProgressDialog.show(this,"Reserving...","Please wait");
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseHandler.databaseURL+"?action=createNewEvent",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        loading.dismiss();
//                        Toast.makeText(AdminHomeActivity.this,response,Toast.LENGTH_LONG).show();
////                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
////                        startActivity(intent);
////                        UserHomeActivity.this.finish();
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loading.dismiss();
//                        Toast.makeText(AdminHomeActivity.this,"A problem has occurred. Please try again",Toast.LENGTH_LONG).show();
////                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
////                        startActivity(intent);
////                        ReservationForm.this.finish();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                //here we pass params
//                params.put("action","createNewEvent");
//                params.put("eventName",name);
//                params.put("eventDate",date);
//                params.put("eventTimeStart",timeStart);
//                params.put("eventTimeEnd", timeEnd);
//                params.put("eventLocation",location);
//                params.put("participants", players);
//                params.put("reservationOn", reservationOn);
//
//                return params;
//            }
//        };
//
//        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
//
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        queue.add(stringRequest);
//
//
//    }
}
