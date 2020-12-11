package com.example.vnight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;

import java.util.HashMap;
import java.util.Map;


public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    UserInfo userInfo;
    Context ctx;

//    @BindView(R.id.et_firstName)
    EditText editTextFirstName;
//    @BindView(R.id.et_lastName)
    EditText editTextLastName;
//    @BindView(R.id.et_batch)
    EditText editTextBatch;
//    @BindView(R.id.et_contactNum)
    EditText editTextContactNumber;
//    @BindView(R.id.et_password)
    EditText editTextPassword;
//    @BindView(R.id.et_passwordRetype)
    EditText editTextPasswordRetype;
//    @BindView(R.id.btn_submit)
    Button buttonSubmit;
    EditText editTextUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_info_actvity);
        ctx = this;
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx, "UserInfo", "UserInfo", UserInfo.class);
//        ButterKnife.bind(this);
        editTextFirstName = (EditText)findViewById(R.id.et_firstName);
        editTextLastName = (EditText)findViewById(R.id.et_lastName);
        editTextBatch = (EditText)findViewById(R.id.et_batch);
        editTextContactNumber = (EditText)findViewById(R.id.et_contactNum);
        editTextPassword = (EditText)findViewById(R.id.et_password);
        editTextPasswordRetype = (EditText)findViewById(R.id.et_passwordRetype);
        buttonSubmit = (Button)findViewById(R.id.btn_submitDraft);
        editTextUsername = (EditText)findViewById(R.id.et_username);
        buttonSubmit.setOnClickListener(this);


        editTextFirstName.setText(userInfo.getFirstName());
        editTextLastName.setText(userInfo.getLastName());
        editTextBatch.setText("" +userInfo.getBatch());
        editTextContactNumber.setText(userInfo.getContactNum());
        editTextUsername.setText(userInfo.getUsername());


    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            final String firstName = editTextFirstName.getText().toString().trim();
            final String lastName = editTextLastName.getText().toString().trim();
            final int batch =  Integer.parseInt(editTextBatch.getText().toString().trim());
            final String contactNum = editTextContactNumber.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();
            final String passwordRetype = editTextPasswordRetype.getText().toString().trim();
            final String username = editTextUsername.getText().toString().trim();

            if (username.isEmpty()){
                Toast.makeText(ctx,"username field must not be empty",Toast.LENGTH_LONG).show();
                return;
            }
            else if (firstName.isEmpty()){
                Toast.makeText(ctx,"First Name field must not be empty",Toast.LENGTH_LONG).show();
                return;
            }
            else if(lastName.isEmpty()){
                Toast.makeText(ctx,"Last Name field must not be empty",Toast.LENGTH_LONG).show();
                return;
            }
            else if(password.isEmpty()){
                Toast.makeText(ctx,"password field must not be empty",Toast.LENGTH_LONG).show();
                return;
            } else if(password.compareTo(passwordRetype) != 0){
                Toast.makeText(ctx,"Passwords do not match. Please check your password",Toast.LENGTH_LONG).show();
                return;
            }
            else {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                //params.put("action", "addItem");
                params.put("username", username);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("batch", ""+batch);
                params.put("contactNum", contactNum);
                params.put("password", password);
                DatabaseHandler.editRowFromSheetByID(ctx, "Items", userInfo.getEntryID(), params, new DatabaseHandler.onResponseListener() {
                    @Override
                    public void processResponse(String response) {
                        if(response.compareTo(DatabaseHandler.EditReturnCodes.EDIT_SUCCESS) == 0){
                            Toast.makeText(ctx,"Your profile has been updated.", Toast.LENGTH_LONG).show();
                            userInfo.setUsername(username);
                            userInfo.setFirstName(firstName);
                            userInfo.setLastName(lastName);
                            userInfo.setBatch(batch);
                            userInfo.setContactNum(contactNum);
                            SharedPreferenceHandler.saveObjectToSharedPreference(ctx,"UserInfo", "UserInfo", userInfo);
                            Intent intent = new Intent(EditUserInfoActivity.this, UserHomeActivity.class);
                            startActivity(intent);
                            EditUserInfoActivity.this.finish();
                        }
                        else if(response.compareTo(DatabaseHandler.EditReturnCodes.ENTRY_DOES_NOT_EXIST) == 0){
                            Toast.makeText(ctx,"Entry not found", Toast.LENGTH_LONG).show();
                        }
                        else if(response.compareTo(DatabaseHandler.EditReturnCodes.ENTRY_RENAME_DUPLICATE) == 0){
                            Toast.makeText(ctx,"username already exists", Toast.LENGTH_LONG).show();
                        }
                        else
                            {
                            Toast.makeText(ctx,response,Toast.LENGTH_LONG).show();
                        }
                    }
                });
               // Toast.makeText(EditUserInfoActivity.this, "Update User Info successful", Toast.LENGTH_LONG).show();

            }
        }

    }
}
