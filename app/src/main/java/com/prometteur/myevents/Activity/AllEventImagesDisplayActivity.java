package com.prometteur.myevents.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.prometteur.myevents.Adapters.AllEventImagesAdapter;
import com.prometteur.myevents.Adapters.CommentsAdapter;
import com.prometteur.myevents.Adapters.EventPhotoUserWiseAdapter;
import com.prometteur.myevents.R;
import com.prometteur.myevents.RecyclerItemTouchHelper;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventCommentFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import gun0912.tedbottompicker.TedRxBottomPicker;
import io.reactivex.disposables.Disposable;

public class AllEventImagesDisplayActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 210;
    final int PERMISSION_REQUEST_CODE = 200;
    RecyclerView rvEventAllImages, rvUserwiseImages, rvComments;
    EventClassFirebase clickedEvent;
    String eventName = "";
    LinearLayout llBack, closeComment, cvSwipingLayout;
    TextView txtEventName;
    ArrayList<EventImageFirebase> imageList = new ArrayList<>();
    CardView cvEventPhotoDetail, cvAddComment;
    AllEventImagesAdapter mAdapter;
    CircleImageView imgEventCreatorProfileCmt, imgEventCreatorProfileDes;
    TextView eventCreatorNameCmt, eventCreatorNameDes, eventDateTimeDes, txtImgTitle;
    ImageView eventSelectedImage, /*imgBtnSend, */
            imgCloseCommentSheet,imgCloseDialog;
    EditText edtCommentCmt;
    ImageView imgSwitchGallaryToTimeline, imgDetailsOfImage;
    Context mContext;
    boolean showingGallery = false;
    //add images to event
    ArrayList<EventCommentFirebase> commentList = new ArrayList<>();
    FloatingActionButton fabAddEventPhotos;
    SharedPreferences preferences;
    String nameRealname = "";
    String userName = "";
    String userPhoto = "";
    int from;
    //For alpha comment layout
    LinearLayout linAlphaBlock;
    boolean bottomsheetIsOpen = true;
    SpotsDialog spotsDialog;
    ImageView commentSent;
    int SelectedImagePosition = 0;
    EditText edtCommentDes;
    private ViewGroup mSelectedImagesContainer;
    private Disposable multiImageDisposable;
    private List<Uri> selectedUriList = new ArrayList<>();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_all_event_images_display);
        mContext = this;
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("events");
        spotsDialog = new SpotsDialog(this);

        initValues();

        onClicks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initValues();
        linAlphaBlock.setAlpha(1.0f);
    }

    private void initValues() {

        preferences = PreferenceManager.getDefaultSharedPreferences(AllEventImagesDisplayActivity.this);
        nameRealname = preferences.getString("nameRealname", "");
        userName = preferences.getString("userName", "");
        userPhoto = preferences.getString("userPhoto", "");


        fabAddEventPhotos = findViewById(R.id.fabAddEventPhotos);
        rvComments = findViewById(R.id.rvComments);
        imgDetailsOfImage = findViewById(R.id.imgDetailsOfImage);
        imgDetailsOfImage.setOnClickListener(null);



        imgSwitchGallaryToTimeline = findViewById(R.id.imgSwitchGallaryToTimeline);
        imgEventCreatorProfileCmt = findViewById(R.id.imgEventCreatorProfileCmt);
        imgEventCreatorProfileDes = findViewById(R.id.imgEventCreatorProfileDes);
        eventCreatorNameDes = findViewById(R.id.eventCreatorNameDes);
        eventDateTimeDes = findViewById(R.id.eventDateTimeDes);
        txtImgTitle = findViewById(R.id.txtImgTitle);
        eventCreatorNameCmt = findViewById(R.id.eventCreatorNameCmt);
        eventSelectedImage = findViewById(R.id.eventSelectedImage);
        // imgBtnSend = findViewById(R.id.imgBtnSend);
        imgCloseCommentSheet = findViewById(R.id.imgCloseCommentSheet);
        imgCloseDialog = findViewById(R.id.imgCloseDialog);
        edtCommentCmt = findViewById(R.id.edtCommentCmt);
        cvSwipingLayout = findViewById(R.id.cvSwipingLayout);
        cvEventPhotoDetail = findViewById(R.id.cvEventPhotoDetail);
        cvAddComment = findViewById(R.id.cvAddComment);
        closeComment = findViewById(R.id.closeComment);
        rvEventAllImages = findViewById(R.id.rvEventAllImages);
        rvUserwiseImages = findViewById(R.id.rvUserwiseImages);
        llBack = findViewById(R.id.llBack);
        txtEventName = findViewById(R.id.txtEventName);
        linAlphaBlock = findViewById(R.id.linAlphaBlock);
        commentSent = findViewById(R.id.commentSent);
        edtCommentDes = findViewById(R.id.edtCommentDes);


        txtEventName.setText(eventName);
        cvSwipingLayout.setVisibility(View.GONE);
        cvEventPhotoDetail.setVisibility(View.GONE);
        cvAddComment.setVisibility(View.GONE);
        rvUserwiseImages.setVisibility(View.GONE);


        Intent intent = getIntent();
        if (null != intent) {
            clickedEvent = (EventClassFirebase) getIntent().getSerializableExtra("clickedEvent");
            eventName = getIntent().getStringExtra("eventName");

            if (null != clickedEvent) {

                /*if (null != clickedEvent.getEventImages() && !clickedEvent.getEventImages().equals("")) {
                    String eventImages = clickedEvent.getEventImages();

                    Gson converter = new Gson();

                    Type type = new TypeToken<ArrayList<EventImageFirebase>>() {
                    }.getType();
                    imageList = converter.fromJson(eventImages, type);


                }*/
                imageList = clickedEvent.getEventImages();
            }
        }

        if (imageList != null) {
            if (imageList.size() > 0) {
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvEventAllImages.setLayoutManager(mLayoutManager);
                rvEventAllImages.setItemAnimator(new DefaultItemAnimator());
                ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvEventAllImages);


                mAdapter = new AllEventImagesAdapter(AllEventImagesDisplayActivity.this, imageList,
                        clickedEvent, new AllEventImagesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(EventImageFirebase item, int position) {
                        SelectedImagePosition = position;

                        String url1 = item.getImgUploaderPhoto();

                        if (url1 != null && !url1.isEmpty()) {
                            Glide.with(AllEventImagesDisplayActivity.this)
                                    .load(url1)
                                    .into(imgEventCreatorProfileCmt);
                        } else {
                            Glide.with(AllEventImagesDisplayActivity.this)
                                    .load(R.drawable.default_profile)
                                    .into(imgEventCreatorProfileCmt);
                        }


                        eventCreatorNameCmt.setText(nameRealname);

                        String urleventSelectedImage = item.getImgPath();

                        Glide.with(AllEventImagesDisplayActivity.this)
                                .load(urleventSelectedImage)
                                .into(eventSelectedImage);

                    /*    imgDetailsOfImage.setOnTouchListener(new View.OnTouchListener() {
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    if ("".equals("")) {
                                        openDialog(urleventSelectedImage);

                                    }

                                }
                                return true;
                            }
                        });

*/


                   /* Intent intent = new Intent(AllEventImagesDisplayActivity.this, EventPhotoDetailsActivity.class);
                    startActivity(intent);*/

                        String urlimgEventCreatorProfileDes = imageList.get(position).getImgUploaderPhoto();
                        if (!urlimgEventCreatorProfileDes.isEmpty()) {
                            Glide.with(AllEventImagesDisplayActivity.this)
                                    .load(urlimgEventCreatorProfileDes)
                                    .into(imgEventCreatorProfileDes);
                        } else {
                            Glide.with(AllEventImagesDisplayActivity.this)
                                    .load(R.drawable.default_profile)
                                    .into(imgEventCreatorProfileDes);
                        }
                        String urlimgDetailsOfImage = imageList.get(position).getImgPath();

                        Glide.with(AllEventImagesDisplayActivity.this)
                                .load(urlimgDetailsOfImage)
                                .into(imgDetailsOfImage);

                        linAlphaBlock.setAlpha(0.5f);
                        cvSwipingLayout.setVisibility(View.VISIBLE);
                        cvAddComment.setVisibility(View.GONE);
                        cvEventPhotoDetail.setVisibility(View.VISIBLE);
                        fabAddEventPhotos.hide();
                        eventCreatorNameDes.setText(imageList.get(position).getImgUploaderName());


                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                        Date date = null;
                        try {
                            date = format1.parse(imageList.get(position).getImgUploadTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String dateToSet = format2.format(date);


                        eventDateTimeDes.setText(dateToSet);
                        txtImgTitle.setText(imageList.get(position).getImgUploaderUserName());
// Todo: changes
                        List<EventCommentFirebase> commentFirebaseArrayList = new ArrayList<>();
                        CommentsAdapter commentsAdapter = new CommentsAdapter(commentFirebaseArrayList, AllEventImagesDisplayActivity.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllEventImagesDisplayActivity.this);
                        linearLayoutManager.setReverseLayout(true);
                        rvComments.setLayoutManager(linearLayoutManager);
                        rvComments.setItemAnimator(new DefaultItemAnimator());
                        rvComments.setAdapter(commentsAdapter);
                        String key = clickedEvent.getEventKey();
                        DatabaseReference commentsDatabace = mFirebaseDatabase.child(key).child("eventImages")
                                .child("" + SelectedImagePosition)
                                .child("imgComments");

                        commentsDatabace.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                commentFirebaseArrayList.clear();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    EventCommentFirebase value = data.getValue(EventCommentFirebase.class);
                                    commentFirebaseArrayList.add(value);
                                    if (imageList.get(position).getImgComments() != null) {
                                        imageList.get(position).getImgComments().put(data.getKey(), value);
                                    } else {
                                        Map<String, EventCommentFirebase> val = new HashMap<>();
                                        val.put(data.getKey(), value);
                                        imageList.get(position).setImgComments(val);
                                    }

                                }
                                Collections.reverse(commentFirebaseArrayList);
                                commentsAdapter.notifyDataSetChanged();
                                rvComments.scrollToPosition(commentFirebaseArrayList.size());
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                },
                        new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                rvEventAllImages.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }

        imgSwitchGallaryToTimeline.setImageResource(R.mipmap.ic_gallary);

        imgSwitchGallaryToTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showingGallery) {
                    rvEventAllImages.setVisibility(View.GONE);
                    rvUserwiseImages.setVisibility(View.VISIBLE);


                    imgSwitchGallaryToTimeline.setImageResource(R.drawable.ic_timeline);
                    showingGallery = true;


                    ArrayList<EventImageFirebase> imageListToSend = new ArrayList<>();

                    if (imageList != null) {
                        if (imageList.size() > 0) {
                            for (int i = 0; i < imageList.size(); i++) {
                                if (isPresentInList(imageListToSend, imageList.get(i).getImgUploaderName()) == -1) {
                                    EventImageFirebase eventImageFirebase = new EventImageFirebase();
                                    eventImageFirebase.setImgPath(imageList.get(i).getImgPath());
                                    eventImageFirebase.setImgUploaderName(imageList.get(i).getImgUploaderName());
                                    eventImageFirebase.setImgUploaderUserName(imageList.get(i).getImgUploaderUserName());
                                    eventImageFirebase.setImgUploaderPhoto(imageList.get(i).getImgUploaderPhoto());
                                    eventImageFirebase.setImgUploadTime(imageList.get(i).getImgUploadTime());
                                    imageListToSend.add(eventImageFirebase);
                                } else {
                                    int position = isPresentInList(imageListToSend, imageList.get(i).getImgUploaderName());
                                    imageListToSend.get(position).setImgPath(imageListToSend.get(position).getImgPath() + "," + imageList.get(i).getImgPath());

                                }
                            }
                        }
                    }

                    EventPhotoUserWiseAdapter mAdapter = new EventPhotoUserWiseAdapter(imageListToSend, AllEventImagesDisplayActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvUserwiseImages.setLayoutManager(mLayoutManager);
                    rvUserwiseImages.setItemAnimator(new DefaultItemAnimator());
                    rvUserwiseImages.setAdapter(mAdapter);


                } else {
                    rvEventAllImages.setVisibility(View.VISIBLE);
                    rvUserwiseImages.setVisibility(View.GONE);

                    imgSwitchGallaryToTimeline.setImageResource(R.mipmap.ic_gallary);
                    showingGallery = false;

                    if (imageList != null) {
                        if (imageList.size() > 0) {

                            mAdapter = new AllEventImagesAdapter(AllEventImagesDisplayActivity.this,
                                    imageList, clickedEvent,
                                    new AllEventImagesAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(EventImageFirebase item, int position) {
                                            SelectedImagePosition = position;

                                            String url1 = item.getImgUploaderPhoto();

                                            if (url1 != null && !url1.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(url1)
                                                        .into(imgEventCreatorProfileCmt);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileCmt);
                                            }


                                            eventCreatorNameCmt.setText(nameRealname);

                                            String urleventSelectedImage = item.getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urleventSelectedImage)
                                                    .into(eventSelectedImage);

                                         /*   imgDetailsOfImage.setOnTouchListener(new View.OnTouchListener() {
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                        if ("".equals("")) {
                                                            openDialog(urleventSelectedImage);

                                                        }
                                                    }
                                                    return true;
                                                }
                                            });

*/
                   /* Intent intent = new Intent(AllEventImagesDisplayActivity.this, EventPhotoDetailsActivity.class);
                    startActivity(intent);*/

                                            String urlimgEventCreatorProfileDes = imageList.get(position).getImgUploaderPhoto();
                                            if (!urlimgEventCreatorProfileDes.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(urlimgEventCreatorProfileDes)
                                                        .into(imgEventCreatorProfileDes);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileDes);
                                            }
                                            String urlimgDetailsOfImage = imageList.get(position).getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urlimgDetailsOfImage)
                                                    .into(imgDetailsOfImage);

                                            linAlphaBlock.setAlpha(0.5f);
                                            cvSwipingLayout.setVisibility(View.VISIBLE);
                                            cvAddComment.setVisibility(View.GONE);
                                            closeComment.setVisibility(View.GONE);
                                            cvEventPhotoDetail.setVisibility(View.VISIBLE);
                                            fabAddEventPhotos.hide();
                                            eventCreatorNameDes.setText(imageList.get(position).getImgUploaderName());


                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                            Date date = null;
                                            try {
                                                date = format1.parse(imageList.get(position).getImgUploadTime());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String dateToSet = format2.format(date);


                                            eventDateTimeDes.setText(dateToSet);
                                            txtImgTitle.setText(imageList.get(position).getImgUploaderUserName());
// Todo: changes
                                            List<EventCommentFirebase> commentFirebaseArrayList = new ArrayList<>();
                                            CommentsAdapter commentsAdapter = new CommentsAdapter(commentFirebaseArrayList, AllEventImagesDisplayActivity.this);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllEventImagesDisplayActivity.this);
                                            linearLayoutManager.setReverseLayout(true);
                                            rvComments.setLayoutManager(linearLayoutManager);
                                            rvComments.setItemAnimator(new DefaultItemAnimator());
                                            rvComments.setAdapter(commentsAdapter);
                                            String key = clickedEvent.getEventKey();
                                            DatabaseReference commentsDatabace = mFirebaseDatabase.child(key).child("eventImages")
                                                    .child("" + SelectedImagePosition)
                                                    .child("imgComments");

                                            commentsDatabace.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    commentFirebaseArrayList.clear();
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        EventCommentFirebase value = data.getValue(EventCommentFirebase.class);
                                                        commentFirebaseArrayList.add(value);
                                                        if (imageList.get(position).getImgComments() != null) {
                                                            imageList.get(position).getImgComments().put(data.getKey(), value);
                                                        } else {
                                                            Map<String, EventCommentFirebase> val = new HashMap<>();
                                                            val.put(data.getKey(), value);
                                                            imageList.get(position).setImgComments(val);
                                                        }
                                                    }
                                                    Collections.reverse(commentFirebaseArrayList);
                                                    commentsAdapter.notifyDataSetChanged();
                                                    mAdapter.notifyDataSetChanged();
                                                    rvComments.scrollToPosition(commentFirebaseArrayList.size());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    }, new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return false;
                                }
                            });
                            rvEventAllImages.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        this.bottomsheetIsOpen = cvSwipingLayout.getVisibility() == View.VISIBLE;

        if (!bottomsheetIsOpen) {
            super.onBackPressed();
            return;
        } else {
            cvSwipingLayout.setVisibility(View.GONE);
            closeComment.setVisibility(View.VISIBLE);
            linAlphaBlock.setAlpha(1.0f);
            fabAddEventPhotos.show();
        }
    }

    public int isPresentInList(ArrayList<EventImageFirebase> imageListToSend, String uploaderName) {
        int presentPosition = -1;
        for (int i = 0; i < imageListToSend.size(); i++) {
            String compareName = imageListToSend.get(i).getImgUploaderName();
            if (compareName.equals(uploaderName)) {
                presentPosition = i;
            } else {
                presentPosition = -1;

            }
        }

        return presentPosition;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        SelectedImagePosition = position;

        if (viewHolder instanceof AllEventImagesAdapter.MyViewHolder) {
            cvSwipingLayout.setVisibility(View.VISIBLE);
            cvAddComment.setVisibility(View.VISIBLE);
            fabAddEventPhotos.hide();
            cvEventPhotoDetail.setVisibility(View.GONE);
            closeComment.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();


            if (imageList != null) {
                EventImageFirebase item = imageList.get(position);
                String url1 = item.getImgUploaderPhoto();

                if (url1 != null && !url1.isEmpty()) {
                    Glide.with(AllEventImagesDisplayActivity.this)
                            .load(url1)
                            .into(imgEventCreatorProfileCmt);
                } else {
                    Glide.with(AllEventImagesDisplayActivity.this)
                            .load(R.drawable.default_profile)
                            .into(imgEventCreatorProfileCmt);
                }




                eventCreatorNameCmt.setText(item.getImgUploaderName());

                String urleventSelectedImage = item.getImgPath();

                Glide.with(AllEventImagesDisplayActivity.this)
                        .load(urleventSelectedImage)
                        .into(eventSelectedImage);



              /*  imgDetailsOfImage.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            if ("" .equals("")){

                                openDialog(urleventSelectedImage);
                            }
                        }
                        return true;
                    }
                });
*/

            }

            ArrayList<EventCommentFirebase> commentsArray = new ArrayList<>();

            edtCommentCmt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtCommentCmt.getRight() - edtCommentCmt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            String eventCreateDate = sdf.format(new Date());


                            if (edtCommentCmt.getText().toString().length() > 0) {
                                String text = edtCommentCmt.getText().toString();
                                edtCommentCmt.setText("");
                                //   EventImageFirebase old_Comments = (EventImageFirebase) clickedEvent.getEventImages().get(SelectedImagePosition).getComments();
                                EventCommentFirebase newComment = new EventCommentFirebase(text, nameRealname, userName, userPhoto, eventCreateDate);
                                String key = clickedEvent.getEventKey();
                                //   String imgKey =    clickedEvent.getEventImages().get(SelectedImagePosition).getImgKey();
                                mFirebaseDatabase.child(key).child("eventImages")
                                        .child("" + SelectedImagePosition)
                                        .child("imgComments").push().setValue(newComment)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Toast.makeText(mContext, "sucseesd ", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(mContext, "Enter Comment", Toast.LENGTH_SHORT).show();
                            }


                            return true;
                        }
                    }
                    return false;
                }
            });

        }


    }

    private void openDialog(String urleventSelectedImage) {
        Dialog settingsDialog = new Dialog(AllEventImagesDisplayActivity.this);

        if(!settingsDialog.isShowing())
        {
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                    , null));


            PhotoView imgDetailsOfImage=settingsDialog.findViewById(R.id.imgDetailsOfImage);
            ImageView imgDialogBtnClose=settingsDialog.findViewById(R.id.imgDialogBtnClose);
            Glide.with(AllEventImagesDisplayActivity.this)
                    .load(urleventSelectedImage)
                    .into(imgDetailsOfImage);

            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

            imgDialogBtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            settingsDialog.show();
        }

    }


    private void onClicks() {

        commentSent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String text = edtCommentDes.getText().toString();
                if (!text.isEmpty()) {
                    edtCommentDes.setText("");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String eventCreateDate = sdf.format(new Date());
                    //   EventImageFirebase old_Comments = (EventImageFirebase) clickedEvent.getEventImages().get(SelectedImagePosition).getComments();
                    EventCommentFirebase newComment = new EventCommentFirebase(text, nameRealname, userName, userPhoto, eventCreateDate);
                    String key = clickedEvent.getEventKey();
                    //   String imgKey =    clickedEvent.getEventImages().get(SelectedImagePosition).getImgKey();
                    mFirebaseDatabase.child(key).child("eventImages")
                            .child("" + SelectedImagePosition)
                            .child("imgComments").push().setValue(newComment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // Toast.makeText(mContext, "sucseesd ", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(mContext, "Enter Comment", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", clickedEvent);

                startActivity(new Intent(AllEventImagesDisplayActivity.this, EventDetailsActivity.class)
                        .putExtra("EVENT", bundle));
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


       /* imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AllEventImagesDisplayActivity.this, "Send Comment : " + edtComment.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
        imgCloseCommentSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linAlphaBlock.setAlpha(1.0f);
                cvSwipingLayout.setVisibility(View.GONE);

                fabAddEventPhotos.show();
            }
        });
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linAlphaBlock.setAlpha(1.0f);
                cvSwipingLayout.setVisibility(View.GONE);

                fabAddEventPhotos.show();
            }
        });
        closeComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                linAlphaBlock.setAlpha(1.0f);
                cvSwipingLayout.setVisibility(View.GONE);
                closeComment.setVisibility(View.VISIBLE);

                fabAddEventPhotos.show();
                return true;
            }
        });
       /* linAlphaBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linAlphaBlock.setAlpha(1.0f);
                cvSwipingLayout.setVisibility(View.GONE);
                fabAddEventPhotos.show();
            }
        });*/
        fabAddEventPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] choice = {"Image", "Video"};


                AlertDialog.Builder alert = new AlertDialog.Builder(AllEventImagesDisplayActivity.this);
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

    }

    private boolean checkPermission1() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(AllEventImagesDisplayActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission1() {

        ActivityCompat.requestPermissions(AllEventImagesDisplayActivity.this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }


    private void setRxMultiShowButton(ViewGroup mSelectedImagesContainer) {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectedUriList = new ArrayList<>();

                if (from == 1) {
                    multiImageDisposable = TedRxBottomPicker.with(AllEventImagesDisplayActivity.this)
                            .setPeekHeight(1600)
                            //.showVideoMedia()
                            .showTitle(false)
                            .setCompleteButtonText(mContext.getResources().getString(R.string.done))
                            .setEmptySelectionText(mContext.getResources().getString(R.string.noselect))
                            .setSelectMaxCount(8)
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList = uris;
                                addNewImagesToEvent();
//                                showUriList(uris, mSelectedImagesContainer);
                            }, Throwable::printStackTrace);

                } else if (from == 2) {
                    multiImageDisposable = TedRxBottomPicker.with(AllEventImagesDisplayActivity.this)
                            .setPeekHeight(1600)
                            .showVideoMedia()
                            .showTitle(false)
                            .setCompleteButtonText(mContext.getResources().getString(R.string.done))
                            .setEmptySelectionText(mContext.getResources().getString(R.string.noselect))
                            .setSelectMaxCount(8)
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList = uris;
                                addNewImagesToEvent();
//                                showUriList(uris, mSelectedImagesContainer);
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

    private void addNewImagesToEvent() {
        ArrayList<EventImageFirebase> arrayListEventImages = new ArrayList<>();

        if (selectedUriList.size() > 0) {
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


                                    addImagesToEvent(arrayListEventImages);


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
            Toast.makeText(mContext, "no data", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(mContext.getResources().getString(R.string.permissioninstruction))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    private void addImagesToEvent(final ArrayList<EventImageFirebase> eventImages) {


        ArrayList<EventImageFirebase> eventImagesOld = clickedEvent.getEventImages();
        if (eventImagesOld != null) {
            eventImagesOld.addAll(eventImages);
        } else if (eventImages.size() > 0) {
            eventImagesOld = new ArrayList<>();
            eventImagesOld.addAll(eventImages);
        }
        Map<String, Object> mapEventImages = new HashMap<>();

        mapEventImages.put("eventImages", eventImagesOld);

        DatabaseReference data = mFirebaseDatabase.child(clickedEvent.getEventKey());

        data.updateChildren(mapEventImages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                data.child("eventImages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<EventImageFirebase> eventImagesRefresh = new ArrayList<>();
                        if (imageList != null) {
                            imageList.clear();
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {

                                EventImageFirebase eventClassFirebase = snapshot.getValue(EventImageFirebase.class);
                                eventClassFirebase.setImgKey(snapshot.getKey());
                                eventImagesRefresh.add(eventClassFirebase);
                                if (imageList != null) {
                                    imageList.add(eventClassFirebase);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                            clickedEvent.setEventImages(imageList);
                        } else {
                            imageList = eventImagesRefresh;
                            clickedEvent.setEventImages(imageList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvEventAllImages.setLayoutManager(mLayoutManager);
                            rvEventAllImages.setItemAnimator(new DefaultItemAnimator());
                            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, AllEventImagesDisplayActivity.this);
                            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvEventAllImages);


                            mAdapter = new AllEventImagesAdapter(AllEventImagesDisplayActivity.this,
                                    imageList, clickedEvent,
                                    new AllEventImagesAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(EventImageFirebase item, int position) {
                                            SelectedImagePosition = position;

                                            String url1 = item.getImgUploaderPhoto();
                                            if (url1 != null && !url1.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(url1)
                                                        .into(imgEventCreatorProfileCmt);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileCmt);
                                            }


                                            eventCreatorNameCmt.setText(nameRealname);

                                            String urleventSelectedImage = item.getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urleventSelectedImage)
                                                    .into(eventSelectedImage);

                                          /*  imgDetailsOfImage.setOnTouchListener(new View.OnTouchListener() {
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                        if ("".equals("")) {
                                                            openDialog(urleventSelectedImage);

                                                        }
                                                    }
                                                    return true;
                                                }
                                            });*/



                   /* Intent intent = new Intent(AllEventImagesDisplayActivity.this, EventPhotoDetailsActivity.class);
                    startActivity(intent);*/

                                            String urlimgEventCreatorProfileDes = imageList.get(position).getImgUploaderPhoto();
                                            if (!urlimgEventCreatorProfileDes.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(urlimgEventCreatorProfileDes)
                                                        .into(imgEventCreatorProfileDes);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileDes);
                                            }
                                            String urlimgDetailsOfImage = imageList.get(position).getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urlimgDetailsOfImage)
                                                    .into(imgDetailsOfImage);

                                            linAlphaBlock.setAlpha(0.5f);
                                            cvSwipingLayout.setVisibility(View.VISIBLE);
                                            closeComment.setVisibility(View.GONE);
                                            cvAddComment.setVisibility(View.GONE);
                                            cvEventPhotoDetail.setVisibility(View.VISIBLE);
                                            fabAddEventPhotos.hide();
                                            eventCreatorNameDes.setText(imageList.get(position).getImgUploaderName());


                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                            Date date = null;
                                            try {
                                                date = format1.parse(imageList.get(position).getImgUploadTime());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String dateToSet = format2.format(date);


                                            eventDateTimeDes.setText(dateToSet);
                                            txtImgTitle.setText(imageList.get(position).getImgUploaderUserName());
                                            // Todo: changes
                                            List<EventCommentFirebase> commentFirebaseArrayList = new ArrayList<>();
                                            CommentsAdapter commentsAdapter = new CommentsAdapter(commentFirebaseArrayList, AllEventImagesDisplayActivity.this);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllEventImagesDisplayActivity.this);
                                            linearLayoutManager.setReverseLayout(true);
                                            rvComments.setLayoutManager(linearLayoutManager);
                                            rvComments.setItemAnimator(new DefaultItemAnimator());
                                            rvComments.setAdapter(commentsAdapter);
                                            String key = clickedEvent.getEventKey();
                                            DatabaseReference commentsDatabace = mFirebaseDatabase.child(key).child("eventImages")
                                                    .child("" + SelectedImagePosition)
                                                    .child("imgComments");

                                            commentsDatabace.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    commentFirebaseArrayList.clear();
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        EventCommentFirebase value = data.getValue(EventCommentFirebase.class);
                                                        commentFirebaseArrayList.add(value);
                                                        if (imageList.get(position).getImgComments() != null) {
                                                            imageList.get(position).getImgComments().put(data.getKey(), value);
                                                        } else {
                                                            Map<String, EventCommentFirebase> val = new HashMap<>();
                                                            val.put(data.getKey(), value);
                                                            imageList.get(position).setImgComments(val);
                                                        }
                                                    }
                                                    Collections.reverse(commentFirebaseArrayList);
                                                    commentsAdapter.notifyDataSetChanged();
                                                    mAdapter.notifyDataSetChanged();
                                                    rvComments.scrollToPosition(commentFirebaseArrayList.size());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    }, new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return false;
                                }
                            });
                            rvEventAllImages.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (null != spotsDialog && spotsDialog.isShowing()) {
                            spotsDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (null != spotsDialog && spotsDialog.isShowing()) {
                            spotsDialog.dismiss();
                        }
                    }
                });

                /*


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
                sendNotification(notification);*/


                Toast.makeText(AllEventImagesDisplayActivity.this, "Image added successfully", Toast.LENGTH_SHORT).show();


                if (!showingGallery) {
                    rvEventAllImages.setVisibility(View.VISIBLE);
                    rvUserwiseImages.setVisibility(View.GONE);

                    imgSwitchGallaryToTimeline.setImageResource(R.mipmap.ic_gallary);

                    if (imageList != null) {
                        if (imageList.size() > 0) {

                            mAdapter = new AllEventImagesAdapter(AllEventImagesDisplayActivity.this,
                                    imageList, clickedEvent,
                                    new AllEventImagesAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(EventImageFirebase item, int position) {
                                            SelectedImagePosition = position;

                                            String url1 = item.getImgUploaderPhoto();

                                            if (url1 != null && !url1.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(url1)
                                                        .into(imgEventCreatorProfileCmt);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileCmt);
                                            }


                                            eventCreatorNameCmt.setText(nameRealname);

                                            String urleventSelectedImage = item.getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urleventSelectedImage)
                                                    .into(eventSelectedImage);

                                         /*   imgDetailsOfImage.setOnTouchListener(new View.OnTouchListener() {
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                        if ("".equals("")) {
                                                            openDialog(urleventSelectedImage);

                                                        }
                                                    }
                                                    return true;
                                                }
                                            });

*/
                   /* Intent intent = new Intent(AllEventImagesDisplayActivity.this, EventPhotoDetailsActivity.class);
                    startActivity(intent);*/

                                            String urlimgEventCreatorProfileDes = imageList.get(position).getImgUploaderPhoto();
                                            if (!urlimgEventCreatorProfileDes.isEmpty()) {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(urlimgEventCreatorProfileDes)
                                                        .into(imgEventCreatorProfileDes);
                                            } else {
                                                Glide.with(AllEventImagesDisplayActivity.this)
                                                        .load(R.drawable.default_profile)
                                                        .into(imgEventCreatorProfileDes);
                                            }
                                            String urlimgDetailsOfImage = imageList.get(position).getImgPath();

                                            Glide.with(AllEventImagesDisplayActivity.this)
                                                    .load(urlimgDetailsOfImage)
                                                    .into(imgDetailsOfImage);

                                            linAlphaBlock.setAlpha(0.5f);
                                            cvSwipingLayout.setVisibility(View.VISIBLE);
                                            cvAddComment.setVisibility(View.GONE);
                                            closeComment.setVisibility(View.GONE);
                                            cvEventPhotoDetail.setVisibility(View.VISIBLE);
                                            fabAddEventPhotos.hide();
                                            eventCreatorNameDes.setText(imageList.get(position).getImgUploaderName());


                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                            Date date = null;
                                            try {
                                                date = format1.parse(imageList.get(position).getImgUploadTime());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String dateToSet = format2.format(date);


                                            eventDateTimeDes.setText(dateToSet);
                                            txtImgTitle.setText(imageList.get(position).getImgUploaderUserName());
// Todo: changes
                                            List<EventCommentFirebase> commentFirebaseArrayList = new ArrayList<>();
                                            CommentsAdapter commentsAdapter = new CommentsAdapter(commentFirebaseArrayList, AllEventImagesDisplayActivity.this);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllEventImagesDisplayActivity.this);
                                            linearLayoutManager.setReverseLayout(true);
                                            rvComments.setLayoutManager(linearLayoutManager);
                                            rvComments.setItemAnimator(new DefaultItemAnimator());
                                            rvComments.setAdapter(commentsAdapter);
                                            String key = clickedEvent.getEventKey();
                                            DatabaseReference commentsDatabace = mFirebaseDatabase.child(key).child("eventImages")
                                                    .child("" + SelectedImagePosition)
                                                    .child("imgComments");

                                            commentsDatabace.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    commentFirebaseArrayList.clear();
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        EventCommentFirebase value = data.getValue(EventCommentFirebase.class);
                                                        commentFirebaseArrayList.add(value);
                                                        if (imageList.get(position).getImgComments() != null) {
                                                            imageList.get(position).getImgComments().put(data.getKey(), value);
                                                        } else {
                                                            Map<String, EventCommentFirebase> val = new HashMap<>();
                                                            val.put(data.getKey(), value);
                                                            imageList.get(position).setImgComments(val);
                                                        }
                                                    }
                                                    Collections.reverse(commentFirebaseArrayList);
                                                    commentsAdapter.notifyDataSetChanged();
                                                    mAdapter.notifyDataSetChanged();
                                                    rvComments.scrollToPosition(commentFirebaseArrayList.size());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }
                                    }, new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return false;
                                }
                            });
                            rvEventAllImages.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    rvEventAllImages.setVisibility(View.GONE);
                    rvUserwiseImages.setVisibility(View.VISIBLE);


                    imgSwitchGallaryToTimeline.setImageResource(R.drawable.ic_timeline);


                    ArrayList<EventImageFirebase> imageListToSend = new ArrayList<>();

                    if (imageList != null) {
                        if (imageList.size() > 0) {
                            for (int i = 0; i < imageList.size(); i++) {
                                if (isPresentInList(imageListToSend, imageList.get(i).getImgUploaderName()) == -1) {
                                    EventImageFirebase eventImageFirebase = new EventImageFirebase();
                                    eventImageFirebase.setImgPath(imageList.get(i).getImgPath());
                                    eventImageFirebase.setImgUploaderName(imageList.get(i).getImgUploaderName());
                                    eventImageFirebase.setImgUploaderUserName(imageList.get(i).getImgUploaderUserName());
                                    eventImageFirebase.setImgUploaderPhoto(imageList.get(i).getImgUploaderPhoto());
                                    eventImageFirebase.setImgUploadTime(imageList.get(i).getImgUploadTime());
                                    imageListToSend.add(eventImageFirebase);
                                } else {
                                    int position = isPresentInList(imageListToSend, imageList.get(i).getImgUploaderName());
                                    imageListToSend.get(position).setImgPath(imageListToSend.get(position).getImgPath() + "," + imageList.get(i).getImgPath());

                                }
                            }
                        }
                    }

                    EventPhotoUserWiseAdapter mAdapter = new EventPhotoUserWiseAdapter(imageListToSend, AllEventImagesDisplayActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvUserwiseImages.setLayoutManager(mLayoutManager);
                    rvUserwiseImages.setItemAnimator(new DefaultItemAnimator());
                    rvUserwiseImages.setAdapter(mAdapter);





                }

                /*Intent intent = new Intent(AddNewEventActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/

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
}
