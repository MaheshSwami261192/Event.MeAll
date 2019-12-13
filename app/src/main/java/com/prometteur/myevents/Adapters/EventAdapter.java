package com.prometteur.myevents.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prometteur.myevents.Activity.AllEventImagesDisplayActivity;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    Context mContext;
    private List<EventClassFirebase> moviesList;

    public EventAdapter(List<EventClassFirebase> moviesList, Context mContext) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventClassFirebase event = moviesList.get(position);
        holder.eventTitle.setText(event.getEventTitle());
        holder.eventLocation.setText(event.getEventEventAddress());
        holder.eventCreatorName.setText(event.getEventCreatorName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventTime.setText(event.getEventStartTime());


        holder.llOneImage.setVisibility(View.GONE);
        holder.llTwoImage.setVisibility(View.GONE);
        holder.llThreeImage.setVisibility(View.GONE);
        holder.llFourImage.setVisibility(View.GONE);

        ArrayList<EventImageFirebase> eventImagesList = new ArrayList<>();
        eventImagesList = event.getEventImages();
       /* if (null != event.getEventImages() && !event.getEventImages().equals("")) {
            String eventImages = event.getEventImages();

            Gson converter = new Gson();

            Type type = new TypeToken<ArrayList<EventImageFirebase>>(){}.getType();
            eventImagesList =  converter.fromJson(eventImages, type );



        }*/

       if(eventImagesList!=null) {
           if (eventImagesList.size() == 0) {
               holder.llOneImage.setVisibility(View.VISIBLE);
               holder.imgEventImagesOne.setImageDrawable(mContext.getResources().getDrawable(R.drawable.background_event));
               holder.imgEventImagesOne.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                       intent.putExtra("clickedEvent", event);
                       intent.putExtra("eventName", event.getEventTitle());
                       ((Activity) mContext).startActivity(intent);

                   }
               });
           } else if (eventImagesList.size() == 1) {
               holder.llOneImage.setVisibility(View.VISIBLE);
               holder.llTwoImage.setVisibility(View.GONE);
               holder.llThreeImage.setVisibility(View.GONE);
               holder.llFourImage.setVisibility(View.GONE);

               String url1 = eventImagesList.get(0).getImgPath();

               Glide.with(mContext)
                       .load(url1)
                       .into(holder.imgEventImagesOne);

               ArrayList<EventImageFirebase> finalItems = eventImagesList;
               holder.imgEventImagesOne.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           } else if (eventImagesList.size() == 2) {
               holder.llOneImage.setVisibility(View.GONE);
               holder.llTwoImage.setVisibility(View.VISIBLE);
               holder.llThreeImage.setVisibility(View.GONE);
               holder.llFourImage.setVisibility(View.GONE);


               String Count = "+ " + eventImagesList.size();
               holder.txtImagesCountOfTwo.setText(Count);

               String url1 = eventImagesList.get(0).getImgPath();
               if (url1 != null && !url1.isEmpty()) {
                   Glide.with(mContext)
                           .load(url1)
                           .into(holder.imgEventImagesTwoOne);
               } else {
                   Glide.with(mContext)
                           .load(R.drawable.default_profile)
                           .into(holder.imgEventImagesTwoOne);
               }

               String url2 = eventImagesList.get(1).getImgPath();
               if (url2 != null && !url2.isEmpty()) {
                   Glide.with(mContext)
                           .load(url2)
                           .into(holder.imgEventImagesTwoTwo);
               } else {
                   Glide.with(mContext)
                           .load(R.drawable.default_profile)
                           .into(holder.imgEventImagesTwoTwo);
               }

               ArrayList<EventImageFirebase> finalItems = eventImagesList;
               holder.imgEventImagesTwoOne.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
               holder.imgEventImagesTwoTwo.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           } else if (eventImagesList.size() == 3) {
               holder.llOneImage.setVisibility(View.GONE);
               holder.llTwoImage.setVisibility(View.GONE);
               holder.llThreeImage.setVisibility(View.VISIBLE);
               holder.llFourImage.setVisibility(View.GONE);

               String Count = "+ " + eventImagesList.size();
               holder.txtImagesCountOfThree.setText(Count);
               holder.txtImagesCountOfThree.setVisibility(View.GONE);


               String url1 = eventImagesList.get(0).getImgPath();
               Glide.with(mContext)
                       .load(url1)
                       .into(holder.imgEventImagesThreeOne);


               String url2 = eventImagesList.get(1).getImgPath();
               Glide.with(mContext)
                       .load(url2)
                       .into(holder.imgEventImagesThreeTwo);

               String url3 = eventImagesList.get(2).getImgPath();
               Glide.with(mContext)
                       .load(url3)
                       .into(holder.imgEventImagesThreeThree);


               ArrayList<EventImageFirebase> finalItems = eventImagesList;
               holder.imgEventImagesThreeOne.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });holder.imgEventImagesThreeTwo.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });holder.imgEventImagesThreeThree.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });


           } else if (eventImagesList.size() == 4 || eventImagesList.size() > 4) {
               holder.llOneImage.setVisibility(View.GONE);
               holder.llTwoImage.setVisibility(View.GONE);
               holder.llThreeImage.setVisibility(View.GONE);
               holder.llFourImage.setVisibility(View.VISIBLE);

               String Count = "+ " + (eventImagesList.size()-3);
               if (eventImagesList.size() == 4){
                   holder.txtImagesCountOfFour.setVisibility(View.GONE);
               }else {
                   holder.txtImagesCountOfFour.setText(Count);
               }

               String url1 = eventImagesList.get(0).getImgPath();
               Glide.with(mContext)
                       .load(url1)
                       .into(holder.imgEventImagesFourOne);


               String url2 = eventImagesList.get(1).getImgPath();
               Glide.with(mContext)
                       .load(url2)
                       .into(holder.imgEventImagesFourTwo);

               String url3 = eventImagesList.get(2).getImgPath();
               Glide.with(mContext)
                       .load(url3)
                       .into(holder.imgEventImagesFourThree);

               String url4 = eventImagesList.get(3).getImgPath();
               Glide.with(mContext)
                       .load(url4)
                       .into(holder.imgEventImagesFourFour);


               ArrayList<EventImageFirebase> finalItems = eventImagesList;
               holder.imgEventImagesFourOne.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
               holder.imgEventImagesFourTwo.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
               holder.imgEventImagesFourThree.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
               holder.imgEventImagesFourFour.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (finalItems.size() > 0) {
                           Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                           intent.putExtra("clickedEvent", event);
                           intent.putExtra("eventName", event.getEventTitle());
                           ((Activity) mContext).startActivity(intent);
                       } else {
                           Toast.makeText(mContext, "No images found in this event", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           }


           String url1 = event.getEventCreatorProfile();
           if (!url1.isEmpty()) {
               Glide.with(mContext)
                       .load(url1)
                       .into(holder.imgEventCreatorProfile);
           } else {
               Glide.with(mContext)
                       .load(R.drawable.default_profile)
                       .into(holder.imgEventCreatorProfile);
           }
       }else
       {
           holder.llOneImage.setVisibility(View.VISIBLE);
           holder.imgEventImagesOne.setImageDrawable(mContext.getResources().getDrawable(R.drawable.background_event));
           holder.imgEventImagesOne.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   Intent intent = new Intent(mContext, AllEventImagesDisplayActivity.class);
                   intent.putExtra("clickedEvent", event);
                   intent.putExtra("eventName", event.getEventTitle());
                   ((Activity) mContext).startActivity(intent);

               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, eventDate, eventTime, eventCreatorName, eventLocation;
        ImageView imgEventCreatorProfile;
        RelativeLayout rlDefaultImage;

        LinearLayout llOneImage, llTwoImage, llThreeImage, llFourImage;

        ImageView imgEventImagesOne, imgEventImagesTwoOne, imgEventImagesTwoTwo, imgEventImagesThreeOne,
                imgEventImagesThreeTwo, imgEventImagesThreeThree, imgEventImagesFourOne, imgEventImagesFourTwo,
                imgEventImagesFourThree, imgEventImagesFourFour;
        TextView txtImagesCountOfTwo, txtImagesCountOfThree, txtImagesCountOfFour;

        public MyViewHolder(View view) {
            super(view);
            eventTitle = (TextView) view.findViewById(R.id.eventTitle);
            eventDate = (TextView) view.findViewById(R.id.eventDate);
            eventTime = (TextView) view.findViewById(R.id.eventTime);
            eventLocation = (TextView) view.findViewById(R.id.eventLocation);
            eventCreatorName = (TextView) view.findViewById(R.id.eventCreatorName);
            imgEventCreatorProfile = view.findViewById(R.id.imgEventCreatorProfile);
            rlDefaultImage = view.findViewById(R.id.rlDefaultImage);

            llOneImage = view.findViewById(R.id.llOneImage);
            llTwoImage = view.findViewById(R.id.llTwoImage);
            llThreeImage = view.findViewById(R.id.llThreeImage);
            llFourImage = view.findViewById(R.id.llFourImage);


            imgEventImagesOne = view.findViewById(R.id.imgEventImagesOne);
            imgEventImagesTwoOne = view.findViewById(R.id.imgEventImagesTwoOne);
            imgEventImagesTwoTwo = view.findViewById(R.id.imgEventImagesTwoTwo);
            imgEventImagesThreeOne = view.findViewById(R.id.imgEventImagesThreeOne);
            imgEventImagesThreeTwo = view.findViewById(R.id.imgEventImagesThreeTwo);
            imgEventImagesThreeThree = view.findViewById(R.id.imgEventImagesThreeThree);
            imgEventImagesFourOne = view.findViewById(R.id.imgEventImagesFourOne);
            imgEventImagesFourTwo = view.findViewById(R.id.imgEventImagesFourTwo);
            imgEventImagesFourThree = view.findViewById(R.id.imgEventImagesFourThree);
            imgEventImagesFourFour = view.findViewById(R.id.imgEventImagesFourFour);
            txtImagesCountOfTwo = view.findViewById(R.id.txtImagesCountOfTwo);
            txtImagesCountOfThree = view.findViewById(R.id.txtImagesCountOfThree);
            txtImagesCountOfFour = view.findViewById(R.id.txtImagesCountOfFour);


        }
    }
}