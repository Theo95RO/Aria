package com.gmail.btheo95.aria;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by btheo on 16.11.2016.
 */

public class Utils {

    public static long getDeviceImei(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return Long.parseLong(imei);
    }


    public static List<File> getAllGalleryPhotos(Context context) {
        File[] dcimDirs = getDcimFolders(context);
        List<File> photos = new ArrayList<>();

        //parcurgere foldere DCIM
        for (File dcimDir : dcimDirs) {
            //Acces subfoldere DCIM
            File[] dcimSubDirs = dcimDir.listFiles();
            //parcurgere subfoldere
            for (File dcimSubDir : dcimSubDirs) {
                //daca nu sunt thumbnail
                //le adauga in lista
                if (!dcimSubDir.getName().equals(".thumbnails") && !dcimSubDir.isHidden()) {
                    photos.addAll(getAllPhotosFromSpecificFiles(dcimSubDir.listFiles()));
//                    photos.addAll(Arrays.asList(dcimSubDir.listFiles()));
                }
            }
        }

        return photos;
    }

    private static Collection<? extends File> getAllPhotosFromSpecificFiles(File[] files) {
        List<File> returnedList = new ArrayList<>();

        for (int i = 0 ; i < files.length; i++) {
            if (files[i].isDirectory()) {
                returnedList.addAll(getAllPhotosFromSpecificFiles(files[i].listFiles()));
            } else if (pathIsPhoto(files[i].getPath())){
                returnedList.add(files[i]);
            }
        }
        return returnedList;
    }

    public static boolean pathIsPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (options.outWidth != -1 && options.outHeight != -1) {
            return true;
        }
        else {
            return false;
        }
    }

    public static File[] getDcimFolders(Context context) {
        File[] storagesPaths = ContextCompat.getExternalFilesDirs(context, null);
        List<File> dcimDirs = new ArrayList<>();
        for (File storagesPath : storagesPaths) {
            String data = storagesPath.toString();
            int index = data.indexOf("Android");
            data = data.substring(0, index);
            File dcimFile = new File(data + "DCIM/");

            if (dcimFile.exists()) {
                dcimDirs.add(dcimFile);
                Log.d("Utils", "DCIM found ");
            }

        }
        return dcimDirs.toArray(new File[dcimDirs.size()]);
    }


    @Nullable
    public static List<File> getFilesAfterDate(Date lastUpdate, List<File> fileList) {
        List<File> returnedList = new ArrayList<>();
        for(File file: fileList){
            if(file.lastModified() > lastUpdate.getTime()){
                returnedList.add(file);
            }
        }

        return returnedList;
    }


    @Nullable
    public static List<File> getPhotosAfterDate(Date lastUpdate, Context context) throws IOException {
        List<File> allPhotos = getAllGalleryPhotos(context);
        return getFilesAfterDate(lastUpdate, allPhotos);
    }

}

