package io.hackharvard.emotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by mdislam on 10/21/16.
 */
public class NotificationService extends NotificationListenerService {

    private Context context;
    private TextToSpeech t1;

    private boolean isEnabled;
    private boolean isRecognition;
    private boolean isTTS;

    private ArrayList<String> notifications;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        notifications = new ArrayList<>();

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

        SharedPreferences sp0 = this.getSharedPreferences("enabled", 0);
        isEnabled = sp0.getBoolean("enabled", false);

        if(!isEnabled){
            stopSelf();
        } else {


            String pack = sbn.getPackageName();
            String ticker ="";
            if(sbn.getNotification().tickerText !=null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            final String title = extras.getString("android.title");
            final String text = extras.getCharSequence("android.text").toString();

            Intent notification = new Intent("notification");
            notification.putExtra("package", pack);
            notification.putExtra("ticker", ticker);
            notification.putExtra("title", title);
            notification.putExtra("text", text);

            SharedPreferences sp3 = this.getSharedPreferences("imagerecog", 0);
            isRecognition = sp3.getBoolean("imagerecog", false);

            SharedPreferences sp4 = this.getSharedPreferences("texttospeech", 0);
            isTTS = sp4.getBoolean("texttospeech", false);


//        SharedPreferences sp1 = this.getSharedPreferences("imagerecog", 0);
//        isRecognition = sp1.getBoolean("imagerecog", false);

            if(text.length() <= 200 && (pack.toLowerCase().contains("mms") || pack.toLowerCase().contains("sms") || pack.toLowerCase().contains("messenger") || pack.toLowerCase().contains("messaging"))) {


                if(isClean(text)) {

//                    Log.d("Crash", text);

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            EmotificationAPICall call = new EmotificationAPICall(new OnAPICallComplete() {
                                @Override
                                public void onTaskComplete(String response) {
                                    notifications.add(text);
                                    ding(response, text);
                                }
                            });

                            call.execute(buildData("title", title), buildData("text", text), buildData("filename", generateRandom()));
                        }

                        //            }
                    });

                    LocalBroadcastManager.getInstance(context).sendBroadcast(notification);

                }

            }



        }
    }

    private boolean isClean(String text){

        try {
            String[] parts = text.split(", ");
            for (String part : parts) {
                if (contactExists(part)) {
                    return false;
                } else if (part.matches(".[0-9][0-9][0-9]. [0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]")) {
                    return false;
                }
            }
        } catch (Exception e) {
            //TODO: Log out
        }

        return true;
    }



    public boolean contactExists(String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
            Cursor cur = getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
            return false;
        } else {
            return false;
        }
    }




    private String[] buildData(String name, String content){
        String[] d = new String[2];
        d[0] = name;
        d[1] = content;

        return d;
    }



    private void ding(final String url, final String text){

        SharedPreferences sp0 = this.getSharedPreferences("volume", 0);
        int volume = sp0.getInt("volume", 50);

        try{
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            final int notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            final int soundVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            Log.d("Crash", Integer.toString(volume));

            float av = ( ((float) volume) / 100 ) * 15;

            Log.d("Crash", Float.toString(av));

            int auto_volume = (int) av;

            Log.d("Crash", Integer.toString(auto_volume));

            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, auto_volume, 0);

            final MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isTTS) {
                        t1.speak(text, TextToSpeech.QUEUE_ADD, null);
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationVolume, 0);
                    } else {
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationVolume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, soundVolume, 0);
                    }

                    player.release();
//                    cancelAllNotifications();
                }
            });

            player.prepare();
            player.start();
            player.setLooping(false);

        } catch (Exception e) {
            Log.d("Crash", e.getMessage());
            Log.d("Crash", "Failed to play sound");
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    private static String generateRandom(){
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }



    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }
}
