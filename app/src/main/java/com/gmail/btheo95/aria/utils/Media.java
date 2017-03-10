package com.gmail.btheo95.aria.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by btheo on 16.11.2016.
 */

public class Media {

    private static final String TAG = Media.class.getSimpleName();
//    public static List<File> getAllGalleryPhotos(Context context) {
//        File[] DCIMDirs = getDCIMFolders(context);
//        List<File> photos = new ArrayList<>();
//
//        //parcurgere foldere DCIM
//        for (File DCIMDir : DCIMDirs) {
//            //Acces subfoldere DCIM
//            File[] DCIMSubDirs = DCIMDir.listFiles();
//            //parcurgere subfoldere
//            for (File DCIMSubDir : DCIMSubDirs) {
//                //daca nu sunt thumbnail
//                //le adauga in lista
//                if (!DCIMSubDir.getName().equals(".thumbnails") && !DCIMSubDir.isHidden()) {
//                    photos.addAll(getAllPhotosFromSpecificFiles(DCIMSubDir.listFiles()));
////                    photos.addAll(Arrays.asList(DCIMSubDir.listFiles()));
//                }
//            }
//        }
//
//        return photos;
//    }
//
//    private static Collection<? extends File> getAllPhotosFromSpecificFiles(File[] files) {
//        List<File> returnedList = new ArrayList<>();
//
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].isDirectory()) {
//                returnedList.addAll(getAllPhotosFromSpecificFiles(files[i].listFiles()));
//            } else if (pathIsPhoto(files[i].getPath())) {
//                returnedList.add(files[i]);
//            }
//        }
//        return returnedList;
//    }
//
//    public static boolean pathIsPhoto(String path) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//        if (options.outWidth != -1 && options.outHeight != -1) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static File[] getDCIMFolders(Context context) {
        File[] storagePaths = ContextCompat.getExternalFilesDirs(context, null);
        List<File> DCIMDirs = new ArrayList<>();
        for (File storagePath : storagePaths) {
            String data = storagePath.toString();
            int index = data.indexOf("Android");
            data = data.substring(0, index);
            File DCIMFile = new File(data + "DCIM/");

            if (DCIMFile.exists()) {
                DCIMDirs.add(DCIMFile);
                Log.d("Media", "DCIM found ");
            }

        }
        return DCIMDirs.toArray(new File[DCIMDirs.size()]);
    }


    @Nullable
    public static List<File> getFilesAfterDate(Date lastUpdate, List<File> fileList) {
        List<File> returnedList = new ArrayList<>();
        for (File file : fileList) {
            if (file.lastModified() > lastUpdate.getTime()) {
                returnedList.add(file);
            }
        }

        return returnedList;
    }


//    @Nullable
//    public static List<File> getPhotosAfterDate(Date lastUpdate, Context context) throws IOException {
//        List<File> allPhotos = getAllGalleryPhotos(context);
//        return getFilesAfterDate(lastUpdate, allPhotos);
//    }


    public static List<File> getMediaAfterDate(Date lastUpdate, Context context) throws IOException {
        List<File> DCIMContent = getAllDCIMContent(context);
        DCIMContent = getFilesAfterDate(lastUpdate, DCIMContent);
        DCIMContent = getMediaFromFiles(DCIMContent);
        return DCIMContent;
    }

    private static List<File> getMediaFromFiles(List<File> DCIMContent) {
        List<File> returnedList = new ArrayList<>();

        for (File file : DCIMContent) {
            String path = file.getAbsolutePath();
            if (isImageFile(path) || isVideoFile(path)) {
                returnedList.add(file);
            }
        }
        return returnedList;
    }

    private static List<File> getAllDCIMContent(Context context) {
        File[] DCIMFolders = getDCIMFolders(context);
        return getAllFilesRecursively(DCIMFolders);
    }

    private static List<File> getAllFilesRecursively(File[] files) {
        List<File> returnedList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                returnedList.addAll(getAllFilesRecursively(files[i].listFiles()));
            } else if (!(files[i].isHidden())) {
                returnedList.add(files[i]);
            }
        }
        return returnedList;
    }

    public static List<File> getVideoFiles(List<File> files) {
        List<File> returnedList = new ArrayList<>();
        for (File file : files) {
            if (isVideoFile(file.getPath())) {
                returnedList.add(file);
            }
        }
        return returnedList;
    }

    public static List<File> getPhotoFiles(List<File> files) {
        List<File> returnedList = new ArrayList<>();
        for (File file : files) {
            if (isImageFile(file.getPath())) {
                returnedList.add(file);
            }
        }
        return returnedList;
    }


    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static void updateData(Context context) {
        try {
            Database db = new Database(context);
            Date lastDate = db.getLastDate();
            Log.d(TAG, "Last sync date: " + lastDate.toString());

            List<File> mediaList = Media.getMediaAfterDate(lastDate, context);

            db.addFiles(mediaList);
            db.updateDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

