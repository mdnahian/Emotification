package io.hackharvard.emotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Locale;


/**
 * Created by mdislam on 10/21/16.
 */
public class NotificationService extends NotificationListenerService {

    private Context context;
    private TextToSpeech t1;


    private boolean isRecognition;
    private boolean isTTS;

    private String nfis = "";


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        SharedPreferences sp1 = this.getSharedPreferences("imagerecog", 0);
        isRecognition = sp1.getBoolean("imagerecog", false);

        SharedPreferences sp2 = this.getSharedPreferences("texttospeech", 0);
        isTTS = sp2.getBoolean("texttospeech", false);

        t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Crash", "This Language is not supported");
                    }
                }
            }});
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        final String title = extras.getString("android.title");
        final String text = extras.getCharSequence("android.text").toString().replace(nfis, "");

        nfis += text;


//        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
//        final Bitmap id = sbn.getNotification().largeIcon;

        Intent notification = new Intent("notification");
        notification.putExtra("package", pack);
        notification.putExtra("ticker", ticker);
        notification.putExtra("title", title);
        notification.putExtra("text", text);

        SharedPreferences sp1 = this.getSharedPreferences("imagerecog", 0);
        isRecognition = sp1.getBoolean("imagerecog", false);

        SharedPreferences sp2 = this.getSharedPreferences("texttospeech", 0);
        isTTS = sp2.getBoolean("texttospeech", false);

        if(pack.equals("com.google.android.apps.messaging") && text.length() <= 200) {

            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                @Override
                public void run() {

                    //                if(id != null){
                    //                    MicrosoftAPICallBytes microsoftAPICallBytes = new MicrosoftAPICallBytes(new OnAPICallComplete(){
                    //                        @Override
                    //                        public void onTaskComplete(String response) {
                    //                            if(response != null){
                    //                                Log.d("Crash", response);
                    //                            }
                    //                        }
                    //                    });
                    //
                    //                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //                    id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //                    byte[] byteArray = stream.toByteArray();
                    //                    microsoftAPICallBytes.execute(byteArray);
                    //
                    //                } else {
                    MicrosoftAPICall microsoftAPICall = new MicrosoftAPICall(new OnAPICallComplete() {
                        @Override
                        public void onTaskComplete(String response) {
                            JSONParser jsonParser = new JSONParser(response);
                            MicrosoftAPIResponse microsoftAPIResponse = jsonParser.getParsedResponse();

                            MicrosoftAPICall microsoftAPICall1 = new MicrosoftAPICall(new OnAPICallComplete() {
                                @Override
                                public void onTaskComplete(String response) {
                                    ding(response, text);
                                }
                            });

                            try {
                                microsoftAPICall1.execute("request", microsoftAPIResponse.getScore(), text);
                            } catch (NullPointerException e) {
                                Log.d("Crash", "Did not get a response");
                            }
                        }
                    });

                    microsoftAPICall.execute("sentiment", title, text);
                }

                //            }
            });

            LocalBroadcastManager.getInstance(context).sendBroadcast(notification);

        }
    }


    private void ding(String url, final String text){
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(isTTS) {
                        t1.speak(text, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            });

            player.prepare();
            player.start();
            player.setLooping(false);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        nfis = "";
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }
}
