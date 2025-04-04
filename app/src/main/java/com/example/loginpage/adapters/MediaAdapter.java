package com.example.loginpage.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private List<Uri> mediaList;  // Change from List<String> to List<Uri>
    private final Context context;

    public MediaAdapter(Context context, List<Uri> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) { // Use MediaViewHolder here
        Uri mediaUri = mediaList.get(position);

        if (mediaUri.toString().contains("raw")) {  // Checking if it's a video
            holder.videoView.setVideoURI(mediaUri);
            holder.videoView.start();
            holder.videoView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
        } else {  // It's an image
            Glide.with(context).load(mediaUri).into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMedia);
            videoView = itemView.findViewById(R.id.videoViewMedia);
        }
    }
}
