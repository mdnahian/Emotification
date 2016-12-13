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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView instructionsBtn = (TextView) findViewById(R.id.instructionsBtn);
        instructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstructionsActivity.class));
            }
        });

        final BroadcastReceiver onNotice = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
            }
        };

        SharedPreferences sp0 = this.getSharedPreferences("volume", 0);
        int volume = sp0.getInt("volume", 50);

        SeekBar seekBar = (SeekBar) findViewById(R.id.volumeBar);
        seekBar.setProgress(volume);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saveVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final Switch aSwitch = (Switch) findViewById(R.id.enableBtn);

        if(isMyServiceRunning(NotificationService.class) && isEnabled()){
            aSwitch.setChecked(true);
        }


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(checkNotificationEnabled()){
                        saveOption("enabled");
                        Toast.makeText(MainActivity.this, "Emotification Enabled", Toast.LENGTH_SHORT).show();
                        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter("notification"));
                    } else {
                        Toast.makeText(MainActivity.this, "Please enable notifications", Toast.LENGTH_SHORT).show();
                        aSwitch.setChecked(false);
                        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                } else {
                    clear("enabled");
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
//                    saveOption("imagerecog");
//                    Toast.makeText(MainActivity.this, "Enabled Image Recognition", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Image Recognition Coming Soon", Toast.LENGTH_SHORT).show();
                    bSwitch.setChecked(false);
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

        requestPermission();

    }



    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
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


    public void saveVolume(int volume){
        SharedPreferences sp1 = this.getSharedPreferences("volume", 0);
        SharedPreferences.Editor ed = sp1.edit();
        ed.putInt("volume", volume);
        ed.apply();
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


    private Boolean isEnabled(){
        SharedPreferences sp0 = this.getSharedPreferences("enabled", 0);
        return sp0.getBoolean("enabled", false);
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




}
