package com.prometteur.myevents.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prometteur.myevents.Activity.AllEventImagesDisplayActivity;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.EventClassFirebase;
import com.prometteur.myevents.SingletonClasses.EventCommentFirebase;
import com.prometteur.myevents.SingletonClasses.EventImageFirebase;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private List<EventCommentFirebase> commentsList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtYellowMessageDate, txtYellowMessageText, txtGrayMessageDate, txtGrayMessageText,txtSenderNameYellow,txtSenderNameGray;
        ImageView imgYellowMessageProfile, imgGaryMessageProfile;
        CardView card_right,card_left;


        public MyViewHolder(View view) {
            super(view);
            txtYellowMessageDate = view.findViewById(R.id.txtYellowMessageDate);
            txtYellowMessageText = view.findViewById(R.id.txtYellowMessageText);
            txtGrayMessageDate = view.findViewById(R.id.txtGrayMessageDate);
            txtGrayMessageText = view.findViewById(R.id.txtGrayMessageText);
            imgYellowMessageProfile = view.findViewById(R.id.imgYellowMessageProfile);
            txtSenderNameYellow = view.findViewById(R.id.txtSenderNameYellow);
            txtSenderNameGray = view.findViewById(R.id.txtSenderNameGray);
            imgGaryMessageProfile = view.findViewById(R.id.imgGaryMessageProfile);
            card_right = view.findViewById(R.id.card_right);
            card_left = view.findViewById(R.id.card_left);


        }
    }


    public CommentsAdapter(List<EventCommentFirebase> commentsList, Context mContext) {
        this.commentsList = commentsList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventCommentFirebase comment = commentsList.get(position);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userName= preferences.getString("userName", "");




        if(userName.equals(comment.getCommentorUserName())) {//This is Left
            holder.card_right.setVisibility(View.VISIBLE);
            holder.card_left.setVisibility(View.GONE);


            holder.txtSenderNameYellow.setText(""+comment.getCommentorName());
            holder.txtYellowMessageText.setText(""+comment.getStrComment());


            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            Date date = null;
            try {
                date = format1.parse(comment.getCommentDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateToSet = format2.format(date);

            String[] splitStr = dateToSet.trim().split("\\s+");

            dateToSet=splitStr[0]+"\n"+splitStr[1]+" "+splitStr[2];


            holder.txtYellowMessageDate.setText(dateToSet);

            String url1 = comment.getCommentorProfile();
            if(url1!=null && !url1.isEmpty()) {
                Glide.with(mContext)
                        .load(url1)
                        .into(holder.imgYellowMessageProfile);
            }else
            {
                Glide.with(mContext)
                        .load(R.drawable.default_profile)
                        .into(holder.imgYellowMessageProfile);
            }

        }else {
            holder.card_right.setVisibility(View.GONE);
            holder.card_left.setVisibility(View.VISIBLE);

            holder.txtSenderNameGray.setText(""+comment.getCommentorName());
            holder.txtGrayMessageText.setText(""+comment.getStrComment());


            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            Date date = null;
            try {
                date = format1.parse(comment.getCommentDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateToSet = format2.format(date);


            String[] splitStr = dateToSet.trim().split("\\s+");

            dateToSet=splitStr[0]+"\n"+splitStr[1]+" "+splitStr[2];


            holder.txtGrayMessageDate.setText(dateToSet);

            String url1 = comment.getCommentorProfile();
            if(url1!=null && !url1.isEmpty()) {
                Glide.with(mContext)
                        .load(url1)
                        .into(holder.imgGaryMessageProfile);
            }else
            {
                Glide.with(mContext)
                        .load(R.drawable.default_profile)
                        .into(holder.imgGaryMessageProfile);
            }

        }



    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}