package com.gmail.btheo95.aria.network;

import android.util.Pair;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by btheo on 23.11.2016.
 */


public class HttpFileUpload {

    private final static String LINE_END = "\r\n";
    private final static String TWO_HYPHENS = "--";
    private final static String BOUNDARY = "*****";

    private final static String TAG = HttpFileUpload.class.getSimpleName();

    private URL mURL;
    private InputStream mFileInputStream = null;
    private String mFileName = "unnamed";

    public HttpFileUpload(String urlString) throws MalformedURLException{
        try {
            mURL = new URL(urlString);
        } catch (MalformedURLException ex) {
//            Log.e("HttpFileUpload", "URL Malformatted");
            throw ex;
        }
    }

    public Pair<Integer, String> sendNow(InputStream fStream) throws IOException {
        mFileInputStream = fStream;
        return send();
    }

    public Pair<Integer, String> sendNow(File file) throws IOException {
        mFileName = file.getName();
        mFileInputStream = new FileInputStream(file);
        return send();
    }

    private Pair<Integer, String> send() throws IOException {
//        Log.v(TAG, "Starting Http File Sending to URL");

        // Open a HTTP connection to the URL
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        configureConnection(conn);

        //TODO: it waits too much
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream())) {
            doOutput(dataOutputStream);
        }

        int responseCode = conn.getResponseCode();
//        Log.v(TAG, "File Sent, Response: " + String.valueOf(responseCode));
        String responseString = "";

        // retrieve the response from server
        try (InputStream inputStream = conn.getInputStream()) {
            responseString = doInput(inputStream);
        }

        return new Pair<>(responseCode, responseString);
    }

    private String doInput(InputStream inputStream) throws IOException {
        int character;
        StringBuilder stringBuilder = new StringBuilder();

        while ((character = inputStream.read()) != -1) {
            stringBuilder.append((char) character);
        }

        String responseString = stringBuilder.toString();

//        Log.v("Response", responseString);

        return responseString;
    }

    private void doOutput(DataOutputStream dataOutputStream) throws IOException {

//            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
//            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"title\"" + LINE_END);
//            dataOutputStream.writeBytes(LINE_END);
//            dataOutputStream.writeBytes(Title);
//            dataOutputStream.writeBytes(LINE_END);

//            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
//            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"description\"" + LINE_END);
//            dataOutputStream.writeBytes(LINE_END);
//            dataOutputStream.writeBytes(Description);
//            dataOutputStream.writeBytes(LINE_END);

        dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + mFileName + "\"" + LINE_END);
        dataOutputStream.writeBytes(LINE_END);

//        Log.v(TAG, "Headers are written");

        // create a buffer of maximum size
        int bytesAvailable = mFileInputStream.available();

        int maxBufferSize = 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = mFileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
        }
        dataOutputStream.writeBytes(LINE_END);
        dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

        dataOutputStream.flush();
    }

    private void configureConnection(HttpURLConnection conn) throws ProtocolException {
        // Allow Inputs
        conn.setDoInput(true);

        // Allow Outputs
        conn.setDoOutput(true);

        // Don't use a cached copy.
        conn.setUseCaches(false);
//      conn.setRequestProperty("Cache-Control", "no-cache");

        conn.setRequestMethod("PUT");

        conn.setRequestProperty("Connection", "Keep-Alive");

        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
    }

}