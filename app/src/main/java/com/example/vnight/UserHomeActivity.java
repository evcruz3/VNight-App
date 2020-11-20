package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView welcomeText;
    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;
    Button buttonLogOut, buttonReserveSlot, buttonSeeReservedPlayers;
    String playerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_activity);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();
        buttonLogOut = (Button)findViewById(R.id.btn_logOut);
        buttonLogOut.setOnClickListener(this);
        buttonReserveSlot = (Button)findViewById(R.id.btn_reserveSlot);
        buttonReserveSlot.setOnClickListener(this);
        buttonSeeReservedPlayers = (Button)findViewById(R.id.btn_list_reserved);
        buttonSeeReservedPlayers.setOnClickListener(this);

        String username = sp.getString("username", "");
        playerName = sp.getString("firstName", "");

        welcomeText = (TextView)findViewById(R.id.textView2);
        welcomeText.setText("Welcome, "+username);

    }
    @Override
    public void onClick(View v){
        if(v == buttonLogOut){
            sp_editor.putBoolean("logged", false).apply();
            sp_editor.putString("username", "").apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            UserHomeActivity.this.finish();
        }

        if(v == buttonReserveSlot){
//            Intent intent = new Intent(getApplicationContext(), ReservationForm.class);
//            startActivity(intent);
            openDialogBox();
        }

        if(v == buttonSeeReservedPlayers){
            Intent intent = new Intent(getApplicationContext(), ListReservedPlayers.class);
            startActivity(intent);
        }
    }

    private void openDialogBox(){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflater.inflate(R.layout.reservation_form_dialog,null);
        final AlertDialog alertDialog = new AlertDialog.Builder(UserHomeActivity.this).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Please select a position");

        final LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.reserveDialog);
        final Spinner spinnerPosition = (Spinner)view.findViewById(R.id.dialog_spinnerPosition);
        final Switch switchGuest = (Switch)view.findViewById(R.id.dialog_switch1);

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
                String name;
                if(switchGuest.isChecked()){
                    name = playerName + " (G)";
                }
                else {
                    name = playerName;
                }
                params.put("playerName", name);
                params.put("position", spinnerPosition.getSelectedItem().toString().trim());

                final String reservedName = name;
                DatabaseHandler.addRowEntryToSheet(UserHomeActivity.this, "reservationList", params, new DatabaseHandler.onResponseListener() {
                    @Override
                    public void processResponse(String response) {
                        Toast.makeText(UserHomeActivity.this, "Reservation for "+reservedName+" successful", Toast.LENGTH_LONG).show();
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

    private void   addItemToSheet(final String name, final String positionSelected) {

        final ProgressDialog loading = ProgressDialog.show(this,"Reserving...","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=reserveSlot",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(UserHomeActivity.this,response,Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
//                        startActivity(intent);
//                        UserHomeActivity.this.finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
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
