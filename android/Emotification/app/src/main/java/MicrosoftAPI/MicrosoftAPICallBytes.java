package MicrosoftAPI;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.hackharvard.emotification.OnAPICallComplete;

/**
 * Created by mdislam on 10/23/16.
 */
public class MicrosoftAPICallBytes extends AsyncTask<byte[], Void, String> {

    OnAPICallComplete delegate;

    public MicrosoftAPICallBytes(OnAPICallComplete delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(byte[]... params) {
        return getResponse(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.onTaskComplete(s);
    }


    private String getResponse(byte[] image){
        StringBuilder tempBuffer = new StringBuilder();

        try {
            URL url = new URL("https://api.projectoxford.ai/emotion/v1.0/recognize");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", "f424ccad8ea3440ba81d2db885ea39ea");
            connection.getOutputStream().write(image);

            Log.d("Crash", "Uploading...");

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
