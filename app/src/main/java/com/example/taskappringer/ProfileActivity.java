package com.example.taskappringer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {
    Button registerButton;
    EditText FirstName;
    EditText LastName;

    EditText Dateofbirth;
    EditText pincode;
    String gender1;
    String pinCode;
    Calendar myCalendar;
    FirebaseDatabase firebaseDatabase;


    DatabaseReference databaseReference;


    Model model;


    private RequestQueue mRequestQueue;


    private FirebaseAuth mAuth;



    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(0xffffffff);
            ((TextView) parent.getChildAt(0)).setPadding(50, 0, 0, 0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asa);
        mRequestQueue = Volley.newRequestQueue(ProfileActivity.this);



        mAuth = FirebaseAuth.getInstance();

        FirstName = (EditText) findViewById(R.id.etIme);
        LastName = (EditText) findViewById(R.id.etPriimek);
        Dateofbirth = (EditText) findViewById(R.id.etUsernameL);

        pincode = (EditText) findViewById(R.id.etRepeatPasswordL);

        registerButton = (Button) findViewById(R.id.bRegisterL);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Model");

        // initializing our object
        // class variable.
        model = new Model();

        String[] genders = {"Male", "Female", "Other"};
        final Spinner gender;
        gender = findViewById(R.id.etPasswordL);

        ArrayAdapter<String> adapter_gender = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genders);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter_gender);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                ((TextView) arg0.getChildAt(0)).setTextColor(Color.BLACK);
                gender1 =(String) gender.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pinCode = pincode.getText().toString().trim();


                if (TextUtils.isEmpty(pinCode)) {

                    Toast.makeText(ProfileActivity.this, "Please enter valid pin code", Toast.LENGTH_SHORT).show();
                } else {

                    getDataFromPinCode(pinCode);
                }
            }
        });

        myCalendar = Calendar.getInstance();
        Dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 0); // the age you want to limit
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "-" + String.valueOf(monthOfYear + 1)
                                + "-" + String.valueOf(year);
                        Dateofbirth.setText(date);
                    }
                }, yy, mm, dd);


                datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePicker.show();


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String firstname = FirstName.getText().toString();
                String lastname = LastName.getText().toString();
                String dateofbirth = Dateofbirth.getText().toString();
                String gender2 = gender1;
                String pincode1 = pincode.getText().toString();
                addDatatoFirebase(firstname, lastname,dateofbirth,gender2,pincode1);

            }
        });

    }

    private void getDataFromPinCode(String pinCode) {


        mRequestQueue.getCache().clear();


        String url = "http://www.postalpincode.in/api/pincode/" + pinCode;


        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray postOfficeArray = response.getJSONArray("PostOffice");
                    if (response.getString("Status").equals("Error")) {

                        Toast.makeText(ProfileActivity.this, "Pin code is not valid1.", Toast.LENGTH_SHORT).show();
                    } else {

                        JSONObject obj = postOfficeArray.getJSONObject(0);


                        String district = obj.getString("District");
                        String state = obj.getString("State");
                        String country = obj.getString("Country");


                        pincode.setText("District is : " + district + "\n" + "State : "
                                + state + "\n" + "Country : " + country);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Pin code is not valid2.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("hello", "Registration Error: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Pin code is not valid3.", Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(objectRequest);
    }

    private void addDatatoFirebase(String firstname, String lastname, String dateofbirth, String gender, String pincode) {
        // below 3 lines of code is used to set
        // data in our object class.
        model.setFirstname(firstname);
        model.setLastname(lastname);
        model.setDateofbirth(dateofbirth);
        model.setGender(gender);
        model.setPincode(pincode);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                databaseReference.setValue(model);


                Toast.makeText(ProfileActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }








    }