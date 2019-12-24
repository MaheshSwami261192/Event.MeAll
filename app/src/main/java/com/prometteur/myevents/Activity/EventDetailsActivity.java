package com.prometteur.myevents.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.myevents.Adapters.ContactListAdapter;
import com.prometteur.myevents.Adapters.EventDetailAdapter;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Contact;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;
import com.prometteur.myevents.SingletonClasses.User;
import com.prometteur.myevents.Utils.TextViewCustomFont;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetailsActivity extends AppCompatActivity {
    public static ArrayList<Contact> contactToInviteList = new ArrayList<>();
    public static ArrayList<String> contactToInviteListForUnique = new ArrayList<>();
    public static ArrayList<Contact> oldlistContact = new ArrayList<>();
    public static ArrayList<String> oldlistContactForUniqe = new ArrayList<>();
    EventDetailAdapter eventDetailAdapter;
    CircleImageView imgContactProfile;
    private EventClassFirebase theEvent;
    private TextViewCustomFont tv_eventTitle, tv_Location, tv_addMember, txtContactName, txtContactSubName, tvDateTime;
    private RecyclerView rvContactList;
    private LinearLayout llBack;
    private Cursor cursor;
    private ArrayList<Contact> membersDatalist = new ArrayList<>();
    private ArrayList<String> membersDatalistString = new ArrayList<>();
    private DatabaseReference mFirebaseDatabase,mFirebaseDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
         getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details);
        tv_eventTitle = findViewById(R.id.tv_eventTitle);
        tv_Location = findViewById(R.id.tv_Location);
        tv_addMember = findViewById(R.id.tv_addMember);
        rvContactList = findViewById(R.id.rvContactList);
        llBack = findViewById(R.id.llBack);

        txtContactName = findViewById(R.id.txtContactName);
        txtContactSubName = findViewById(R.id.txtContactSubName);
        tvDateTime = findViewById(R.id.tvDateTime);
        imgContactProfile = findViewById(R.id.imgContactProfile);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("events");
        mFirebaseDatabaseUser = FirebaseDatabase.getInstance().getReference("users");

        //theEvent =
        getRefreshMemberList((EventClassFirebase) getIntent()
                .getBundleExtra("EVENT")
                .getSerializable("EVENT"));

        tv_addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldlistContact = new ArrayList<>();
                contactToInviteList = new ArrayList<>();
                contactToInviteListForUnique = new ArrayList<>();
                if (theEvent.getMembersNumber() == null) {
                    theEvent.setMembersNumber(new ArrayList<>());
                }
                startActivityForResult(new Intent(EventDetailsActivity.this, ContactListActivity.class)
                        .putStringArrayListExtra("invitedContacts", theEvent.getMembersNumber()), 102, null);
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    final ArrayList<User> arrayListUser = new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {

            ArrayList<String> oldlist = theEvent.getMembersNumber();
            String invitee=theEvent.getEventInvitees();
            ArrayList<Contact> oldlistContactList = new ArrayList<>();

            oldlistContactList.addAll(oldlistContact);
            for (Contact contact : contactToInviteList) {
                if (contact.getIsSelected() != null) {
                    if (contact.getIsSelected().equalsIgnoreCase("Y")) {
                        oldlist.add(0, contact.getSubname());
                        oldlistContactList.add(contact);
                        if(!contact.getUserName().isEmpty())
                        {
                            invitee=invitee+", "+contact.getUserName();
                        }
                    }
                }

            }
            addInviteesToEvent(oldlist, oldlistContactList,invitee);
        }

    }


    private void addInviteesToEvent(
            final ArrayList<String> arrayList, ArrayList<Contact> oldlistContactList,String invitee) {


        Map<String, Object> mapEventImages = new HashMap<>();

        mapEventImages.put("membersNumber", (Object) arrayList);
        mapEventImages.put("eventInvitees", (Object) invitee);

        DatabaseReference data = mFirebaseDatabase.child(theEvent.getEventKey());

        data.updateChildren(mapEventImages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //membersDatalist.clear();
                getContactsIntoArrayList(null, oldlistContactList);
                theEvent.setMembersNumber(arrayList);

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

    }

    public void getContactsIntoArrayList(ArrayList<String> membersNumberString, ArrayList<Contact> membersNumber) {
        if (membersNumberString != null) {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (null != cursor) {



                //start

                arrayListUser.clear();
                mFirebaseDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            arrayListUser.add(user);

                        }

                        while (cursor.moveToNext()) {

                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phonenumber = phonenumber.replace(" ", "").replace("-", "");
                            if (membersNumberString != null) {
                                if (membersNumberString.contains(phonenumber)) {
                                    Contact contact1 = new Contact(name, phonenumber, "");
                                    if (!membersDatalistString.contains(phonenumber)) {
                                        for(int i=0;i<arrayListUser.size();i++)
                                        {
                                            if(arrayListUser.get(i).getMobileNumber().contains(phonenumber))
                                            {
                                                contact1.setUserName(arrayListUser.get(i).getUserName());
                                            }else if(phonenumber.contains(arrayListUser.get(i).getMobileNumber()))
                                            {
                                                contact1.setUserName(arrayListUser.get(i).getUserName());
                                            }
                                        }





                                        membersDatalist.add(contact1);
                                        membersDatalistString.add(phonenumber);
                                    }
                                }
                            }
                        }

                            for(int j=0;j<membersNumberString.size();j++) {
                                for (int i = 0; i < arrayListUser.size(); i++) {
                                    if (!membersDatalistString.contains(membersNumberString.get(j))) {
                                        Contact contact1 = new Contact(arrayListUser.get(i).getFirstName() + " " + arrayListUser.get(i).getLastName(), arrayListUser.get(i).getMobileNumber(), "");
                                        if (arrayListUser.get(i).getMobileNumber().contains(membersNumberString.get(j))) {
                                            contact1.setUserName(arrayListUser.get(i).getUserName());
                                            membersDatalist.add(contact1);
                                            membersDatalistString.add(arrayListUser.get(i).getMobileNumber());
                                        } else if (membersNumberString.get(j).contains(arrayListUser.get(i).getMobileNumber())) {
                                            contact1.setUserName(arrayListUser.get(i).getUserName());
                                            membersDatalist.add(contact1);
                                            membersDatalistString.add(arrayListUser.get(i).getMobileNumber());
                                        }
                                    }
                                }
                            }

                      /*  for(int j=0;j<membersNumberString.size();j++) {
                            if (!membersDatalistString.contains(membersNumberString.get(j))) {
                                Contact contact1 = new Contact(membersNumberString.get(j), membersNumberString.get(j), "");
                                membersDatalist.add(contact1);
                                membersDatalistString.add(membersNumberString.get(j));
                            }

                        }*/

                        Collections.sort(membersDatalist, new CustomComparator());
                        eventDetailAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("UserList", "Failed to read user", error.toException());
                    }


                });


                //end





               // cursor.close();
            }
        } else if (membersNumber != null) {
            Collections.sort(membersNumber, new CustomComparator());
            membersDatalist.clear();
            membersDatalist.addAll(membersNumber);
            eventDetailAdapter.notifyDataSetChanged();
        }

    }

    public class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    public void getRefreshMemberList(EventClassFirebase eventClassFirebase)
    {
        DatabaseReference data = mFirebaseDatabase.child(eventClassFirebase.getEventKey());

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() instanceof List) {
                    List<Object> values = (List<Object>) dataSnapshot.getValue();
                    // do your magic with values
                }
                else {
                    // handle other possible types
                    HashMap<String,Object> event=new HashMap<>();
                    event= (HashMap<String, Object>) dataSnapshot.getValue();
                    theEvent=new EventClassFirebase();
                    theEvent.setEventKey(eventClassFirebase.getEventKey());
                    theEvent.setEventInvitees(event.get("eventInvitees").toString());
                    theEvent.setEventCreateDate(event.get("eventCreateDate").toString());
                    theEvent.setEventCreatorName(eventClassFirebase.getEventCreatorName());
                    theEvent.setEventCreatorProfile(eventClassFirebase.getEventCreatorProfile());
                    theEvent.setEventCreatorUserName(eventClassFirebase.getEventCreatorUserName());
                    theEvent.setEventDate(event.get("eventDate").toString());
                    theEvent.setEventEventAddress(event.get("eventEventAddress").toString());
                    theEvent.setEventImages((ArrayList<EventImageFirebase>) event.get("eventEventImages"));
                    theEvent.setEventTitle(event.get("eventTitle").toString());
                    theEvent.setEventStartTime(event.get("eventStartTime").toString());
                    theEvent.setEventEndTime(event.get("eventEndTime").toString());
                    theEvent.setEventProfileImage(event.get("eventProfileImage").toString());
                    theEvent.setMembersNumber((ArrayList<String>)event.get("membersNumber"));
                }



               // Log.i("memberlist",theEvent.getMembersNumber().toString());


                tv_eventTitle.setText(theEvent.getEventTitle());
                tv_Location.setText(theEvent.getEventEventAddress());
                txtContactName.setText(theEvent.getEventCreatorName());
                txtContactSubName.setText(theEvent.getEventCreatorUserName());
                tvDateTime.setText("Created by, " + theEvent.getEventCreateDate());

                if (theEvent.getEventCreatorProfile() != null && !theEvent.getEventCreatorProfile().isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(theEvent.getEventCreatorProfile())
                            .into(imgContactProfile);
                } else {
                    Glide.with(getApplicationContext()).load(R.drawable.user).into(imgContactProfile);
                }


                eventDetailAdapter = new EventDetailAdapter(EventDetailsActivity.this, membersDatalist);


                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvContactList.setLayoutManager(mLayoutManager);
                rvContactList.setItemAnimator(new DefaultItemAnimator());
                rvContactList.setAdapter(eventDetailAdapter);
                eventDetailAdapter.notifyDataSetChanged();

                getContactsIntoArrayList(theEvent.getMembersNumber(), null);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

