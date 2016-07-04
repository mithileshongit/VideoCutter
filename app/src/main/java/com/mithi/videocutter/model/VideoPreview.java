package com.mithi.videocutter.model;


import java.io.File;

public class VideoPreview {
    private File mVideoFile;

    public VideoPreview(File file) {
        mVideoFile = file;
    }

    public File getVideoFile() {
        return mVideoFile;
    }
}
