package com.mycropimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageMediaAdapter extends RecyclerView.Adapter<ImageMediaAdapter.MyViewHolder> {
    public List<String> list;
    Context context;
    ClickImageListener clickImageListener;

    public ImageMediaAdapter(List<String> list, Context context, ClickImageListener clickImageListener) {
        this.list = list;
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
        Uri uri = Uri.parse(list.get(position));
        holder.image.setImageURI(uri);
    }

    public List<String> getList() {
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItem(String bean) {
        list.add(bean);
        notifyDataSetChanged();
    }
    public void updateItem(String uri, int position) {
        list.set(position, uri);
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
