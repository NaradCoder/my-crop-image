package com.mycropimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class ImageMediaAdapter extends RecyclerView.Adapter<ImageMediaAdapter.MyViewHolder> {
    public List<String> list;
    public List<Bitmap> bitmapList;
    Context context;
    ClickImageListener clickImageListener;

    public ImageMediaAdapter(List<String> list, List<Bitmap> bitmapList, Context context, ClickImageListener clickImageListener) {
        this.list = list;
        this.bitmapList = bitmapList;
        this.context = context;
        this.clickImageListener = clickImageListener;
    }

    public interface ClickImageListener {
        void onClick(String strUri, int position, String type);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_media, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.image.setImageBitmap(bitmapList.get(position));
    }

    public List<String> getList() {
        return list;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItem(String uri, Bitmap bitmap) {
        list.add(uri);
        bitmapList.add(bitmap);
        notifyDataSetChanged();
    }

    public void updateItem(String uri, Bitmap bitmap, int position) {
        list.set(position, uri);
        bitmapList.set(position, bitmap);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image, crossImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            crossImage = itemView.findViewById(R.id.delete);

            crossImage.setOnClickListener(view -> clickImageListener.onClick(list.get(getAdapterPosition()), getAdapterPosition(), "remove"));

            itemView.setOnClickListener(v -> clickImageListener.onClick(list.get(getAdapterPosition()), getAdapterPosition(), "crop"));
        }
    }

}
