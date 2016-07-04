package com.mithi.videocutter.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.util.Locale;

/**
 * Created by mithilesh on 04/07/16.
 */

public class FFmpegUtils {
    private static final String TRIM_QUERY = " -acodec copy -f segment -segment_time %d -vcodec copy -reset_timestamps 1 -map 0 ";
    private static final String OUTPUT_DIR = "videoCutterOutput";

    private static void execFFmpegBinary(Context context, final String[] command, ExecuteBinaryResponseHandler handler) throws FFmpegCommandAlreadyRunningException {
        FFmpeg.getInstance(context).execute(command, handler);
    }

    public static void trimVideo(Context context, Uri path, int durationInSec, ExecuteBinaryResponseHandler handler) {
        String outputDirectory = getVideoCutterOutputDir();
        File outputFileDir = new File(outputDirectory);
        if (!outputFileDir.exists()) {
            outputFileDir.mkdir();
        }
        FileUtils.cleanDirectory(outputFileDir);
        String trim = "-i " + UriUtils.getPath(context, path) + String.format(Locale.getDefault(), TRIM_QUERY, durationInSec) + outputDirectory + "output_%d.mp4";
        String[] cmds = trim.split(" ");
        try {
            execFFmpegBinary(context, cmds, handler);
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public static String getVideoCutterOutputDir() {
        return Environment.getExternalStorageDirectory().getPath() + "/" + OUTPUT_DIR + "/";
    }
}
