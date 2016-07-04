package com.mithi.videocutter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.mithi.videocutter.R;
import com.mithi.videocutter.model.VideoPreview;

import java.util.List;

public class VideoPreviewAdapter extends RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder> {
    private List<VideoPreview> mContent;

    public VideoPreviewAdapter(List<VideoPreview> content) {
        mContent = content;
    }

    @Override
    public VideoPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoPreviewAdapter.ViewHolder holder, int position) {
        VideoPreview videoPreview = getItem(position);
        holder.title.setText(videoPreview.getVideoFile().getName());
        Glide.with(holder.itemView.getContext()).load(videoPreview.getVideoFile()).centerCrop().into(holder.preview);
    }

    private VideoPreview getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView preview;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            preview = (ImageView)itemView.findViewById(R.id.preview);
        }
    }
}
