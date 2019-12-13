package com.prometteur.myevents.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Contact;
import com.prometteur.myevents.Utils.TextViewCustomFont;

import java.util.ArrayList;

public class EventDetailAdapter extends RecyclerView.Adapter<EventDetailAdapter.ViewHolder> {

private Context context;
    private ArrayList<Contact> dataList;
    public EventDetailAdapter(Context context, ArrayList<Contact> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v =  LayoutInflater.from(context).inflate(R.layout.events_details_card_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
       Contact contact = dataList.get(position);
       h.name.setText(contact.getName());
       h.number.setText(contact.getSubname());
       if(contact.getUserName()!=null && !contact.getUserName().isEmpty())
       {

           h.tv_Invite.setVisibility(View.GONE);
       }else
       {
           h.tv_Invite.setVisibility(View.VISIBLE);
       }
      // h.name.setText(contact.getName());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,number,tv_Invite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.txtContactName);
            number=itemView.findViewById(R.id.txtContactSubName);
            tv_Invite=itemView.findViewById(R.id.tv_Invite);
        }
    }
}