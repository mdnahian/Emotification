package io.hackharvard.emotification;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mdislam on 10/27/16.
 */
public class EmotificationAPICall extends AsyncTask<String[], Void, String> {

    private OnAPICallComplete delegate;
    private static final String BASE_URL = "http://138.197.0.96";

    public EmotificationAPICall(OnAPICallComplete onAPICallComplete){
        this.delegate = onAPICallComplete;
    }

    @Override
    protected String doInBackground(String[]... params) {
        return sendRequest(params);
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.onTaskComplete(s);
    }

    private String sendRequest(String[][] params){
        StringBuilder tempBuffer = new StringBuilder();

        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            OutputStream os = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String data = "";

            for(String[] d : params){
                data += Uri.encode(d[0], "UTF-8")+"="+Uri.encode(d[1], "UTF-8")+"&";
            }

            bufferedWriter.write(data.substring(0, data.length()-1));
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int charRead;
            char[] inputBuffer = new char[500];

            while (true) {
                charRead = isr.read(inputBuffer);
                if (charRead <= 0) {
                    break;
                }

                tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
            }

            return tempBuffer.toString();


        } catch (IOException e) {
            Log.d("Crash", "Could not read data.");
        }

        return null;
    }

}
