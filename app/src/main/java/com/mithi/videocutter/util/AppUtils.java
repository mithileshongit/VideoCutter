package com.mithi.videocutter.util;

import android.util.Log;

import java.io.File;

public class AppUtils {
    private static final String TAG = "VideoCutterLastVideo";
    public static void logLastVideo() {
        File dir = new File(FFmpegUtils.getVideoCutterOutputDir());
        if (dir.exists()) {
            File[] list = dir.listFiles();
            if (list != null && list.length > 0) {
                Log.d(TAG, "" + list[list.length - 1].getName());
            }
        }
    }
}
