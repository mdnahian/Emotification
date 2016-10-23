package io.hackharvard.emotification;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String noti = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final TextView notifications = (TextView) findViewById(R.id.notifications);

        final BroadcastReceiver onNotice = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");

                noti += title+" - "+text+"\n";

//                notifications.setText(noti);
            }
        };



        final Switch aSwitch = (Switch) findViewById(R.id.enableBtn);

        if(isMyServiceRunning(NotificationService.class)){
            aSwitch.setChecked(true);
        }


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(checkNotificationEnabled()){
                        Toast.makeText(MainActivity.this, "Emotification Enabled", Toast.LENGTH_SHORT).show();
                        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter("notification"));
                    } else {
                        Toast.makeText(MainActivity.this, "Please enable notifications", Toast.LENGTH_SHORT).show();
                        aSwitch.setChecked(false);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Emotification Disabled", Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onNotice);
                }
            }
        });

        final Switch bSwitch = (Switch) findViewById(R.id.imageBtn);

        SharedPreferences sp1 = this.getSharedPreferences("imagerecog", 0);
        if(sp1.getBoolean("imagerecog", false)){
            bSwitch.setChecked(true);
        }

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveOption("imagerecog");
                    Toast.makeText(MainActivity.this, "Enabled Image Recognition", Toast.LENGTH_SHORT).show();
                } else {
                    clear("imagerecog");
                    Toast.makeText(MainActivity.this, "Disabled Image Recognition", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final Switch cswitch = (Switch) findViewById(R.id.textBtn);

        SharedPreferences sp2 = this.getSharedPreferences("texttospeech", 0);
        if(sp2.getBoolean("texttospeech", false)){
            cswitch.setChecked(true);
        }


        cswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    saveOption("texttospeech");
                    Toast.makeText(MainActivity.this, "Enabled Text to Speech", Toast.LENGTH_SHORT).show();
                } else {
                    clear("texttospeech");
                    Toast.makeText(MainActivity.this, "Disabled Text to Speech", Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkPermissions();

    }


    public void saveOption(String option){
        SharedPreferences sp1 = this.getSharedPreferences(option, 0);
        SharedPreferences.Editor ed = sp1.edit();
        ed.putBoolean(option, true);
        ed.apply();
    }



    public void clear(String option){
        SharedPreferences sp = this.getSharedPreferences(option, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.commit();
    }



    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        101);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public boolean checkNotificationEnabled() {
        try{
            if(Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners").contains(getPackageName())) {
                return true;
            } else {
                return false;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }







    private void destroyService(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int count = 0;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                manager.killBackgroundProcesses(service.service.getPackageName());
                Process.killProcess(count);
                break;
            }
            count++;
        }
    }




}
