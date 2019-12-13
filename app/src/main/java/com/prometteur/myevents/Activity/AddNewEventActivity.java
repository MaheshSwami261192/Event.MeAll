package com.prometteur.myevents.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.prometteur.myevents.Adapters.InviteesAdapter;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Contact;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;
import com.prometteur.myevents.SingletonClasses.Invitees;
import com.prometteur.myevents.Utils.ButtonCustomFont;
import com.prometteur.myevents.Utils.HorizontalImagesList;
import com.prometteur.myevents.Utils.MediaFilePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import gun0912.tedbottompicker.TedRxBottomPicker;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddNewEventActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1212;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 210;
    public static ArrayList<Contact> arrNewList = new ArrayList<>();
    public static ArrayList<String> arrNewListForCheck = new ArrayList<>();
    public static ArrayList<Contact> arrSendTextMsg = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    final String TAG = "NOTIFICATION TAG";
    final int PERMISSION_REQUEST_CODE = 200;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA1TsXoPg:APA91bF9aQ5q1nBw4Zl-aDmr-2dQszyaiBTFzUx7Nixe5wcvdaCo2f8BcVYp_I-ulJsYhTk8MdMuTCj0uSWyrpNw0hOtqvsMExGngdfLjpDGDj6b8uX5eDMovwQKGj9SsPfP7CzJcf6O";
    final private String contentType = "application/json";
    RecyclerView rvInvitees;
    ImageView imgAddInvitees;
    SpotsDialog spotsDialog;
    Uri URI;
    EditText edtEventTitle, edtEventDate, edtEventStartTime, edtEventEndTime, edtEventAddress;
    ButtonCustomFont btnDone;
    LinearLayout llBack;
    String userName = "";
    String nameRealname = "";
    String userPhoto = "";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String NOTIFICATION_IDS;
    String TOPIC;
    ImageView imgAddImages;
    int from;
    Context mContext;
    String eventImages = "";
    File imgFile;
    Bitmap myBitmap;
    List<Bitmap> bitmapas = new ArrayList<>();
    List<String> filenameList = new ArrayList<>();
    List<File> filenameFiles = new ArrayList<>();
    String mainuser_id = "";
    String mainuser_session = "";
    RecyclerView imageToUpload;
    String phoneNumber, message;
    private ArrayList<Invitees> inviteesList = new ArrayList<>();
    private InviteesAdapter mAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private ViewGroup mSelectedImagesContainer;
    private List<Uri> selectedUriList = new ArrayList<>();
    private Disposable multiImageDisposable;
    private String filename;
    private RequestManager requestManager;
    private ArrayList<String> membersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_new_event_activity);
        mContext = this;
        initValues();
        onClicks();
        arrNewList.clear();
        arrNewListForCheck.clear();
    }

    private void initValues() {
        spotsDialog = new SpotsDialog(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("events");

        requestManager = Glide.with(this.mContext);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("userName", "");
        nameRealname = preferences.getString("nameRealname", "");
        userPhoto = preferences.getString("userPhoto", "");


        imageToUpload = findViewById(R.id.imgToUpload);

        rvInvitees = findViewById(R.id.rvInvitees);
        imgAddImages = findViewById(R.id.imgAddImages);
        imgAddInvitees = findViewById(R.id.imgAddInvitees);
        edtEventTitle = findViewById(R.id.edtEventTitle);
        edtEventDate = findViewById(R.id.edtEventDate);
        edtEventStartTime = findViewById(R.id.edtEventStartTime);
        edtEventEndTime = findViewById(R.id.edtEventEndTime);
        edtEventAddress = findViewById(R.id.edtEventAddress);
        btnDone = findViewById(R.id.btnDone);
        llBack = findViewById(R.id.llBack);


        mAdapter = new InviteesAdapter(inviteesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvInvitees.setLayoutManager(mLayoutManager);
        rvInvitees.setItemAnimator(new DefaultItemAnimator());
        rvInvitees.setAdapter(mAdapter);

        prepareInviteesData();
    }

    private void updateDate(EditText editText) {
        String myFormat = "dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddNewEventActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void onClicks() {
        selectedUriList = new ArrayList<>();
        imgAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] choice = {"Image", "Video"};


                AlertDialog.Builder alert = new AlertDialog.Builder(AddNewEventActivity.this);
                alert.setTitle("Upload");
                mSelectedImagesContainer = findViewById(R.id.selected_photos_container);

                alert.setSingleChoiceItems(choice, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choice[which] == "Image") {
                            from = 1;
                            dialog.dismiss();
                            if (checkPermission1()) {
                                setRxMultiShowButton(mSelectedImagesContainer);
                            } else {
                                requestPermission1();
                            }
                        } else if (choice[which] == "Video") {
                            from = 2;
                            dialog.dismiss();
                            if (checkPermission1()) {
                                setRxMultiShowButton(mSelectedImagesContainer);
                            } else {
                                requestPermission1();
                            }
                        }
                    }
                });
                alert.show();

            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewEventActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        imgAddInvitees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddNewEventActivity.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddNewEventActivity.this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(AddNewEventActivity.this,
                                new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                }

                Intent intent = new Intent(AddNewEventActivity.this, ContactListActivity.class);
                startActivity(intent);
            }
        });


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(edtEventDate);
            }
        };
        edtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNewEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        edtEventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hourString = "";
                        if (selectedHour < 12) {
                            hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        } else {
                            hourString = (selectedHour - 12) < 10 ? "0" + (selectedHour - 12) : "" + (selectedHour - 12);
                        }
                        String minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        String am_pm = (selectedHour < 12) ? "AM" : "PM";
                        String time = hourString + ":" + minuteString + " " + am_pm;

                        edtEventStartTime.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Event Start Time");
                mTimePicker.show();

            }
        });
        edtEventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hourString = "";
                        if (selectedHour < 12) {
                            hourString = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                        } else {
                            hourString = (selectedHour - 12) < 10 ? "0" + (selectedHour - 12) : "" + (selectedHour - 12);
                        }
                        String minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        String am_pm = (selectedHour < 12) ? "AM" : "PM";
                        String time = hourString + ":" + minuteString + " " + am_pm;

                        edtEventEndTime.setText(time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Event End Time");
                mTimePicker.show();
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<EventImageFirebase> arrayListEventImages = new ArrayList<>();
                 edtEventTitle.setError(null);
                edtEventDate.setError(null);
                edtEventStartTime.setError(null);
                  edtEventEndTime.setError(null);
                 edtEventAddress.setError(null);
                if (selectedUriList.size() > 0) {
                    String strEventTitle = edtEventTitle.getText().toString().trim();
                    String strEventDate = edtEventDate.getText().toString().trim();
                    String strEventStartTime = edtEventStartTime.getText().toString().trim();
                    String strEventEndTime = edtEventEndTime.getText().toString().trim();
                    String strEventAddress = edtEventAddress.getText().toString().trim();
                    if (!strEventTitle.equals("")) {
                        if (!strEventDate.equals("")) {
                            if (!strEventStartTime.equals("")) {
                                if (!strEventEndTime.equals("")) {
                                    if (!strEventAddress.equals("")) {
                                        spotsDialog.show();
                                        for (int i = 0; i < selectedUriList.size(); i++) {
                                            if (null != selectedUriList.get(i)) {

                                                Uri myUri = Uri.parse(selectedUriList.get(i).toString());

                                                final StorageReference mStorageReference = FirebaseStorage.getInstance()
                                                        .getReference()
                                                        .child(userName).child(myUri.getLastPathSegment());
                                                UploadTask uploadTask = mStorageReference.putFile(selectedUriList.get(i));
                                                int finalI = i;
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
                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                                            String eventCreateDate = sdf.format(new Date());

                                                            final String photo = task.getResult().toString();
                                                            EventImageFirebase eventImageFirebase = new EventImageFirebase();
                                                            eventImageFirebase.setImgPath(photo);
                                                            eventImageFirebase.setImgUploaderName(nameRealname);
                                                            eventImageFirebase.setImgUploaderUserName(userName);
                                                            eventImageFirebase.setImgUploaderPhoto(userPhoto);
                                                            eventImageFirebase.setImgUploadTime(eventCreateDate);
                                                            if (!arrayListEventImages.contains(eventImageFirebase)) {
                                                                arrayListEventImages.add(eventImageFirebase);
                                                            }

                                                            if (arrayListEventImages.size() == selectedUriList.size()) {

                                                                Gson gson = new GsonBuilder().create();
                                                                JsonArray myCustomArray = gson.toJsonTree(arrayListEventImages).getAsJsonArray();
                                                                eventImages = myCustomArray.toString();



                                                                String strEventInvitees = "";
                                                                String strEventSendMessage = "";

                                                                for (int i = 0; i < arrNewList.size(); i++) {
                                                                    if (null != arrNewList.get(i).getIsSelected())
                                                                        if (arrNewList.get(i).getIsSelected().equals("Y")) {
                                                                            membersList.add(arrNewList.get(i).getSubname());
                                                                            if (arrNewList.get(i).getUserName().equals("")) {
                                                                                strEventSendMessage = strEventSendMessage + ", " + arrNewList.get(i).getSubname();
                                                                            } else {
                                                                                strEventInvitees = strEventInvitees + ", " + arrNewList.get(i).getUserName();

                                                                            }
                                                                        }
                                                                }


                                                                List<String> items = Arrays.asList(strEventSendMessage.split("\\s*,\\s*"));

                                                                for (int i = 0; i < items.size(); i++) {
                                                                    if (!items.get(i).equals(""))
                                                                        sendSMS(items.get(i), "You are invited to join My Event. To download tap on below link" + " www.google.com");
                                                                }



                                                                createNewEvent(
                                                                        "",
                                                                        strEventTitle,
                                                                        arrayListEventImages,
                                                                        "",
                                                                        userPhoto,
                                                                        nameRealname,
                                                                        userName,
                                                                        eventCreateDate,
                                                                        strEventDate,
                                                                        strEventStartTime,
                                                                        strEventEndTime,
                                                                        strEventAddress,
                                                                        strEventInvitees,
                                                                        membersList);


                                                            }
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(mContext, "Failed To upload", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                    } else {
                                        edtEventAddress.setError("Enter event place/address.");
                                        edtEventAddress.requestFocus();
                                    }
                                } else {
                                    //edtEventEndTime.setError("Enter event end time.");
                                   // edtEventEndTime.requestFocus();
                                    Toast.makeText(mContext, "Select event end time.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //edtEventStartTime.setError("Enter event start time.");
                                //edtEventStartTime.requestFocus();
                                Toast.makeText(mContext, "Select event start time.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                           // edtEventDate.setError("Enter event date.");
                            //edtEventDate.requestFocus();
                            Toast.makeText(mContext, "Select event date.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Enter event title.", Toast.LENGTH_SHORT).show();
                        //edtEventTitle.setError("Enter event title.");
                        edtEventTitle.requestFocus();
                    }
                } else {

                    String strEventTitle = edtEventTitle.getText().toString().trim();
                    String strEventDate = edtEventDate.getText().toString().trim();
                    String strEventStartTime = edtEventStartTime.getText().toString().trim();
                    String strEventEndTime = edtEventEndTime.getText().toString().trim();
                    String strEventAddress = edtEventAddress.getText().toString().trim();
                    String strEventInvitees = "";
                    String strEventSendMessage = "";
                    if (!strEventTitle.equals("")) {
                        if (!strEventDate.equals("")) {
                            if (!strEventStartTime.equals("")) {
                                if (!strEventEndTime.equals("")) {
                                    if (!strEventAddress.equals("")) {
                                        spotsDialog.show();
                                        for (int i = 0; i < arrNewList.size(); i++) {
                                            if (null != arrNewList.get(i).getIsSelected())
                                                if (arrNewList.get(i).getIsSelected().equals("Y")) {

                                                    membersList.add(arrNewList.get(i).getSubname());

                                                    if (arrNewList.get(i).getUserName().equals("")) {
                                                        strEventSendMessage = strEventSendMessage + ", " + arrNewList.get(i).getSubname();
                                                    } else {
                                                        strEventInvitees = strEventInvitees + ", " + arrNewList.get(i).getUserName();

                                                    }
                                                }
                                        }


                                        List<String> items = Arrays.asList(strEventSendMessage.split("\\s*,\\s*"));

                                        for (int i = 0; i < items.size(); i++) {
                                            if (!items.get(i).equals(""))
                                                sendSMS(items.get(i), "You are invited to join My Event. To download tap on below link" + " www.google.com");
                                        }


                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                        String eventCreateDate = sdf.format(new Date());


                                        createNewEvent(
                                                "",
                                                strEventTitle,
                                                arrayListEventImages,
                                                "",
                                                userPhoto,
                                                nameRealname,
                                                userName,
                                                eventCreateDate,
                                                strEventDate,
                                                strEventStartTime,
                                                strEventEndTime,
                                                strEventAddress,
                                                strEventInvitees,membersList
                                        );


                                    } else {
                                        edtEventAddress.setError("Enter event place/address.");
                                        edtEventAddress.requestFocus();
                                    }
                                } else {
                                    //edtEventEndTime.setError("Enter event end time.");
                                    // edtEventEndTime.requestFocus();
                                    Toast.makeText(mContext, "Select event end time.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //edtEventStartTime.setError("Enter event start time.");
                                //edtEventStartTime.requestFocus();
                                Toast.makeText(mContext, "Select event start time.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // edtEventDate.setError("Enter event date.");
                            //edtEventDate.requestFocus();
                            Toast.makeText(mContext, "Select event date.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Enter event title.", Toast.LENGTH_SHORT).show();
                        //edtEventTitle.setError("Enter event title.");
                        edtEventTitle.requestFocus();
                    }
                }
            }
        });

    }

    private boolean checkPermission1() {
        if (ContextCompat.checkSelfPermission(AddNewEventActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission1() {

        ActivityCompat.requestPermissions(AddNewEventActivity.this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void setRxMultiShowButton(ViewGroup mSelectedImagesContainer) {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                if (from == 1) {
                    multiImageDisposable = TedRxBottomPicker.with(AddNewEventActivity.this)
                            .setPeekHeight(1600)
                            //.showVideoMedia()
                            .showTitle(false)
                            .setCompleteButtonText(mContext.getResources().getString(R.string.done))
                            .setEmptySelectionText(mContext.getResources().getString(R.string.noselect))
                            .setSelectMaxCount(8)
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList=uris;
                                showUriList(selectedUriList, mSelectedImagesContainer);
                            }, Throwable::printStackTrace);

                } else if (from == 2) {
                    multiImageDisposable = TedRxBottomPicker.with(AddNewEventActivity.this)
                            .setPeekHeight(1600)
                            .showVideoMedia()
                            .showTitle(false)
                            .setCompleteButtonText(mContext.getResources().getString(R.string.done))
                            .setEmptySelectionText(mContext.getResources().getString(R.string.noselect))
                            .setSelectMaxCount(8)
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList=uris;
                                showUriList(selectedUriList, mSelectedImagesContainer);
                            }, Throwable::printStackTrace);

                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.permissiondenied) + "\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        checkPermission(permissionlistener);


    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(mContext.getResources().getString(R.string.permissioninstruction))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION);
        /* int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
         */
        return result == PackageManager.PERMISSION_GRANTED /*&& result1 == PackageManager.PERMISSION_GRANTED*/;
    }

    private void showUriList(List<Uri> uriList, ViewGroup mSelectedImagesContainer) {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());

        selectedUriList = uriList;
        try {

            for (Uri uri : uriList) {

                View imageHolder = LayoutInflater.from(mContext).inflate(R.layout.image_item, null);
                ImageView thumbnail = imageHolder.findViewById(R.id.media_image);


                String url1 = uri.toString();
                if (url1.contains("https")) {
                    url1 = url1.replace("https", "http");
                }

                requestManager
                        .load(url1)
                        .apply(new RequestOptions().fitCenter())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // log exception
                                if (null != spotsDialog && spotsDialog.isShowing()) {
                                    spotsDialog.dismiss();
                                }
                                return false; // important to return false so the error placeholder can be placed
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (null != spotsDialog && spotsDialog.isShowing()) {
                                    spotsDialog.dismiss();
                                }
                                return false;
                            }
                        }).into(thumbnail);


                String path = uri.getPath(); // "/mnt/sdcard/FileName.mp3"
                String filePath = path;
                try {
                    File file = new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (filePath != null) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    selectedImage = BitmapFactory.decodeFile(filePath, options);
                    String fileNameSegments[] = filePath.split("/");
                    filename = fileNameSegments[fileNameSegments.length - 1];

                    imgFile = new File(filePath);


                    if (imgFile.exists()) {
                        imgFile = new File(filePath.toString());
                        if (imgFile.exists()) {
                            try {

                                BitmapFactory.Options options1 = new BitmapFactory.Options();
                                options.inSampleSize = 8;
                                Bitmap.createScaledBitmap(myBitmap, options.inSampleSize, options.inSampleSize, false);
                                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options1);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    bitmapas.add(selectedImage);
                    filenameList.add(filename);
                    filenameFiles.add(imgFile);
                    final String[] reviewImageType = {""};
                    try {

                        imageToUpload.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
                        HorizontalImagesList horizontalImagesList = new HorizontalImagesList(bitmapas, mContext);
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                        imageToUpload.setLayoutManager(horizontalLayoutManager);
                        imageToUpload.setAdapter(horizontalImagesList);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] columns = {MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE};

                    Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, null);
                    cursor.moveToFirst();

                    int pathColumnIndex = cursor.getColumnIndex(columns[0]);
                    int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);

                    String contentPath = cursor.getString(pathColumnIndex);
                    contentPath = MediaFilePath.getPath(mContext, uri);
                    cursor.close();

                    if (contentPath != null) {
                        Bitmap selectedImage = BitmapFactory.decodeFile(contentPath);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        selectedImage = BitmapFactory.decodeFile(contentPath, options);
                        String fileNameSegments[] = contentPath.split("/");
                        filename = fileNameSegments[fileNameSegments.length - 1];

                        imgFile = new File(contentPath);


                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(contentPath, MediaStore.Video.Thumbnails.MICRO_KIND);

                        bitmapas.add(bMap);
                        filenameList.add(filename);
                        filenameFiles.add(imgFile);
                        final String[] reviewImageType = {""};
                        try {

                            imageToUpload.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
                            HorizontalImagesList horizontalImagesList = new HorizontalImagesList(bitmapas, mContext);
                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                            imageToUpload.setLayoutManager(horizontalLayoutManager);
                            imageToUpload.setAdapter(horizontalImagesList);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //It's an image
                }

                mSelectedImagesContainer.addView(imageHolder);


                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AddNewEventActivity.this, mContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        if (!phoneNumber.equals("") && !phoneNumber.isEmpty()) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareInviteesData();
    }

    private void prepareInviteesData() {
        inviteesList.clear();
        Invitees invitees;
        for (int i = 0; i < arrNewList.size(); i++) {
            if (null != arrNewList.get(i).getIsSelected() && arrNewList.get(i).getIsSelected().equals("Y")) {
                if (null != arrNewList.get(i).getUserName() && !arrNewList.get(i).getUserName().equals("")) {
                    invitees = new Invitees(arrNewList.get(i).getName(), arrNewList.get(i).getUserName(),i);
                } else {
                    invitees = new Invitees(arrNewList.get(i).getName(), null,i);

                }
                inviteesList.add(invitees);
            }
        }

        mAdapter.notifyDataSetChanged();
    }


    private void createNewEvent(final String eventProfileImage,
                                final String eventTitle,
                                final ArrayList<EventImageFirebase> eventImages,
                                final String eventComments,
                                final String eventCreatorProfile,
                                final String eventCreatorName,
                                final String eventCreatorUserName,
                                final String eventCreateDate,
                                final String eventDate,
                                final String eventStartTime,
                                final String eventEndTime,
                                final String eventEventAddress,
                                final String eventInvitees, ArrayList<String> membersList) {

        EventClassFirebase eventClassFirebase = new EventClassFirebase(
                eventProfileImage,
                eventTitle,
                eventImages,
                eventComments,
                eventCreatorProfile,
                eventCreatorName,
                eventCreatorUserName,
                eventCreateDate,
                eventStartTime,
                eventDate,
                eventEventAddress,
                eventEndTime,
                eventInvitees,
                null,membersList
        );

        mFirebaseDatabase.push().setValue(eventClassFirebase).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (null != spotsDialog && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }


                TOPIC = "/topics/AllEvents"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "New Event Added";
                NOTIFICATION_MESSAGE = eventCreatorName + " added new event " + eventTitle;

                NOTIFICATION_IDS = eventInvitees;

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);
                    notifcationBody.put("messageids", NOTIFICATION_IDS);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage());
                }
                sendNotification(notification);


                Toast.makeText(AddNewEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddNewEventActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //addUserChangeListener();

    }
    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}


