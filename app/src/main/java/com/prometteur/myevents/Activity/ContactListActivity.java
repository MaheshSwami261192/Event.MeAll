package com.prometteur.myevents.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.myevents.Adapters.ContactListAdapter;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Contact;
import com.prometteur.myevents.SingletonClasses.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.prometteur.myevents.Activity.EventDetailsActivity.contactToInviteList;
import static com.prometteur.myevents.Activity.EventDetailsActivity.contactToInviteListForUnique;
import static com.prometteur.myevents.Activity.EventDetailsActivity.oldlistContact;
import static com.prometteur.myevents.Activity.EventDetailsActivity.oldlistContactForUniqe;

public class ContactListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {
    public static final int RequestPermissionCode = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    final ArrayList<User> arrayListUser = new ArrayList<>();
    RecyclerView rvContactList;
    ImageView imgAddContact;
    Cursor cursor;
    String name, phonenumber;
    FloatingActionButton btnDone;
    LinearLayout llBack;
    String TAG = MainActivity.class.getSimpleName();
    EditText edtSearch;
    LinearLayout llSearch, llTitle, llBtnSearch;
    Context context;
    SpotsDialog spotsDialog;
    TextView tvEmpty;
    private ArrayList<Contact> listOfContacts = new ArrayList<>();
    private ContactListAdapter mAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    ArrayList<String> invitedContacts;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1212;


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
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact_list);
        context = this;
        spotsDialog = new SpotsDialog(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue,
                R.color.colorBlue,
                R.color.colorBlue,
                R.color.colorYellow);



        if (ContextCompat.checkSelfPermission(ContactListActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactListActivity.this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(ContactListActivity.this,
                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }





        initValues();
        onClicks();
    }

    private void initValues() {

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        edtSearch = findViewById(R.id.edtSearch);
        llTitle = findViewById(R.id.llTitle);
        llBtnSearch = findViewById(R.id.llBtnSearch);
        llSearch = findViewById(R.id.llSearch);
        llBack = findViewById(R.id.llBack);
        btnDone = findViewById(R.id.btnDone);
        rvContactList = findViewById(R.id.rvContactList);
        tvEmpty = findViewById(R.id.tvEmpty);


        GetContactsIntoArrayList();
        /*mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                GetContactsIntoArrayList();
            }
        });
*/

        try {
            invitedContacts = getIntent().getStringArrayListExtra("invitedContacts");

        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> contactListString=new ArrayList<>();
        for(Contact contact:AddNewEventActivity.arrNewList){
            contactListString.add(contact.getSubname());
        }

        mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity.this,contactListString);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContactList.setLayoutManager(mLayoutManager);
        rvContactList.setItemAnimator(new DefaultItemAnimator());
        rvContactList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        llSearch.setVisibility(View.GONE);
        llTitle.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(mAdapter!=null) {
                    mAdapter.getFilter().filter(s);
                }
               /* if (s.toString().length() > 0) {
                    final ArrayList<Contact> tempArrayToSend = new ArrayList<>();

                        for (int i = 0; i < AddNewEventActivity.arrNewList.size(); i++) {
                            if (AddNewEventActivity.arrNewList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                                tempArrayToSend.add(AddNewEventActivity.arrNewList.get(i));
                            }
                        }

                        if (tempArrayToSend.size() != 0) {
                            mAdapter = new ContactListAdapter(tempArrayToSend, ContactListActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvContactList.setLayoutManager(mLayoutManager);
                            rvContactList.setItemAnimator(new DefaultItemAnimator());
                            rvContactList.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.GONE);
                            rvContactList.setVisibility(View.VISIBLE);
                        }else if(contactToInviteList.size()!=0)
                        {
                            for (int i = 0; i < contactToInviteList.size(); i++) {
                                if (contactToInviteList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                                    tempArrayToSend.add(contactToInviteList.get(i));
                                }
                            }

                            if (tempArrayToSend.size() != 0) {
                                mAdapter = new ContactListAdapter(tempArrayToSend, ContactListActivity.this);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rvContactList.setLayoutManager(mLayoutManager);
                                rvContactList.setItemAnimator(new DefaultItemAnimator());
                                rvContactList.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                tvEmpty.setVisibility(View.GONE);
                                rvContactList.setVisibility(View.VISIBLE);
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                                rvContactList.setVisibility(View.GONE);
                            }
                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvContactList.setVisibility(View.GONE);
                        }



                } else {
                    if (AddNewEventActivity.arrNewList.size() != 0) {
                        mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvContactList.setLayoutManager(mLayoutManager);
                        rvContactList.setItemAnimator(new DefaultItemAnimator());
                        rvContactList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(View.GONE);
                        rvContactList.setVisibility(View.VISIBLE);
                    } else if(contactToInviteList.size()!=0)
                    {
                        mAdapter = new ContactListAdapter(contactToInviteList, ContactListActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvContactList.setLayoutManager(mLayoutManager);
                        rvContactList.setItemAnimator(new DefaultItemAnimator());
                        rvContactList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(View.GONE);
                        rvContactList.setVisibility(View.VISIBLE);
                    }else {
                        rvContactList.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }*/

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


    }

    @Override
    public void onRefresh() {

        // Fetching data from server
        GetContactsIntoArrayList();
    }
    private void onClicks() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideKeyboard(ContactListActivity.this);
                if (invitedContacts != null)
                {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideKeyboard(ContactListActivity.this);
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
                    hideKeyboard(ContactListActivity.this);
                    ArrayList<String> contactListString=new ArrayList<>();
                    for(Contact contact:AddNewEventActivity.arrNewList){
                        contactListString.add(contact.getSubname());
                    }
                    mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity.this,contactListString);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvContactList.setLayoutManager(mLayoutManager);
                    rvContactList.setItemAnimator(new DefaultItemAnimator());
                    rvContactList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AddNewEventActivity.arrNewList = mAdapter.getModifyList();

    }


    // public ArrayList<String> arrContactList = new ArrayList<>();
    public void GetContactsIntoArrayList() {
        if (!isFinishing()) {
           spotsDialog.show();

        }


        int contact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (contact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {

            String[] PROJECTION = new String[] {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER };
            CursorLoader cursorLoader = new CursorLoader(context,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
                    null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME
                    + ")ASC");

            cursor= cursorLoader.loadInBackground();




        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (null != cursor) {
            // AddNewEventActivity.arrNewList.clear();
            arrayListUser.clear();

            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        arrayListUser.add(user);

                    }
                    //int j = 0;
                    oldlistContact.clear();
                    oldlistContactForUniqe.clear();
                    while (cursor.moveToNext()) {

                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phonenumber = phonenumber.replace(" ", "").replace("-", "");
                        Contact contact1 = new Contact(name, phonenumber, "");
                        if (invitedContacts != null) {
                            if (!invitedContacts.contains(phonenumber)) {
                                if (!contactToInviteListForUnique.contains(phonenumber)) {
                                    for (int i = 0; i < arrayListUser.size(); i++) {
                                        String mobileNumber = "";
                                        if (null != arrayListUser.get(i).getMobileNumber()) {
                                            mobileNumber = arrayListUser.get(i).getMobileNumber();
                                        }

                                        String userName = arrayListUser.get(i).getUserName();
                                        phonenumber = phonenumber.replace(" ", "").replace("-", "");
                                        if (mobileNumber.contains(phonenumber)) {
                                            contact1.setSubname(arrayListUser.get(i).getUserName());
                                            contact1.setUserName(userName);
                                        } else if (phonenumber.contains(mobileNumber)) {
                                            contact1.setUserName(userName);
                                        }
                                    }
                                    contactToInviteList.add(contact1);
                                    contactToInviteListForUnique.add(phonenumber);
                                }


                            } else {
                                if (!oldlistContactForUniqe.contains(phonenumber)) {
                                    for (int i = 0; i < arrayListUser.size(); i++) {
                                        String mobileNumber = "";
                                        if (null != arrayListUser.get(i).getMobileNumber()) {
                                            mobileNumber = arrayListUser.get(i).getMobileNumber();
                                        }

                                        String userName = arrayListUser.get(i).getUserName();
                                        phonenumber = phonenumber.replace(" ", "").replace("-", "");
                                        if (mobileNumber.contains(phonenumber)) {
                                            contact1.setSubname(arrayListUser.get(i).getUserName());
                                            contact1.setUserName(userName);
                                        } else if (phonenumber.contains(mobileNumber)) {
                                            contact1.setUserName(userName);
                                        }
                                    }
                                    contact1.setIsSelected("Y");

                                    oldlistContact.add(contact1);
                                    oldlistContactForUniqe.add(phonenumber);
                                }

                            }
                        } else {
                            for (int i = 0; i < arrayListUser.size(); i++) {
                                String mobileNumber = "";
                                if (null != arrayListUser.get(i).getMobileNumber()) {
                                    mobileNumber = arrayListUser.get(i).getMobileNumber();
                                }

                                String userName = arrayListUser.get(i).getUserName();
                                phonenumber = phonenumber.replace(" ", "").replace("-", "");
                                if (mobileNumber.equals(phonenumber)) {
                                    contact1.setSubname(mobileNumber);
                                    contact1.setUserName(userName);
                                }
                            }

                            try {
                                if (!AddNewEventActivity.arrNewListForCheck.contains(phonenumber)) {
                                    AddNewEventActivity.arrNewListForCheck.add(phonenumber);
                                    AddNewEventActivity.arrNewList.add(contact1);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                AddNewEventActivity.arrNewListForCheck.add(phonenumber);
                                AddNewEventActivity.arrNewList.add(contact1);
                                e.printStackTrace();
                            }

                        }
                    }
                    if (invitedContacts != null) {
                        Collections.sort(contactToInviteList, new CustomComparator());
                        ArrayList<String> contactListString = new ArrayList<>();
                        for (Contact contact : contactToInviteList) {
                            contactListString.add(contact.getSubname());
                        }
                        mAdapter = new ContactListAdapter(contactToInviteList, ContactListActivity.this, contactListString);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvContactList.setLayoutManager(mLayoutManager);
                        rvContactList.setItemAnimator(new DefaultItemAnimator());
                        rvContactList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        llSearch.setVisibility(View.GONE);
                        llTitle.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        // checkAndRequestPermissions();

                    } else {
                        Collections.sort(AddNewEventActivity.arrNewList, new CustomComparator());
                        mAdapter.notifyDataSetChanged();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    //cursor.close();

                    if (null != spotsDialog && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Failed to read user", error.toException());
                }


            });

        }
        }


    }

    public class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    GetContactsIntoArrayList();

                } else {

                    Toast.makeText(ContactListActivity.this, "Please allow Contact Permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}