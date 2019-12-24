package com.prometteur.myevents.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AllEventImagesAdapter extends RecyclerView.Adapter<AllEventImagesAdapter.MyViewHolder> {

    List<EventImageFirebase> eventsList;
    Context mContext;
    String uploadTime="";
    private final OnItemClickListener listener;
    private final View.OnLongClickListener onLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(EventImageFirebase item,int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEventPhotoUploaderLeft,eventImageLeft,imgEventPhotoUploaderRight,eventImageRight;
        LinearLayout llLeft,llRight,linLikeRight,linLikeLeft;
        TextView txtEventPhotoUploaderName,txtImgTime,txtImgTimeRight,txtEventPhotoUploaderNameRight,eventCommentsRight,
        eventLikesRight;
        TextView eventCommentsLeft,eventLikesLeft;
       public RelativeLayout viewBackground,viewForeground;
        public MyViewHolder(View view) {
            super(view);
            imgEventPhotoUploaderLeft = view.findViewById(R.id.imgEventPhotoUploaderLeft);
            eventImageLeft = view.findViewById(R.id.eventImageLeft);
            llLeft = view.findViewById(R.id.llLeft);
            txtEventPhotoUploaderName = view.findViewById(R.id.txtEventPhotoUploaderName);
            txtImgTime = view.findViewById(R.id.txtImgTime);

            imgEventPhotoUploaderRight = view.findViewById(R.id.imgEventPhotoUploaderRight);
            eventImageRight = view.findViewById(R.id.eventImageRight);
            llRight = view.findViewById(R.id.llRight);
            linLikeRight = view.findViewById(R.id.linLikeRight);
            linLikeLeft = view.findViewById(R.id.linLikeLeft);
            txtImgTimeRight = view.findViewById(R.id.txtImgTimeRight);
            txtEventPhotoUploaderNameRight = view.findViewById(R.id.txtEventPhotoUploaderNameRight);
            eventLikesRight = view.findViewById(R.id.eventLikesRight);
            eventCommentsRight = view.findViewById(R.id.eventCommentsRight);

            eventLikesLeft = view.findViewById(R.id.eventLikes);
            eventCommentsLeft = view.findViewById(R.id.eventComments);


            viewBackground = view.findViewById(R.id.viewBackground);
            viewForeground = view.findViewById(R.id.viewForeground);
        }
    }



    EventClassFirebase clikedEvent;
    public AllEventImagesAdapter(Context mContext, List<EventImageFirebase> eventsList,
                                 EventClassFirebase clickedEvent,
                                 OnItemClickListener listener,
                                 View.OnLongClickListener onLongClickListener) {
        this.eventsList = eventsList;
        this.mContext = mContext;
        this.listener = listener;
        this.onLongClickListener = onLongClickListener;
        this.clikedEvent = clickedEvent;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String eventPath = eventsList.get(position).getImgPath();
        String eventUploaderName = eventsList.get(position).getImgUploaderName();
        String eventUploaderUserName = eventsList.get(position).getImgUploaderUserName();
        String eventUploaderPhoto = eventsList.get(position).getImgUploaderPhoto();
        String eventUploadTime = eventsList.get(position).getFormattedImgUploadTime();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userName= preferences.getString("userName", "");

        int eventImageCommentsCount =0;
        int eventImageLikesCount =0;
        try {
            eventImageCommentsCount = eventsList.get(position).getImgComments().size();
        }catch (Exception e){} try {

            eventImageLikesCount = eventsList.get(position).getImgLikes().size();
        }catch (Exception e){}


      /*  SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = format1.parse(eventUploadTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        eventUploadTime=format2.format(date);*/
        //uploadTime=eventUploadTime;


        holder.llLeft.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
holder.llRight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        if(userName.equals(eventUploaderUserName))
        {
            //This is Right

            holder.llRight.setVisibility(View.VISIBLE);
            holder.llLeft.setVisibility(View.GONE);

            try {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        holder.txtEventPhotoUploaderNameRight.setText(eventUploaderName);



                        holder.txtImgTimeRight.setText(eventUploadTime);
                        Glide.with(mContext)
                                .load(eventPath)
                                .into(holder.eventImageRight);

                        holder.eventImageRight.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                listener.onItemClick(eventsList.get(position),position);
                            }
                        });

                        if(eventUploaderPhoto!=null && !eventUploaderPhoto.isEmpty()) {
                            Glide.with(mContext)
                                    .load(eventUploaderPhoto)
                                    .into(holder.imgEventPhotoUploaderRight);
                        }else
                        {
                            Glide.with(mContext)
                                    .load(R.drawable.default_profile)
                                    .into(holder.imgEventPhotoUploaderRight);
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
           // callLikesToServer( position,holder);
            holder.eventCommentsRight.setText(eventImageCommentsCount+" Comments");
            if(eventImageLikesCount==0) {
                holder.eventLikesRight.setText(eventImageLikesCount + " Likes");
                holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }else {
                holder.eventLikesRight.setText(eventImageLikesCount + " Likes");
                holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }
            if(eventsList.get(position).getImgLikes()!=null)
            {
                Set<String> keys=eventsList.get(position).getImgLikes().keySet();

                boolean isUserMyself=false;
                String keyUserSelf="";
                for(String key:keys)
                {
                    if(eventsList.get(position).getImgLikes().get(key)!=null) {
                        if (eventsList.get(position).getImgLikes().get(key).getLikeorUserName().equalsIgnoreCase(userName)) {
                            isUserMyself=true;
                            keyUserSelf=key;
                        } /*else {
                            isUserMyself=false;
                        }*/
                    }/*else
                    {
                        isUserMyself=false;
                    }*/
                }


                if (isUserMyself) {
                    holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    String finalKeyUserSelf = keyUserSelf;
                    holder.linLikeRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(holder.eventLikesRight.getCurrentTextColor()==mContext.getResources().getColor(R.color.colorYellow))
                            {

                                sendimageLikesToServer(position, holder, finalKeyUserSelf);
                                holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                            }else {
                                sendimageLikesToServer(position, holder,"");
                                holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                            }

                        }
                    });
                } else {
                    holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    holder.linLikeRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                sendimageLikesToServer(position, holder,"");
                                holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorYellow));


                        }
                    });
                }
            }else
            {
                holder.linLikeRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendimageLikesToServer(position, holder,"");
                        holder.eventLikesRight.setTextColor(mContext.getResources().getColor(R.color.colorYellow));

                    }
                });
            }

        }else {

            //This is Left

            holder.llRight.setVisibility(View.GONE);
            holder.llLeft.setVisibility(View.VISIBLE);

            try {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        holder.txtEventPhotoUploaderName.setText(eventUploaderName);
                        holder.txtImgTime.setText(eventUploadTime);
                        Glide.with(mContext)
                                .load(eventPath)
                                .into(holder.eventImageLeft);

                        holder.eventImageLeft.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                listener.onItemClick(eventsList.get(position),position);
                            }
                        });

                        if(eventUploaderPhoto!=null && !eventUploaderPhoto.isEmpty()) {
                            Glide.with(mContext)
                                    .load(eventUploaderPhoto)
                                    .into(holder.imgEventPhotoUploaderLeft);
                        }else
                        {
                            Glide.with(mContext)
                                    .load(R.drawable.default_profile)
                                    .into(holder.imgEventPhotoUploaderLeft);
                        }

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.eventCommentsLeft.setText(eventImageCommentsCount+" Comments");
           // callLikesToServer( position,holder);
            if(eventImageLikesCount==0) {
                holder.eventLikesLeft.setText(eventImageLikesCount + " Likes");
                holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }else {
                holder.eventLikesLeft.setText(eventImageLikesCount + " Likes");
                holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }
            if(eventsList.get(position).getImgLikes()!=null)
            {
                Set<String> keys=eventsList.get(position).getImgLikes().keySet();
                boolean isUserMyself=false;
                String keyUserSelf="";
                for(String key:keys)
                {
                    if(eventsList.get(position).getImgLikes().get(key)!=null) {
                        if (eventsList.get(position).getImgLikes().get(key).getLikeorUserName().equalsIgnoreCase(userName)) {
                            isUserMyself=true;
                            keyUserSelf=key;
                        } /*else {
                            isUserMyself=false;
                        }*/
                    }/*else
                    {
                        isUserMyself=false;
                    }*/
                }

                if (isUserMyself) {
                    holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    String finalKeyUserSelf = keyUserSelf;
                    holder.linLikeLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(holder.eventLikesLeft.getCurrentTextColor()==mContext.getResources().getColor(R.color.colorYellow))
                            {

                                sendimageLikesToServer(position, holder, finalKeyUserSelf);
                                holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                            }else {
                                sendimageLikesToServer(position, holder,"");
                                holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                            }

                        }
                    });
                } else {
                    holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    holder.linLikeLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                                sendimageLikesToServer(position, holder,"");
                                holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorYellow));

                        }
                    });
                }

            }else
            {
                holder.linLikeLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendimageLikesToServer(position, holder,"");
                        holder.eventLikesLeft.setTextColor(mContext.getResources().getColor(R.color.colorYellow));

                    }
                });
            }

        }

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    private void sendimageLikesToServer(int position, MyViewHolder holder,String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String likeDateTime = sdf.format(new Date());

        EventImagesLikes likes =new EventImagesLikes("Like", preferences.getString("nameRealname", ""),
                preferences.getString("userName", ""), preferences.getString("userPhoto", ""), likeDateTime);

        DatabaseReference commentsDatabace = FirebaseDatabase.getInstance().getReference().child("events")
                .child(clikedEvent.getEventKey()).child("eventImages")
                .child(""+position)
                .child("imgLikes");


        if(!key.isEmpty()){
            commentsDatabace.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(eventsList.get(position).getImgLikes()!=null) {
                        eventsList.get(position).getImgLikes().remove(key);
                        notifyDataSetChanged();
                    }

                }
            });

        }else {
            commentsDatabace.push().setValue(likes);
            commentsDatabace.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,EventImagesLikes> obj= (Map<String, EventImagesLikes>) dataSnapshot.getValue();
                    final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                     EventImagesLikes eventImagesLikes = null;


                    long count = dataSnapshot.getChildrenCount();
                    holder.eventLikesRight.setText(count + " Likes");
                    if(eventsList.get(position).getImgLikes()!=null) {
                        if(obj!=null) {
                            for (String key : obj.keySet()) {
                                eventImagesLikes = mapper.convertValue(obj.get(key), EventImagesLikes.class);
                                eventsList.get(position).getImgLikes().put(key, eventImagesLikes);
                            }
                        }
                    }else
                    {
                        Map<String,EventImagesLikes> likesMap=new HashMap<>();

                        for(String key:obj.keySet())
                        {
                            eventImagesLikes = mapper.convertValue(obj.get(key), EventImagesLikes.class);
                            likesMap.put(key,eventImagesLikes);
                            eventsList.get(position).setImgLikes(likesMap);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void callLikesToServer(int position, MyViewHolder holder) {


        DatabaseReference commentsDatabace = FirebaseDatabase.getInstance().getReference().child("events")
                .child(clikedEvent.getEventKey()).child("eventImages")
                .child(""+position)
                .child("imgLikes");



            commentsDatabace.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,EventImagesLikes> obj= (Map<String, EventImagesLikes>) dataSnapshot.getValue();
                    final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                     EventImagesLikes eventImagesLikes = null;


                    long count = dataSnapshot.getChildrenCount();
                    holder.eventLikesRight.setText(count + " Likes");
                    if(eventsList.get(position).getImgLikes()!=null) {
                        if(obj!=null) {
                            for (String key : obj.keySet()) {
                                eventImagesLikes = mapper.convertValue(obj.get(key), EventImagesLikes.class);
                                eventsList.get(position).getImgLikes().put(key, eventImagesLikes);
                            }
                        }
                    }else
                    {
                        Map<String,EventImagesLikes> likesMap=new HashMap<>();
if(obj!=null) {
    for (String key : obj.keySet()) {
        eventImagesLikes = mapper.convertValue(obj.get(key), EventImagesLikes.class);
        likesMap.put(key, eventImagesLikes);
        eventsList.get(position).setImgLikes(likesMap);
    }
}
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }
}