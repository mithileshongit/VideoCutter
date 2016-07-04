package com.mithi.videocutter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mithi.videocutter.R;
import com.mithi.videocutter.adapter.VideoPreviewAdapter;
import com.mithi.videocutter.model.VideoPreview;
import com.mithi.videocutter.util.AppUtils;
import com.mithi.videocutter.util.FFmpegUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = (RecyclerView)getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new VideoPreviewAdapter(getVideoPreviews()));
        getView().findViewById(R.id.log_last_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.logLastVideo();
            }
        });
    }

    private List<VideoPreview> getVideoPreviews() {
        List<VideoPreview> list = new ArrayList<>();

        File cutterOutputDir = new File(FFmpegUtils.getVideoCutterOutputDir());

        for (File file: cutterOutputDir.listFiles()) {
            VideoPreview videoPreview = new VideoPreview(file);
            list.add(videoPreview);
        }

        return list;
    }
}
