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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prometteur.myevents.Activity.AllEventImagesDisplayActivity;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventPhotoUserWiseAdapter extends RecyclerView.Adapter<EventPhotoUserWiseAdapter.MyViewHolder> {

    private ArrayList<EventImageFirebase> imageList ;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventUploaderName;
        ImageView eventUploaderProfile;
        RelativeLayout rlDefaultImage;

        LinearLayout llOneImage, llTwoImage, llThreeImage, llFourImage;

        ImageView imgEventImagesOne, imgEventImagesTwoOne, imgEventImagesTwoTwo, imgEventImagesThreeOne,
                imgEventImagesThreeTwo, imgEventImagesThreeThree, imgEventImagesFourOne, imgEventImagesFourTwo,
                imgEventImagesFourThree, imgEventImagesFourFour;
        TextView txtImagesCountOfTwo, txtImagesCountOfThree, txtImagesCountOfFour;



        public MyViewHolder(View view) {
            super(view);
            eventUploaderName =  view.findViewById(R.id.eventUploaderName);
            eventUploaderProfile = view.findViewById(R.id.eventUploaderProfile);
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


    public EventPhotoUserWiseAdapter(ArrayList<EventImageFirebase> imageList , Context mContext) {
        this.imageList = imageList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_userwise_card_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventImageFirebase event = imageList.get(position);
        holder.eventUploaderName.setText(event.getImgUploaderName());


        String profilePic = event.getImgUploaderPhoto();
        if(profilePic!=null && !profilePic.isEmpty()) {
            Glide.with(mContext)
                    .load(profilePic)
                    .into(holder.eventUploaderProfile);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.default_profile)
                    .into(holder.eventUploaderProfile);
        }


        holder.llOneImage.setVisibility(View.GONE);
        holder.llTwoImage.setVisibility(View.GONE);
        holder.llThreeImage.setVisibility(View.GONE);
        holder.llFourImage.setVisibility(View.GONE);



        List<String> eventImagesList=new ArrayList<>();
        if (null != event.getImgPath() && !event.getImgPath().equals("")) {
            String eventImages = event.getImgPath();

            eventImagesList= Arrays.asList(eventImages.split("\\s*,\\s*"));
          /*  Gson converter = new Gson();

            Type type = new TypeToken<ArrayList<EventImageFirebase>>(){}.getType();
            eventImagesList =  converter.fromJson(eventImages, type );
*/


        }

        if (eventImagesList.size() == 0) {
            holder.llOneImage.setVisibility(View.VISIBLE);
            holder.imgEventImagesOne.setImageDrawable(mContext.getResources().getDrawable(R.drawable.image_three));

        } else if (eventImagesList.size() == 1) {
            holder.llOneImage.setVisibility(View.VISIBLE);
            holder.llTwoImage.setVisibility(View.GONE);
            holder.llThreeImage.setVisibility(View.GONE);
            holder.llFourImage.setVisibility(View.GONE);

            String url1 = eventImagesList.get(0);

            Glide.with(mContext)
                    .load(url1)
                    .into(holder.imgEventImagesOne);


        } else if (eventImagesList.size() == 2) {
            holder.llOneImage.setVisibility(View.GONE);
            holder.llTwoImage.setVisibility(View.VISIBLE);
            holder.llThreeImage.setVisibility(View.GONE);
            holder.llFourImage.setVisibility(View.GONE);


            String Count="+ " + eventImagesList.size();
            holder.txtImagesCountOfTwo.setText(Count);
            holder.txtImagesCountOfTwo.setVisibility(View.GONE);

            String url1 = eventImagesList.get(0);
            Glide.with(mContext)
                    .load(url1)
                    .into(holder.imgEventImagesTwoOne);


            String url2 = eventImagesList.get(1);
            Glide.with(mContext)
                    .load(url2)
                    .into(holder.imgEventImagesTwoTwo);



        } else if (eventImagesList.size() == 3) {
            holder.llOneImage.setVisibility(View.GONE);
            holder.llTwoImage.setVisibility(View.GONE);
            holder.llThreeImage.setVisibility(View.VISIBLE);
            holder.llFourImage.setVisibility(View.GONE);

            String Count="+ " +  (eventImagesList.size()-2);
            holder.txtImagesCountOfThree.setText(Count);

            holder.txtImagesCountOfThree.setVisibility(View.GONE);
            String url1 = eventImagesList.get(0);
            Glide.with(mContext)
                    .load(url1)
                    .into(holder.imgEventImagesThreeOne);


            String url2 = eventImagesList.get(1);
            Glide.with(mContext)
                    .load(url2)
                    .into(holder.imgEventImagesThreeTwo);

            String url3 = eventImagesList.get(2);
            Glide.with(mContext)
                    .load(url3)
                    .into(holder.imgEventImagesThreeThree);




        } else if (eventImagesList.size() == 4 || eventImagesList.size() > 4) {
            holder.llOneImage.setVisibility(View.GONE);
            holder.llTwoImage.setVisibility(View.GONE);
            holder.llThreeImage.setVisibility(View.GONE);
            holder.llFourImage.setVisibility(View.VISIBLE);

            String Count="+ " +  (eventImagesList.size()-3);
            if (eventImagesList.size() == 4){
                holder.txtImagesCountOfFour.setVisibility(View.GONE);
            }else {
                holder.txtImagesCountOfFour.setText(Count);
            }

            String url1 = eventImagesList.get(0);
            Glide.with(mContext)
                    .load(url1)
                    .into(holder.imgEventImagesFourOne);


            String url2 = eventImagesList.get(1);
            Glide.with(mContext)
                    .load(url2)
                    .into(holder.imgEventImagesFourTwo);

            String url3 = eventImagesList.get(2);
            Glide.with(mContext)
                    .load(url3)
                    .into(holder.imgEventImagesFourThree);

            String url4 = eventImagesList.get(3);
            Glide.with(mContext)
                    .load(url4)
                    .into(holder.imgEventImagesFourFour);



        }



    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}