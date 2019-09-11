package com.example.pdfviewerusingjs.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdfviewerusingjs.R;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ImageListRecyclerAdapter extends RecyclerView.Adapter<ImageListRecyclerAdapter.ImageListViewHolder> {
    private Activity activity;
    private ArrayList<Bitmap> imageList;

    public ImageListRecyclerAdapter(Activity activity, ArrayList<Bitmap> imageList) {
        this.activity = activity;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_image_list, parent, false);
        return new ImageListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
        holder.mContainer.setImageBitmap(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView mContainer;
        ImageListViewHolder(@NonNull View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.imageContainer);
        }
    }
}
