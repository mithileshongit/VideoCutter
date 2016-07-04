package com.mithi.videocutter.util;

import java.io.File;

public class FileUtils {

    public static boolean cleanDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return true;
            }
            for (File file : files) {
                file.delete();
            }
            return true;
        }
        return false;
    }
}
