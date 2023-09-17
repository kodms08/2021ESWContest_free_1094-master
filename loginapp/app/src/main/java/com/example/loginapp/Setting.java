package com.example.loginapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends AppCompatActivity {
    Button btn_vr;
    Button btn_sd;
    Button btn_si;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

    btn_vr = findViewById(R.id.btn_vr);
    btn_sd = findViewById(R.id.btn_sd);
    btn_si = findViewById(R.id.btn_si);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //AudioManager  mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(getApplicationContext(), "권한을 허용해주세요", Toast.LENGTH_LONG).show();

            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }
            btn_vr.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O_MR1)
        @Override
        public void onClick(View v) {


                AudioManager audioManager;
                audioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    // 벨소리 모드일 경우
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    // 진동 모드일 경우
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    // 무음 모드일 경우
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                }

        }
    });
    }
}
