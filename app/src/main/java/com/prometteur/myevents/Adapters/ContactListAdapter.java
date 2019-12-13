package com.prometteur.myevents.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.myevents.R;
import com.prometteur.myevents.SingletonClasses.Contact;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    private ArrayList<Contact> contactList;
    private ArrayList<Contact> contactListFilter;
    ArrayList<String> contactListString;
    ArrayList<String> contactTempList=new ArrayList<>();
    private CustomFilter mFilter;
    public ContactListAdapter(ArrayList<Contact> contactList, Context mContext,ArrayList<String> contactListString) {
        this.contactList = contactList;
        this.contactListFilter = contactList;
        this.contactListString = contactListString;
        this.mContext = mContext;
        mFilter = new CustomFilter();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Contact contact = contactListFilter.get(position);
        holder.tvContactName.setText(contact.getName());
        holder.tvContactSubName.setText(contact.getSubname());


        if (null != contactListFilter.get(position).getIsSelected()) {
            if (contactListFilter.get(position).getIsSelected().equals("Y")) {
                holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected));
            } else {
                holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselected));
            }
            /*if(contactTempList.contains(contactListFilter.get(position).getSubname())){
                holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected));
            } else {
                holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselected));
            }*/
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgContactRemove.getDrawable().getConstantState() == mContext.getResources().getDrawable(R.drawable.unselected).getConstantState()) {
                    contactListFilter.get(position).setIsSelected("Y");
                    //contactList.get(contactListString.indexOf(contactListFilter.get(position).getSubname())).setIsSelected("Y");
                   /* if(!contactTempList.contains(contactListFilter.get(position).getSubname())) {
                        contactTempList.add(contactListFilter.get(position).getSubname());
                    }*/
                    holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected));
                } else {
                    contactListFilter.get(position).setIsSelected("N");
                    //contactTempList.remove(contactListFilter.get(position).getSubname());
                   // contactList.get(contactListString.indexOf(contactListFilter.get(position).getSubname())).setIsSelected("N");
                    holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselected));
                }
            }
        });
        holder.imgContactRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.imgContactRemove.getDrawable().getConstantState() == mContext.getResources().getDrawable(R.drawable.unselected).getConstantState()) {
                    contactListFilter.get(position).setIsSelected("Y");
                    if(contactListString.indexOf(contactListFilter.get(position).getSubname())!=-1) {
                        contactList.get(contactListString.indexOf(contactListFilter.get(position).getSubname())).setIsSelected("Y");
                    }
                    holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected));
                } else {
                    contactListFilter.get(position).setIsSelected("N");
                    if(contactListString.indexOf(contactListFilter.get(position).getSubname())!=-1) {
                        contactList.get(contactListString.indexOf(contactListFilter.get(position).getSubname())).setIsSelected("N");
                    }
                    holder.imgContactRemove.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselected));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFilter.size();
    }

    public ArrayList<Contact> getModifyList() {
        return contactListFilter;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvContactName, tvContactSubName;
        ImageView imgContactRemove;

        public MyViewHolder(View view) {
            super(view);
            tvContactName = (TextView) view.findViewById(R.id.txtContactName);
            tvContactSubName = (TextView) view.findViewById(R.id.txtContactSubName);
            imgContactRemove = (ImageView) view.findViewById(R.id.imgContactRemove);
        }
    }



    /**
     * this class for filter data.
     */
    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                ArrayList<Contact> filters = new ArrayList<>();
                charSequence = charSequence.toString().toLowerCase();
                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getName().toLowerCase().contains(charSequence) || contactList.get(i).getSubname().toLowerCase().contains(charSequence)) {
                        /*Contact contact = new Contact();
                        contact.setUserName(contactList.get(i).getUserName());
                        contact.setName(contactList.get(i).getName());
                       // contact.setIsSelected(contactList.get(i).getIsSelected());
                        contact.setSubname(contactList.get(i).getSubname());*/
                        filters.add(contactList.get(i));

                    }
                }
                results.count = filters.size();
                results.values = filters;

            } else {
                results.count = contactList.size();
                results.values = contactList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            contactListFilter = (ArrayList<Contact>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}