package com.cropimagedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {
    public List<MoreBean> list;
    Context context;
    ClickImageListener clickImageListener;

    public MediaAdapter(List<MoreBean> list, Context context, ClickImageListener clickImageListener) {
        this.list = list;
        this.context = context;
        this.clickImageListener = clickImageListener;
    }

    public interface ClickImageListener {
        void onClick(MoreBean moreBean, int position, String type);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder_media, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (list.get(position).getImage() == 1) {
            Bitmap bitmap = getThumbnailFromVideo(context, Uri.parse(list.get(position).getName()));
            if (bitmap != null) {
                holder.image.setImageBitmap(bitmap);
            } else {
                holder.image.setImageResource(R.drawable.ic_launcher_background);
            }
            holder.vid_icon.setVisibility(View.VISIBLE);
        } else {
            if (list.get(position).getBitmapImage() != null) {
//            if (list.get(position).getName().contains("://") || list.get(position).getName().contains("file:/") || list.get(position).getName().contains("cache") || list.get(position).getName().substring(0, 1).equalsIgnoreCase("/")) {
                holder.image.setImageBitmap(list.get(position).getBitmapImage());
            }
        }
        holder.vid_icon.setVisibility(View.GONE);

    }

    public List<MoreBean> getList() {
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItem(MoreBean bean) {
        list.add(bean);
        notifyDataSetChanged();
    }

    public void updateItem(String uri, int position) {
        list.get(position).setName(uri);
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

        ImageView image, crossImage, vid_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            crossImage = itemView.findViewById(R.id.delete);
            vid_icon = itemView.findViewById(R.id.vid_icon);

            crossImage.setOnClickListener(view -> clickImageListener.onClick(list.get(getAdapterPosition()), getAdapterPosition(), "remove"));

            itemView.setOnClickListener(v -> clickImageListener.onClick(list.get(getAdapterPosition()), getAdapterPosition(), "preview"));
        }
    }

    private Bitmap getThumbnailFromVideo(Context context, Uri uri) {
        String path = uri.getPath();//convert to path
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

}
