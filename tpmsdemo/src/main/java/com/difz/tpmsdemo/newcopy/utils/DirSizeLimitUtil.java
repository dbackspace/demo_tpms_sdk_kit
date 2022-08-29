package com.difz.tpmsdemo.newcopy.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class DirSizeLimitUtil {
    static String TAG = "DirSizeLimitUtil";
    private double limitSize;
    private String mDir;

    public DirSizeLimitUtil(String dir, double maxSize) {
        this.mDir = "";
        this.limitSize = -2.147483648E9d;
        this.mDir = dir;
        String str = TAG;
        android.util.Log.w(str, "Set limitSize:" + maxSize);
        this.limitSize = maxSize;
    }

    public static double calcTotalSize(String path) {
        double mDvrSize = 0.0d;
        try {
            File file = new File(path);
            mDvrSize = getFileSize(file);
        } catch (Exception e) {
        }
        String str = TAG;
        android.util.Log.i(str, "mDvrSize files " + mDvrSize);
        return mDvrSize;
    }

    public static double getFileSize(File f) throws Exception {
        double length;
        double size = 0.0d;
        File[] flist = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                length = getFileSize(flist[i]);
            } else {
                length = ((float) flist[i].length()) * 1.0f;
                Double.isNaN(length);
            }
            size += length;
        }
        return size;
    }

    private double calcDvrSize() {
        double mDvrSize = 0.0d;
        try {
            File file = new File(this.mDir);
            File[] files = file.listFiles();
            for (File f : files) {
                double length = ((float) f.length()) * 1.0f;
                Double.isNaN(length);
                mDvrSize += length;
            }
        } catch (Exception e) {
        }
        android.util.Log.i(TAG, "mDvrSize files " + mDvrSize);
        return mDvrSize;
    }

    public void sizeProc() {
        double limitSizeok = this.limitSize;
        String str = TAG;
        android.util.Log.i(str, "limitSizeOK " + limitSizeok);
        if (calcDvrSize() > limitSizeok) {
            String str2 = TAG;
            android.util.Log.i(str2, "overy limitSize " + limitSizeok);
            deleteOldFile();
            sizeProc();
        }
    }

    private void deleteOldFile() {
        File file = new File(this.mDir);
        File[] files = file.listFiles();
        if (files != null && files.length > 0 && files[0] != null) {
            List<File> filels = Arrays.asList(files);
            Collections.sort(filels, new Comparator<File>() { // from class: com.tpms.utils.DirSizeLimitUtil.1
                @Override // java.util.Comparator
                public int compare(File file1, File file2) {
                    if (file1.lastModified() < file2.lastModified()) {
                        return -1;
                    }
                    if (file1.lastModified() > file2.lastModified()) {
                        return 1;
                    }
                    return 0;
                }
            });
            filels.get(0).delete();
        }
    }
}