package com.prometteur.myevents.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.myevents.Activity.AddNewEventActivity;
import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Invitees;

import java.util.List;

public class InviteesAdapter extends RecyclerView.Adapter<InviteesAdapter.MyViewHolder> {

    private List<Invitees> inviteesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInviteesName;
        ImageView imgInviteesRemove;

        public MyViewHolder(View view) {
            super(view);
            tvInviteesName = (TextView) view.findViewById(R.id.txtInviteesName);
            imgInviteesRemove = view.findViewById(R.id.imgInviteesRemove);
        }
    }


    public InviteesAdapter(List<Invitees> inviteesList) {
        this.inviteesList = inviteesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invitees_card_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Invitees invitees = inviteesList.get(position);
        holder.tvInviteesName.setText(invitees.getName());
        holder.imgInviteesRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewEventActivity.arrNewList.get(inviteesList.get(position).getPosition()).setIsSelected("N");
                inviteesList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inviteesList.size();
    }
}