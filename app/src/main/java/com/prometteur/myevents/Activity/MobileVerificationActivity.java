package com.prometteur.myevents.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.User;
import com.prometteur.myevents.Utils.ButtonCustomFont;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MobileVerificationActivity extends AppCompatActivity {
    ButtonCustomFont btnVerify;
    EditText edtOTPOne, edtOTPTwo, edtOTPThree, edtOTPFour, edtOTPFive, edtOTPSix, edtMobileNumner;
    TextView txtResendOTP;
    String mobile = "",countryCode="";
    boolean alreadyUser = false;
    User alreadyUserData;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;

    CountryCodePicker ccp;
    String strCountryCode="+91";
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Toast.makeText(MobileVerificationActivity.this, "" + code, Toast.LENGTH_SHORT).show();
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MobileVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(MobileVerificationActivity.this, "Verification code sent to " + edtMobileNumner.getText().toString(), Toast.LENGTH_SHORT).show();
            mVerificationId = s;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mobile_verification);
        initValues();
        onClicks();


    }

    @Override
    protected void onPause() {
        super.onPause();
        edit.putBoolean("restrictRedirectionToVerify",false);
        edit.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initValues() {
        btnVerify = findViewById(R.id.btnVerify);
        edtOTPOne = findViewById(R.id.edtOTPOne);
        edtOTPTwo = findViewById(R.id.edtOTPTwo);
        edtOTPThree = findViewById(R.id.edtOTPThree);
        edtOTPFour = findViewById(R.id.edtOTPFour);
        edtOTPFive = findViewById(R.id.edtOTPFive);
        edtOTPSix = findViewById(R.id.edtOTPSix);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        edtMobileNumner = findViewById(R.id.edtMobileNumner);
        btnVerify.setElevation(6f);
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        alreadyUser = intent.getBooleanExtra("alreadyUser", false);
        alreadyUserData = (User) intent.getSerializableExtra("alreadyUserData");
        countryCode =  intent.getStringExtra("countryCode");
        edtMobileNumner.setText(mobile);
        sendVerificationCode(mobile);
        ccp = (CountryCodePicker) findViewById(R.id.ccpVerify);
        ccp.setDefaultCountryUsingPhoneCode(Integer.parseInt(countryCode.replace("+","")));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit=preferences.edit();

        final int[] counter = {30};
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtResendOTP.setText(String.valueOf("Resend in " + counter[0] + " sec"));
                counter[0]--;
            }

            @Override
            public void onFinish() {
                txtResendOTP.setText(String.valueOf("Resend OTP"));

            }
        }.start();

        edtMobileNumner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10) {
                    if (!s.toString().equalsIgnoreCase(mobile)) {
                        txtResendOTP.setText(String.valueOf("Send OTP"));
                    } else {
                        txtResendOTP.setText(String.valueOf("Resend OTP"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edtOTPOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPTwo.requestFocus();
                    edtOTPTwo.setSelection(edtOTPTwo.getText().length());
                } else {
                    edtOTPOne.requestFocus();
                    edtOTPOne.setSelection(edtOTPOne.getText().length());
                }

            }
        });
        edtOTPTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPThree.requestFocus();
                    edtOTPThree.setSelection(edtOTPThree.getText().length());
                } else {
                    edtOTPOne.requestFocus();
                    edtOTPOne.setSelection(edtOTPOne.getText().length());
                }

            }
        });
        edtOTPThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPFour.requestFocus();
                    edtOTPFour.setSelection(edtOTPFour.getText().length());
                } else {
                    edtOTPTwo.requestFocus();
                    edtOTPTwo.setSelection(edtOTPTwo.getText().length());
                }

            }
        });
        edtOTPFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPFive.requestFocus();
                    edtOTPFive.setSelection(edtOTPFive.getText().length());
                } else {
                    edtOTPThree.requestFocus();
                    edtOTPThree.setSelection(edtOTPThree.getText().length());
                }

            }
        });
        edtOTPFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPSix.requestFocus();
                    edtOTPSix.setSelection(edtOTPSix.getText().length());
                } else {
                    edtOTPFour.requestFocus();
                    edtOTPFour.setSelection(edtOTPFour.getText().length());
                }

            }
        });

        edtOTPSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    edtOTPSix.requestFocus();
                    edtOTPSix.setSelection(edtOTPSix.getText().length());
                } else {
                    edtOTPFive.requestFocus();
                    edtOTPFive.setSelection(edtOTPFive.getText().length());
                }

            }
        });


    }

    private void onClicks() {
        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtResendOTP.getText().toString().trim().equals("Resend OTP")) {
                    sendVerificationCode(mobile);
                    final int[] counter = {30};
                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            txtResendOTP.setText(String.valueOf("Resend OTP in " + counter[0] + " sec"));
                            counter[0]--;
                        }

                        @Override
                        public void onFinish() {
                            txtResendOTP.setText(String.valueOf("Resend OTP"));


                        }
                    }.start();
                }else if(txtResendOTP.getText().toString().trim().equals("Send OTP"))
                {
                    mobile=edtMobileNumner.getText().toString();
                    sendVerificationCode(mobile);
                    final int[] counter = {30};
                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            txtResendOTP.setText(String.valueOf("Resend OTP in " + counter[0] + " sec"));
                            counter[0]--;
                        }

                        @Override
                        public void onFinish() {
                            txtResendOTP.setText(String.valueOf("Resend OTP"));


                        }
                    }.start();
                }
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String code = edtOTPOne.getText().toString().trim()
                        + edtOTPTwo.getText().toString().trim()
                        + edtOTPThree.getText().toString().trim()
                        + edtOTPFour.getText().toString().trim()
                        + edtOTPFive.getText().toString().trim()
                        + edtOTPSix.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    edtOTPSix.setError("Enter valid code");
                    edtOTPSix.requestFocus();
                    return;
                } else {
                   /* Intent intent = new Intent(MobileVerificationActivity.this, UserDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);*/
                    verifyVerificationCode(code);

                }

            }
        });


    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void verifyVerificationCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.enter_valid_verification_code), Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MobileVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MobileVerificationActivity.this, "Mobile number verified successfully", Toast.LENGTH_SHORT).show();
                            edtMobileNumner.setText("");
                            if (alreadyUser) {  //if user is already registered
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MobileVerificationActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("userName", alreadyUserData.getUserName());
                                editor.putString("mobile", alreadyUserData.getMobileNumber());
                                editor.putString("nameRealname", alreadyUserData.getFirstName() + " " + alreadyUserData.getLastName());
                                editor.putString("userPhoto", alreadyUserData.getPhoto());
                                editor.putBoolean("restrictRedirection",true);

                                editor.apply();


                                Intent intent = new Intent(MobileVerificationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {   //if user is new


                                Intent intent = new Intent(MobileVerificationActivity.this, UserDetailsActivity.class);
                               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("mobile", mobile);
                                startActivity(intent);
                                finish();
                            }

                        } else {

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = getResources().getString(R.string.enter_valid_verification_code);
                            }

                            Toast.makeText(MobileVerificationActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(MobileVerificationActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}
