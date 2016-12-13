package MicrosoftAPI;

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

import io.hackharvard.emotification.OnAPICallComplete;

/**
 * Created by mdislam on 10/22/16.
 */
public class MicrosoftAPICall extends AsyncTask<String, Void, String> {

    private OnAPICallComplete delegate;

    public MicrosoftAPICall(OnAPICallComplete delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        if(params[0] == "sentiment"){
            return getResponse(params[1], params[2]);
        } else if(params[0] == "request") {
            return sendRequest(params[1], params[2]);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.onTaskComplete(s);
    }


    private String getResponse(String title, String text){
        StringBuilder tempBuffer = new StringBuilder();

        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            StringBuilder postData = new StringBuilder();
            postData.append("{");
            postData.append("\"documents\": [");
            postData.append("{");
            postData.append("\"id\": \""+title+"\",");
            postData.append("\"text\": \""+text+"\"");
            postData.append("} ] }");

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");


            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", "aa66e2524b4c49bab89d93ffc1aa8ad6");
            connection.getOutputStream().write(postDataBytes);


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



    private String sendRequest(String score, String text){
        StringBuilder tempBuffer = new StringBuilder();

        try {
            URL url = new URL("http://54.163.25.89:80");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            OutputStream os = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String data = Uri.encode("score", "UTF-8")+"="+Uri.encode(score, "UTF-8")+"&"+Uri.encode("filename", "UTF-8")+"="+Uri.encode(generateRandom(), "UTF-8")+"&"+Uri.encode("text", "UTF-8")+"="+Uri.encode(text, "UTF-8");

            bufferedWriter.write(data);
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



    private static String generateRandom(){
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }



}
