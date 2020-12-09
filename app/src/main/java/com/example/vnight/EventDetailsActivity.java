package com.example.vnight;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
//import android.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.ReservedPlayersAdapter;
import com.example.vnight.utils.SharedPreferenceHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_actionButton;
    Button btn_editEvent;
    Button btn_openDrafter;
    ArrayList<HashMap<String, String>> eventsList;
    Integer positionID;
    HashMap<String, String> event;
    TextView eventName, eventDate, eventLocation, eventTimeStart, eventTimeEnd, eventParticipants, reservationOn;
    Boolean isEventOpen;
//    SharedPreferences sp;
//    SharedPreferences.Editor sp_editor;

//    SharedPreferenceHandler mSharedPreferenceHandler;
    UserInfo userInfo;
    Context ctx;
    RecyclerView mRecyclerView;
    ReservedPlayersAdapter reservedPlayersAdapter;

    String username;
    String playerName, playerSurname;
    boolean isAdmin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);
        eventsList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("eventsList");
        event = (HashMap<String, String>) getIntent().getSerializableExtra("eventDetails");
        positionID = (Integer) getIntent().getSerializableExtra("positionID");
        mRecyclerView = (RecyclerView)findViewById(R.id.gv_reservedPlayers);

        ctx = this;

//        sp = getSharedPreferences("login",MODE_PRIVATE);
//        sp_editor = sp.edit();

//        mSharedPreferenceHandler = new SharedPreferenceHandler(ctx);
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx,"UserInfo", "UserInfo", UserInfo.class);

