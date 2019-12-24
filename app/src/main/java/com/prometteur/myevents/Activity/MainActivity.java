package com.prometteur.myevents.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prometteur.myevents.Adapters.EventAdapter;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Event;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements    SwipeRefreshLayout.OnRefreshListener ,NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fabAddNewEvent;
    String userName = "",mobile="",
    nameRealname="",
            userPhoto=""   ;
    String TAG = MainActivity.class.getSimpleName();
    ArrayList<EventClassFirebase> arrayMainEvents = new ArrayList<>();
    ArrayList<EventClassFirebase> mainSendArrayMainEvents = new ArrayList<>();
    EditText edtSearch;
    LinearLayout llSearch, llTitle, llBtnSearch;
    Context context;
    SpotsDialog spotsDialog;
    //Navigation panel
    Toolbar toolbar;
    //back to exit
    boolean doubleBackToExitPressedOnce = false;
    private List<Event> eventList = new ArrayList<>();
    private RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private EventAdapter mAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    SharedPreferences preferences;

    ImageView imgDrawerProfile;
    TextView txtDrawerName,txtDrawerUserName;
    NavigationView navigationView;
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams attributes=getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode=WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;*/
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



        context = this;
        spotsDialog = new SpotsDialog(this);
        toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.mipmap.ic_drawer_icon);

        /*toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.four_dots, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
*/
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        initValues();
        onClicks();

    }

    private void initValues() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("events");

        FirebaseMessaging.getInstance().subscribeToTopic("AllEvents");


        edtSearch = findViewById(R.id.edtSearch);
        llTitle = findViewById(R.id.llTitle);
        llBtnSearch = findViewById(R.id.llBtnSearch);
        llSearch = findViewById(R.id.llSearch);


        llSearch.setVisibility(View.GONE);
        llTitle.setVisibility(View.VISIBLE);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    final ArrayList<EventClassFirebase> tempArrayToSend = new ArrayList<>();

                    for (int i = mainSendArrayMainEvents.size()-1; i >=0; i--) {
                        if (mainSendArrayMainEvents.get(i).getEventTitle().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                            tempArrayToSend.add(mainSendArrayMainEvents.get(i));
                        }
                    }/* for (int i = 0; i < mainSendArrayMainEvents.size(); i++) {
                        if (mainSendArrayMainEvents.get(i).getEventTitle().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                            tempArrayToSend.add(mainSendArrayMainEvents.get(i));
                        }
                    }*/
                    mAdapter = new EventAdapter(tempArrayToSend, MainActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);


                } else {
                    mainSendArrayMainEvents = new ArrayList<>();
                    for (int i =arrayMainEvents.size()-1; i >=0; i--) {
                        if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                            mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                        }
                    }/* for (int i = 0; i < arrayMainEvents.size(); i++) {
                        if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                            mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                        }
                    }*/

                    mAdapter = new EventAdapter(mainSendArrayMainEvents, MainActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    llSearch.setVisibility(View.GONE);
                    llTitle.setVisibility(View.VISIBLE);
                    llBtnSearch.setBackgroundColor(context.getResources().getColor(R.color.colorToolbar));

                }
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        fabAddNewEvent = findViewById(R.id.fabAddNewEvent);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue,
                R.color.colorBlue,
                R.color.colorBlue,
                R.color.colorYellow);



        preferences = PreferenceManager.getDefaultSharedPreferences(this);

         if(preferences.contains("userName"))
        userName = preferences.getString("userName", "");
        if(preferences.contains("mobile"))
            mobile       = preferences.getString("mobile", "");
        if(preferences.contains("nameRealname"))
            nameRealname = preferences.getString("nameRealname", "");
        if(preferences.contains("userPhoto"))
            userPhoto    = preferences.getString("userPhoto", "");



    }

    @Override
    public void onRefresh() {

        // Fetching data from server
        spotsDialog.show();
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainSendArrayMainEvents = new ArrayList<>();
                arrayMainEvents = new ArrayList<>();

                mSwipeRefreshLayout.setRefreshing(false);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {

                        EventClassFirebase eventClassFirebase = snapshot.getValue(EventClassFirebase.class);
                        eventClassFirebase.setEventKey(snapshot.getKey());
                        arrayMainEvents.add(eventClassFirebase);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                for (int i = arrayMainEvents.size()-1; i >=0; i--) {
                    if(arrayMainEvents.get(i).getEventInvitees()!=null) {
                        if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                            mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                        }
                    }
                }/*for (int i = 0; i < arrayMainEvents.size(); i++) {
                    if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                        mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                    }
                }*/

                mAdapter = new EventAdapter(mainSendArrayMainEvents, MainActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

                if (null != spotsDialog && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
                if (arrayMainEvents.size() == 0) {
                    Toast.makeText(context, "No any event found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }


        });
    }

    private void onClicks() {
        getHeaderLayout();  //navigationview header operation
        spotsDialog.show();
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainSendArrayMainEvents = new ArrayList<>();
                arrayMainEvents = new ArrayList<>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {

                        EventClassFirebase eventClassFirebase = snapshot.getValue(EventClassFirebase.class);
                        eventClassFirebase.setEventKey(snapshot.getKey());
                        arrayMainEvents.add(eventClassFirebase);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                for (int i = arrayMainEvents.size()-1; i >=0; i--) {
                    if(arrayMainEvents.get(i).getEventInvitees()!=null) {
                        if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                            mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                        }
                    }
                }/*for (int i = 0; i < arrayMainEvents.size(); i++) {
                    if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                        mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                    }
                }*/

                mAdapter = new EventAdapter(mainSendArrayMainEvents, MainActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

                if (null != spotsDialog && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
                if (arrayMainEvents.size() == 0) {
                    Toast.makeText(context, "No any event found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }


        });


        fabAddNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewEventActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        llBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llSearch.getVisibility() == View.GONE) {
                    llSearch.setVisibility(View.VISIBLE);
                    llTitle.setVisibility(View.GONE);
                    llBtnSearch.setBackgroundColor(context.getResources().getColor(R.color.colorToolbar));
                } else {
                    llSearch.setVisibility(View.GONE);
                    llTitle.setVisibility(View.VISIBLE);
                    llBtnSearch.setBackgroundColor(context.getResources().getColor(R.color.colorToolbar));
                    hideKeyboard(MainActivity.this);

                    mainSendArrayMainEvents = new ArrayList<>();

                    for (int i = arrayMainEvents.size()-1; i >=0 ; i--) {
                        if (arrayMainEvents.get(i).getEventInvitees().contains(userName) || arrayMainEvents.get(i).getEventCreatorUserName().contains(userName)) {
                            mainSendArrayMainEvents.add(arrayMainEvents.get(i));
                        }
                    }

                    mAdapter = new EventAdapter(mainSendArrayMainEvents, MainActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);


                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*case R.id.nav_main_menu:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;*/
            case R.id.nav_logout:
                preferences.edit().clear().commit();
                Intent intent = new Intent(MainActivity.this, EnterMobileNumberActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    public void getHeaderLayout()
    {
        View headerLayout =
                navigationView.getHeaderView(0);
        LinearLayout llBack = headerLayout.findViewById(R.id.llBack);
        llBack.setFocusable(true);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        imgDrawerProfile = headerLayout.findViewById(R.id.imgDrawerProfile);
        txtDrawerName = headerLayout.findViewById(R.id.txtDrawerName);
        txtDrawerUserName =headerLayout. findViewById(R.id.txtDrawerUserName);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences.contains("userName"))
            userName = preferences.getString("userName", "");
        if(preferences.contains("mobile"))
            mobile       = preferences.getString("mobile", "");
        if(preferences.contains("nameRealname"))
            nameRealname = preferences.getString("nameRealname", "");
        if(preferences.contains("userPhoto"))
            userPhoto    = preferences.getString("userPhoto", "");

        txtDrawerName.setText(nameRealname);
        txtDrawerUserName.setText(userName);

    }
}
