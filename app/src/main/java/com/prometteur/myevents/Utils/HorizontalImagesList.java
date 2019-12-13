package com.prometteur.myevents.Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.myevents.R;

import java.util.List;

public class HorizontalImagesList extends RecyclerView.Adapter<HorizontalImagesList.BookDateSingletonViewHolder>{
    private List<Bitmap> horizontalGrocderyList;
    Context context;
    int row_index=-1;

    public HorizontalImagesList(List<Bitmap> horizontalGrocderyList, Context context){
        this.horizontalGrocderyList= horizontalGrocderyList;
        this.context = context;
    }

    @Override
    public BookDateSingletonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View BookDateSingletonProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ll_images, parent, false);
        BookDateSingletonViewHolder gvh = new BookDateSingletonViewHolder(BookDateSingletonProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final BookDateSingletonViewHolder holder, final int position) {

        if(horizontalGrocderyList.size()>0) {

            holder.imageToUpload.setImageBitmap(horizontalGrocderyList.get(position));
        }


    }

    @Override
    public int getItemCount() {
        int size=0;
        if(horizontalGrocderyList.size()>0)
            size=horizontalGrocderyList.size();
        return size;
    }

    public class BookDateSingletonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageToUpload;
        public BookDateSingletonViewHolder(View view) {
            super(view);
            imageToUpload=view.findViewById(R.id.imageToUpload);
        }
    }
}