//        username = sp.getString("username", "");
//        playerName = sp.getString("firstName", "");
//        playerSurname = sp.getString("lastName", "");
        username = userInfo.getUsername();
        playerName = userInfo.getFirstName();
        playerSurname = userInfo.getLastName();

        isAdmin = username.compareTo("admin") == 0 ? true : false;

        eventName = (TextView)findViewById(R.id.et_eventName);
        eventDate = (TextView)findViewById(R.id.et_date);
        eventLocation = (TextView)findViewById(R.id.et_location);
        eventTimeStart = (TextView)findViewById(R.id.et_timeStart);
        eventTimeEnd = (TextView)findViewById(R.id.et_timeEnd);
        eventParticipants = (TextView)findViewById(R.id.et_participants);
        btn_actionButton = (Button)findViewById(R.id.btn_actionButton);
        reservationOn = (TextView)findViewById(R.id.tv_reservationOn);
        btn_actionButton.setOnClickListener(this);
        btn_openDrafter = (Button)findViewById(R.id.btn_openDrafter);
        btn_editEvent = (Button)findViewById(R.id.btn_editEvent);
        isEventOpen = event.get("reservationOn").compareTo("TRUE") == 0 ? true:false;
        if(isEventOpen){
            reservationOn.setText("Reservation is Open");
        }
        else{
            reservationOn.setText("Reservation is closed");
        }

        if(isAdmin){
            btn_openDrafter.setVisibility(View.VISIBLE);
            btn_openDrafter.setEnabled(true);
            btn_openDrafter.setOnClickListener(this);
            btn_editEvent.setVisibility(View.VISIBLE);
            btn_editEvent.setEnabled(true);
            btn_editEvent.setOnClickListener(this);
            btn_actionButton.setText("Delete Event");
        }
        else{
            btn_openDrafter.setVisibility(View.INVISIBLE);
            btn_openDrafter.setEnabled(false);
            btn_editEvent.setVisibility(View.INVISIBLE);
            btn_editEvent.setEnabled(false);
            btn_actionButton.setText("Reserve a Slot");
            btn_actionButton.setEnabled(isEventOpen);
        }

        eventName.setText(event.get("eventName"));
        eventDate.setText(event.get("eventDate"));
        eventLocation.setText(event.get("eventLocation"));
        eventTimeStart.setText(event.get("eventTimeStart"));
        eventTimeEnd.setText(event.get("eventTimeEnd"));
        eventParticipants.setText(event.get("participants"));

        displayReservedPlayers();

    }

    @Override
    public void onClick(View v){
        if(v==btn_actionButton){
            if(isAdmin) {
                final AlertDialog alertDialog = new AlertDialog.Builder(EventDetailsActivity.this).create();
//            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
//            final View view = layoutInflater.inflate(R.layout.,null);

                alertDialog.setTitle("Confirm Remove Event");
                alertDialog.setMessage("Are you sure you want to remove this event?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //final ProgressDialog loading = ProgressDialog.show(EventDetailsActivity.this, "Removing Event", "Please wait");
                        Map<String, String> params = new HashMap<>();

                        //params.put("action","createNewEvent");
                        params.put("entryID", event.get("entryID"));

                        DatabaseHandler.doActionToSheet(EventDetailsActivity.this, "events", "deleteEvent", params, new DatabaseHandler.onResponseListener() {
                            @Override
                            public void processResponse(String response) {
                                //loading.dismiss();
                                if (response.compareTo(DatabaseHandler.DeleteReturnCodes.DELETE_SUCCESS) == 0) {
                                    eventsList.remove(eventsList.indexOf(event));
                                    Toast.makeText(EventDetailsActivity.this, "Event removed", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EventDetailsActivity.this, EventsListActivity.class);
                                    intent.putExtra("eventsList", eventsList);
                                    startActivity(intent);
                                    EventDetailsActivity.this.finish();
                                } else if (response.compareTo(DatabaseHandler.DeleteReturnCodes.ENTRY_DOES_NOT_EXIST) == 0) {
                                    Toast.makeText(EventDetailsActivity.this, "An error occurred. Please Try Again", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

//                    DatabaseHandler.deleteRowFromSheetByID(EventDetailsActivity.this, "events", event.get("entryID"), new DatabaseHandler.onDeleteListener() {
//                        @Override
//                        public void onDelete(String response) {
//                            loading.dismiss();
//                            if(response.compareTo("0") == 0){
//                                Toast.makeText(EventDetailsActivity.this, "Event removed", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(EventDetailsActivity.this, EventsListActivity.class);
//                                intent.putExtra("eventsList", eventsList);
//                                startActivity(intent);
//                                EventDetailsActivity.this.finish();
//                            }
//                            else{
//                                Toast.makeText(EventDetailsActivity.this, "An error occurred. Please Try Again", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
//            alertDialog.setView(view);
                alertDialog.show();
            }
            else {

                openReserveDialogBox();
            }
        }

        if(v == btn_editEvent){
            openEditEventDialogBox();
        }

        if(v == btn_openDrafter){
            Intent intent = new Intent(EventDetailsActivity.this, TeamDrafterActivity.class);
            intent.putExtra("eventsList", eventsList);
            startActivity(intent);
        }
    }

    private void openReserveDialogBox(){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflater.inflate(R.layout.reservation_form_dialog,null);
        final AlertDialog alertDialog = new AlertDialog.Builder(EventDetailsActivity.this).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Please select a position");

        final LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.reserveDialog);
        final Spinner spinnerPosition = (Spinner)view.findViewById(R.id.dialog_spinnerPosition);
        final EditText guestName = (EditText)view.findViewById(R.id.et_guestName);

        List<String> positions = new ArrayList<String>();
        positions.add("Wing");
        positions.add("Mid");
        positions.add("Setter");
        positions.add("Libero");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, positions);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPosition.setAdapter(dataAdapter);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, String> params = new HashMap<>();
                String userID;

                String gName = guestName.getText().toString().trim();
                if(gName.isEmpty()){
                    userID = ""+userInfo.getEntryID();
//                    firstName = playerName;
                }
                else {
                    userID = gName;
//                    lastName = "("+playerName+"'s Guest)";
                }
                params.put("userID", userID);
                //params.put("lastName", lastName);
                params.put("position", spinnerPosition.getSelectedItem().toString().trim());

                final String reservedName = gName.isEmpty() ? userInfo.getFirstName() : userID;

                DatabaseHandler.doActionToSheet(ctx, event.get("key"), "reserveSlot", params, new DatabaseHandler.onResponseListener(){
                    @Override
                    public void processResponse(String response) {
                        if(response.compareTo(DatabaseHandler.WriteReturnCodes.ROW_WRITE_SUCCESS) == 0)
                            Toast.makeText(EventDetailsActivity.this, "Reservation for "+reservedName+" successful", Toast.LENGTH_LONG).show();
                        else if (response.compareTo(DatabaseHandler.WriteReturnCodes.DUPLICATE_KEY_DETECTED) == 0){
                            Toast.makeText(EventDetailsActivity.this, "You've already made a reservation for " + reservedName, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_LONG).show();
                        }

                    }
                });

//                DatabaseHandler.addRowEntryToSheet(EventDetailsActivity.this, event.get("key"), params, new DatabaseHandler.onResponseListener() {
//                    @Override
//                    public void processResponse(String response) {
//                        if(response.compareTo(DatabaseHandler.WriteReturnCodes.ROW_WRITE_SUCCESS) == 0)
//                            Toast.makeText(EventDetailsActivity.this, "Reservation for "+reservedName+" successful", Toast.LENGTH_LONG).show();
//                        else if (response.compareTo(DatabaseHandler.WriteReturnCodes.DUPLICATE_KEY_DETECTED) == 0){
//                            Toast.makeText(EventDetailsActivity.this, "You've already made a reservation for " + reservedName, Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });



            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();

    }

    private void openEditEventDialogBox(){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflater.inflate(R.layout.add_event_dialog,null);
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
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
        final Switch allowReservation = (Switch)view.findViewById(R.id.allowReservation);

        eventName.setText(event.get("eventName"));
        eventLocation.setText(event.get("eventLocation"));
        eventDate.setText(event.get("eventDate"));
        eventTimeStart.setText(event.get("eventTimeStart"));
        eventTimeEnd.setText(event.get("eventTimeEnd"));
        eventPlayers.setText(event.get("participants"));
        allowReservation.setChecked(event.get("reservationOn").compareTo("TRUE") == 0 ? true:false);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String tmp = minute == 0 ? "00": Integer.toString(minute);
                        eventTimeStart.setText(hourOfDay + ":" + tmp);
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String tmp = minute == 0 ? "00": Integer.toString(minute);
                        eventTimeEnd.setText(hourOfDay + ":" + tmp);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        //final ScrollView scrollView = (ScrollView) view.findViewById(R.id.createEventDialog);

        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String name = eventName.getText().toString().trim();
                final String date = eventDate.getText().toString().trim();
                final String location = eventLocation.getText().toString().trim();
                final String timeStart = eventTimeStart.getText().toString().trim();
                final String timeEnd = eventTimeEnd.getText().toString().trim();
                final String players = eventPlayers.getText().toString().trim();
                final String reservationOn = allowReservation.isChecked() ? "TRUE":"FALSE";


                if (name.isEmpty()){
                    Toast.makeText(ctx,"Event Name cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if (date.isEmpty()){
                    Toast.makeText(ctx,"Event Date cannot be empty",Toast.LENGTH_LONG).show();
                }
                else if  (timeStart.isEmpty() || timeEnd.isEmpty()){
                    Toast.makeText(ctx,"Please specify the time of the event",Toast.LENGTH_LONG).show();
                }
                else if (players.isEmpty()){
                    Toast.makeText(ctx,"Please specify number of players",Toast.LENGTH_LONG).show();
                }
                else {
                    Map<String, String> params = new HashMap<>();

                    //params.put("action","createNewEvent");
                    params.put("entryID", event.get("entryID"));
                    params.put("eventName", name);
                    params.put("eventDate", date);
                    params.put("eventTimeStart", timeStart);
                    params.put("eventTimeEnd", timeEnd);
                    params.put("eventLocation", location);
                    params.put("participants", players);
                    params.put("reservationOn", reservationOn);

//                final ProgressDialog loading = ProgressDialog.show(ctx,"Creating Event","Please wait");
//                final String key = name.replaceAll("\\s+","");
//                    DatabaseHandler.doActionToSheet(ctx, "events", "createNewEvent", params, );

                    DatabaseHandler.doActionToSheet(ctx, DatabaseHandler.EVENTS_SHEET_NAME, "editEvent", params, new DatabaseHandler.onResponseListener() {
                        @Override
                        public void processResponse(String response) {
                            if (response.compareTo(DatabaseHandler.WriteReturnCodes.ROW_WRITE_SUCCESS) == 0) {
                                Toast.makeText(ctx, "Event has been updated", Toast.LENGTH_LONG).show();
                                if(isAdmin) {
                                    startActivity(new Intent(EventDetailsActivity.this,AdminHomeActivity.class));
                                    EventDetailsActivity.this.finish();
                                }
                                else{
                                    startActivity(new Intent(EventDetailsActivity.this,UserHomeActivity.class));
                                    EventDetailsActivity.this.finish();
                                }
                            } else if (response.compareTo(DatabaseHandler.EditReturnCodes.ENTRY_RENAME_DUPLICATE) == 0) {
                                Toast.makeText(ctx, "Event '" + name + "' already exists", Toast.LENGTH_LONG).show();
                            } else if (response.compareTo(DatabaseHandler.EditReturnCodes.ENTRY_DOES_NOT_EXIST) == 0) {
                                Toast.makeText(ctx, "Event '" + name + "' does not exist. It may have been deleted", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(ctx, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
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

    private void displayReservedPlayers(){
//        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.RecyclerView);

        String[] keys = {"entryID", "userID", "position"};

        DatabaseHandler.getItemsFromSheet(ctx, "eventsReservations", event.get("key"), keys, new DatabaseHandler.onResponseProcessedListener (){
            @Override
            public void processList(final ArrayList<HashMap<String, String>> list){
                //loading.dismiss();
                if(list.isEmpty()){
                    Toast.makeText(ctx, "No items can be shown", Toast.LENGTH_LONG).show();
                }
                else{
                    reservedPlayersAdapter = new ReservedPlayersAdapter(ctx, list);
                    mRecyclerView.setAdapter(reservedPlayersAdapter);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(ctx,4));
                    //AdminHomeActivity.this.finish();
                }
            }
        });


    }
}
