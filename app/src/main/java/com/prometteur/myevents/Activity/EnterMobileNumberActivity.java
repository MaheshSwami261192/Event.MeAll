package com.prometteur.myevents.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.myevents.Adapters.EventAdapter;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.User;
import com.prometteur.myevents.Utils.ButtonCustomFont;
import com.prometteur.myevents.R;
import com.prometteur.myevents.Utils.CommonMethods;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;


public class EnterMobileNumberActivity extends AppCompatActivity {

    ButtonCustomFont btnSendOTP;
    TextView optTextView;
    EditText edtMobileNumber;
    CountryCodePicker ccp;
String strCountryCode="+91";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String TAG = MainActivity.class.getSimpleName();
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.enter_mobile_number_acitivity);
        checkAndRequestPermissions();
        initValues();
        onClicks();

         preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = preferences.getString("userName", "");

         edit=preferences.edit();
        if (null != userName && !userName.equals("")) {
            edit.putBoolean("restrictRedirection",true);
            edit.apply();
            Intent intent=new Intent(EnterMobileNumberActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else
        {

           /* edit.putBoolean("restrictRedirectionToVerify",false);
            edit.putBoolean("restrictRedirection",false);
            edit.apply();*/
        }
    }

    private void initValues() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        optTextView = findViewById(R.id.optTextView);


        optTextView = findViewById(R.id.optTextView);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        edtMobileNumber = findViewById(R.id.edtMobileNumner);
        btnSendOTP.setElevation(6);
        String steps = "Please enter your mobile number to\nreceive";
        String title = " One Time Password";

        SpannableString ss1 = new SpannableString(title);
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
        optTextView.append(steps);
        optTextView.append(ss1);

    }

    private void onClicks() {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Toast.makeText(EnterMobileNumberActivity.this, "Updated " + selectedCountry.getIso(), Toast.LENGTH_SHORT).show();
                strCountryCode="+"+selectedCountry.getPhoneCode();
            }
        });
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CommonMethods commonMethods = new CommonMethods(EnterMobileNumberActivity.this);
                final ArrayList<User> arrayListUser = new ArrayList<>();
                final User[] alreadyUser = {new User()};
                mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            boolean isPresent = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                arrayListUser.add(user);
                                String mobileNumber = "";
                                if (null != user.getMobileNumber()) {
                                    mobileNumber = user.getMobileNumber();
                                }
                                if (mobileNumber.equals(edtMobileNumber.getText().toString().trim())) {
                                    isPresent = true;
                                    alreadyUser[0] = user;
                                }
                            }

                       /* for (int i = 0; i < arrayListUser.size(); i++) {

                        }*/
//                        if (!isPresent) {

                        if(!preferences.getBoolean("restrictRedirectionToVerify",false)) {
                            if (!preferences.getBoolean("restrictRedirection", false)) {
                                if (edtMobileNumber.getText().toString().trim().length() > 0
                                        && (commonMethods.isValidMobile(edtMobileNumber.getText().toString().trim()))
                                ) {
                                    edit.putBoolean("restrictRedirectionToVerify",true);
                                    edit.commit();
                                    Intent intent = new Intent(EnterMobileNumberActivity.this, MobileVerificationActivity.class);
                                    intent.putExtra("countryCode", ccp.getSelectedCountryCodeWithPlus());
                                    intent.putExtra("mobile", edtMobileNumber.getText().toString().trim());
                                    intent.putExtra("alreadyUser", isPresent);
                                    intent.putExtra("alreadyUserData", alreadyUser[0]);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    edtMobileNumber.setError("Enter valid mobile number.");
                                }
                       /* } else {
                            Toast.makeText(EnterMobileNumberActivity.this, "Mobile number already exist", Toast.LENGTH_SHORT).show();
                        }*/
                            } else {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read user", error.toException());
                    }


                });

            }
        });


    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int contact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (contact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
