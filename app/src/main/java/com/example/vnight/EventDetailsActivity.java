package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_actionButton;
    ArrayList<HashMap<String, String>> eventsList;
    Integer positionID;
    HashMap<String, String> event;

//    SharedPreferences sp;
//    SharedPreferences.Editor sp_editor;

    SharedPreferenceHandler mSharedPreferenceHandler;
    UserInfo userInfo;
    Context ctx;

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

        ctx = this;

//        sp = getSharedPreferences("login",MODE_PRIVATE);
//        sp_editor = sp.edit();

        mSharedPreferenceHandler = new SharedPreferenceHandler(ctx);
        userInfo = mSharedPreferenceHandler.getSavedObjectFromPreference("UserInfo", "UserInfo", UserInfo.class);

//        username = sp.getString("username", "");
//        playerName = sp.getString("firstName", "");
//        playerSurname = sp.getString("lastName", "");
        username = userInfo.getUsername();
        playerName = userInfo.getFirstName();
        playerSurname = userInfo.getLastName();

        isAdmin = username.compareTo("admin") == 0 ? true : false;

        EditText eventName, eventDate, eventLocation, eventTimeStart, eventTimeEnd, eventParticipants;
        eventName = (EditText)findViewById(R.id.et_eventName);
        eventDate = (EditText)findViewById(R.id.et_date);
        eventLocation = (EditText)findViewById(R.id.et_location);
        eventTimeStart = (EditText)findViewById(R.id.et_timeStart);
        eventTimeEnd = (EditText)findViewById(R.id.et_timeEnd);
        eventParticipants = (EditText)findViewById(R.id.et_participants);
        btn_actionButton = (Button)findViewById(R.id.btn_actionButton);
        btn_actionButton.setOnClickListener(this);
        if(isAdmin){
            btn_actionButton.setText("Delete Event");
        }
        else{
            btn_actionButton.setText("Reserve a Slot");
        }

        eventName.setText(event.get("eventName"));
        eventDate.setText(event.get("eventDate"));
        eventLocation.setText(event.get("eventLocation"));
        eventTimeStart.setText(event.get("eventTimeStart"));
        eventTimeEnd.setText(event.get("eventTimeEnd"));
        eventParticipants.setText(event.get("participants"));

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
                        final ProgressDialog loading = ProgressDialog.show(EventDetailsActivity.this, "Removing Event", "Please wait");
                        Map<String, String> params = new HashMap<>();

                        //params.put("action","createNewEvent");
                        params.put("entryID", event.get("entryID"));

                        DatabaseHandler.doActionToSheet(EventDetailsActivity.this, "events", "deleteEvent", params, new DatabaseHandler.onResponseListener() {
                            @Override
                            public void processResponse(String response) {
                                loading.dismiss();
                                if (response.compareTo("0") == 0) {
                                    eventsList.remove(eventsList.indexOf(event));
                                    Toast.makeText(EventDetailsActivity.this, "Event removed", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EventDetailsActivity.this, EventsListActivity.class);
                                    intent.putExtra("eventsList", eventsList);
                                    startActivity(intent);
                                    EventDetailsActivity.this.finish();
                                } else if (response.compareTo("-1") == 0) {
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
                String lastName;
                String firstName;

                String gName = guestName.getText().toString().trim();
                if(gName.isEmpty()){
                    lastName = playerSurname;
                    firstName = playerName;
                }
                else {
                    firstName = gName;
                    lastName = "("+playerName+"'s Guest)";
                }
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("position", spinnerPosition.getSelectedItem().toString().trim());

                final String reservedName = firstName;

                DatabaseHandler.addRowEntryToSheet(EventDetailsActivity.this, event.get("key"), params, new DatabaseHandler.onResponseListener() {
                    @Override
                    public void processResponse(String response) {
                        if(response.compareTo("0") == 0)
                            Toast.makeText(EventDetailsActivity.this, "Reservation for "+reservedName+" successful", Toast.LENGTH_LONG).show();
                        else if (response.compareTo("-1") == 0){
                            Toast.makeText(EventDetailsActivity.this, "You've already made a reservation for this position", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(EventDetailsActivity.this, response, Toast.LENGTH_LONG).show();
                        }

                    }
                });

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
}
