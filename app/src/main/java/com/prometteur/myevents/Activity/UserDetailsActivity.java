package com.prometteur.myevents.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.prometteur.myevents.SingletonClasses.User;
import com.prometteur.myevents.Utils.ButtonCustomFont;
import com.prometteur.myevents.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class UserDetailsActivity extends AppCompatActivity {
    EditText edtUserName, edtFirstName, edtLastName;
    ButtonCustomFont btnSubmit;


    private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    String mobile = "";

    CircleImageView imgPickGallary, imgProfile, imgPickCamera;


    private static int IMG_RESULT = 1;
    String ImageDecode;
    Intent intent;
    String[] FILE;
    Uri URI;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    SpotsDialog spotsDialog;
    String userName="";
    boolean userNameVerified=false;
    FirebaseUser currentFirebaseUser;

    SharedPreferences preferences ;
            SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_details);
        checkAndRequestPermissions();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        initValues();
        onClicks();


    }

    @Override
    protected void onResume() {
        super.onResume();
        editor.putBoolean("restrictRedirectionToVerify",true);
        editor.apply();
    }

    public void initValues() {
        spotsDialog = new SpotsDialog(UserDetailsActivity.this);
        edtUserName = findViewById(R.id.edtUserName);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPickGallary = findViewById(R.id.imgPickGallary);
        imgProfile = findViewById(R.id.imgProfile);
        imgPickCamera = findViewById(R.id.imgPickCamera);

        Intent intent1 = getIntent();
        mobile = intent1.getStringExtra("mobile");
        preferences = PreferenceManager.getDefaultSharedPreferences(UserDetailsActivity.this);
        editor = preferences.edit();
        editor.putBoolean("restrictRedirectionToVerify",true);
        editor.apply();
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() > 0 )
                        {
                            final int min = 0;
                            final int max = 1000;
                            final int random = new Random().nextInt((max - min) + 1) + min;

                            final String key=s.toString() + edtLastName.getText().toString().substring(0, edtLastName.getText().toString().length())+random;

                            mFirebaseDatabase.orderByKey().equalTo(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()) {
                                        edtUserName.setError("User nameRealname already taken");
                                        edtUserName.setText(key);
                                        userNameVerified=false;
                                    } else {
                                        edtUserName.setText(key);
                                        userNameVerified=true;
                                        edtUserName.setError(null);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });


                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() > 0 )
                        {
                            final int min = 0;
                            final int max = 1000;
                            final int random = new Random().nextInt((max - min) + 1) + min;

                            final String key=edtFirstName.getText().toString() + s.toString().substring(0, s.toString().length())+random;
                            mFirebaseDatabase.orderByKey().equalTo(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()) {
                                        userNameVerified=false;
                                        edtUserName.setText(key);
                                        edtUserName.setError("User nameRealname already taken");
                                    } else {
                                        userNameVerified=true;
                                        edtUserName.setText(key);
                                        edtUserName.setError(null);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });


                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("users");

    }


    @Override
    protected void onPause() {
        super.onPause();
        edtUserName.setError(null);//removes error
        edtUserName.clearFocus();
    }

    private void onClicks() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String firstName = edtFirstName.getText().toString();
                String lastName = edtLastName.getText().toString();
                userName= edtUserName.getText().toString().trim();
                String mobileNumber = mobile;

                if (!firstName.equals("")) {
                    if (!lastName.equals("")) {
                    if (!userName.equals("") && userNameVerified) {

                        editor.putBoolean("restrictRedirection",true);
                        editor.apply();
                            createUser(firstName, lastName, userName, mobileNumber);

                    } else {
                        edtUserName.setError("User nameRealname already taken");
                        Toast.makeText(UserDetailsActivity.this, "User nameRealname already taken", Toast.LENGTH_SHORT).show();
                    }
                    } else {
                        edtLastName.setError("Enter Last Name");
                    }
                } else {
                    edtFirstName.setError("Enter First Name");
                }
            }
        });


        imgPickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        imgPickGallary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, IMG_RESULT);
            }
        });


    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int contact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int sendSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int phoneReadState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (phoneReadState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
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
        if (sendSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                    && null != data) {


                URI = data.getData();
                FILE = new String[]{MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                ImageDecode = cursor.getString(columnIndex);
                cursor.close();

                imgProfile.setImageBitmap(BitmapFactory
                        .decodeFile(ImageDecode));

            } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(photo);

                URI = getImageUri(UserDetailsActivity.this, photo);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(final String firstName, final String lastName, final String userName, final String mobileNumber) {

        spotsDialog.show();


        final String userAuthKey =currentFirebaseUser.getUid();


        if (null != URI) {

            final StorageReference mStorageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(userName);

            UploadTask uploadTask = mStorageReference.putFile(URI);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return mStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final String photo = task.getResult().toString();
                        final User user = new User(firstName, lastName, mobileNumber, photo,userName,userAuthKey);
                        mFirebaseDatabase.child(userName).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (null != spotsDialog && spotsDialog.isShowing()) {
                                    spotsDialog.dismiss();
                                }


                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserDetailsActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("userName",userName);
                                editor.putString("mobile",mobileNumber);
                                editor.putString("nameRealname",firstName+" "+lastName);
                                editor.putString("userPhoto",photo);
                                editor.putBoolean("restrictRedirectionToVerify",false);
                                editor.apply();




                                Toast.makeText(UserDetailsActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                edtUserName.setText("");
                                edtFirstName.setText("");
                                edtLastName.setText("");

                            }
                        });
                    }
                }
            });
        }else {

            final User user = new User(firstName, lastName, mobileNumber, "",userName,userAuthKey);



            mFirebaseDatabase.child(userName).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (null != spotsDialog && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserDetailsActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userName",userName);
                    editor.putString("mobile",mobileNumber);
                    editor.putString("nameRealname",firstName+" "+lastName);
                    editor.putString("userPhoto","");
                    editor.apply();



                    Toast.makeText(UserDetailsActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    edtUserName.setText("");
                    edtFirstName.setText("");
                    edtLastName.setText("");

                }
            });
        }


        //addUserChangeListener();

    }


    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(edtUserName.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.firstName + ", " + user.lastName);



                Log.e(TAG, "" + user.firstName + ", " + user.lastName);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }


        });
    }

    private void updateUser(String name, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userName).child("nameRealname").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userName).child("email").setValue(email);
    }
}
