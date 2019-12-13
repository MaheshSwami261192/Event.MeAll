package com.prometteur.myevents.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ContactListActivity_bk extends AppCompatActivity {
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
      //  mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity_bk.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContactList.setLayoutManager(mLayoutManager);
        rvContactList.setItemAnimator(new DefaultItemAnimator());
        rvContactList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        llSearch.setVisibility(View.GONE);
        llTitle.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        checkAndRequestPermissions();


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    final ArrayList<Contact> tempArrayToSend = new ArrayList<>();

                    for (int i = 0; i < AddNewEventActivity.arrNewList.size(); i++) {
                        if (AddNewEventActivity.arrNewList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                            tempArrayToSend.add(AddNewEventActivity.arrNewList.get(i));
                        }
                    }

                    if(tempArrayToSend.size()!=0) {
                      //  mAdapter = new ContactListAdapter(tempArrayToSend, ContactListActivity_bk.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvContactList.setLayoutManager(mLayoutManager);
                        rvContactList.setItemAnimator(new DefaultItemAnimator());
                        rvContactList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(View.GONE);
                        rvContactList.setVisibility(View.VISIBLE);
                    }else
                    {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvContactList.setVisibility(View.GONE);
                    }
                } else {
                    if(AddNewEventActivity.arrNewList.size()!=0) {
                      //  mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity_bk.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvContactList.setLayoutManager(mLayoutManager);
                        rvContactList.setItemAnimator(new DefaultItemAnimator());
                        rvContactList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(View.GONE);
                        rvContactList.setVisibility(View.VISIBLE);
                    }else
                    {
                        rvContactList.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
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


    }

    private void onClicks() {
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideKeyboard(ContactListActivity_bk.this);
            }
        });
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideKeyboard(ContactListActivity_bk.this);
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
                    hideKeyboard(ContactListActivity_bk.this);
                   // mAdapter = new ContactListAdapter(AddNewEventActivity.arrNewList, ContactListActivity_bk.this);
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

    private boolean checkAndRequestPermissions() {
        int contact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (contact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            GetContactsIntoArrayList();
        }
        return true;
    }
   // public ArrayList<String> arrContactList = new ArrayList<>();
    public void GetContactsIntoArrayList() {

        spotsDialog.show();
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
                    int j=0;
                    while (cursor.moveToNext()) {

                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Contact contact1 = new Contact(name, phonenumber, "");

                        for (int i = 0; i < arrayListUser.size(); i++) {
                            String mobileNumber = "";
                            if (null != arrayListUser.get(i).getMobileNumber()) {
                                mobileNumber = arrayListUser.get(i).getMobileNumber();
                            }

                            String userName = arrayListUser.get(i).getUserName();
                            phonenumber = phonenumber.replace(" ", "").replace("-", "");
                            if (mobileNumber.equals(phonenumber)) {
                                contact1.setSubname(arrayListUser.get(i).getUserName());
                                contact1.setUserName(userName);
                            }
                        }

                        try {
                            if (!AddNewEventActivity.arrNewListForCheck.contains(phonenumber)) {
                                AddNewEventActivity.arrNewListForCheck.add(phonenumber);
                                AddNewEventActivity.arrNewList.add(contact1);
                            }
                        }catch (IndexOutOfBoundsException e)
                        {
                            AddNewEventActivity.arrNewListForCheck.add(phonenumber);
                            AddNewEventActivity.arrNewList.add(contact1);
                            e.printStackTrace();
                        }
                        j++;
                    }
                    Collections.sort(AddNewEventActivity.arrNewList, new CustomComparator());
                    mAdapter.notifyDataSetChanged();
                    cursor.close();

                    if (null != spotsDialog && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read user", error.toException());
                }


            });


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

                    Toast.makeText(ContactListActivity_bk.this, "Please allow Contact Permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
