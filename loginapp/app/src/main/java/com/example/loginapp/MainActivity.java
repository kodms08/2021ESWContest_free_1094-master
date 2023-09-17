package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.net.Uri;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;//권한

    private ImageButton sub_info;
    private ImageButton sub_service;
    private ImageButton sub_ex;

    TextView TextView;
    EditText et_phone1;
    EditText et_phone2;
    private Socket socket;
    private cm_thread thread;
    private InputStream IS;
    private PrintWriter OS;
    private int port = 9000; //서버랑 꼭 포트 번호 같게 만들어주고
    private String SERVER_IP = "192.168.137.9";  //서버 아이피 주소 적어주세요!



    private String call_num = "";


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder= null;


        // powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);

        //wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        sub_info=findViewById(R.id.sub_info);
        sub_service= findViewById(R.id.sub_service);
        sub_ex=findViewById(R.id.sub_ex);
        sub_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, Myinfo.class);
                Intent intent3 = getIntent();
                String userID= intent3.getStringExtra("userID");
                String userPassword= intent3.getStringExtra("userPassword");
                String userName = intent3.getStringExtra("userName");
                String userCall = intent3.getStringExtra("userCall");
                intent2.putExtra("userID",userID);
                intent2.putExtra("userPassword",userPassword);
                intent2.putExtra("userName",userName);
                intent2.putExtra("userCall",userCall);
                startActivity(intent2);

            }
        });
        sub_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Service.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userPassword = intent.getStringExtra("userPassword");

        String userWord = intent.getStringExtra("userWord");
        String userName = intent.getStringExtra("userName");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            socket = new Socket(SERVER_IP, port);
            OS = new PrintWriter(socket.getOutputStream(),true);
            Log.d("socket","socket");
        }catch(Exception e){
            e.printStackTrace();
        }
        thread = new cm_thread();
        thread.start();
        OS.println(userWord);
        //OS.println("connect success");
        Log.d("connect success","connect success");

        TextView = (TextView)findViewById(R.id.TextView);
        sub_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,Setting.class);
                startActivity(intent1);
            }
        });

        //살짝 바꿨는데 잘 모르겠어요...

        if(TextView != null)
        {

            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setIcon(R.mipmap.ic_launcher);
            ad.setTitle("위험");
            ad.setMessage("위험상황입니까?");

            final EditText et = new EditText(MainActivity.this);
            ad.setView(et);

            ad.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String tel = "tel:"+call_num;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(tel));

                        startActivity(intent);

                    //startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));


                }

            });

            ad.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }




    }


    class cm_thread extends Thread
    {
        @Override
        public void run() {
            byte[] buf = new byte[1024];
            String data = null;
            while(true)
            {
                try {
                    IS = socket.getInputStream();
                    DataInputStream DIS = new DataInputStream(IS);
                    DIS.read(buf, 0, 1024);
                    final String redata = new String(buf, 0, 1024);
                    if (data != redata) {
                        TextView.setText(redata);

                    }
                    Log.d("data", "data : " + redata);


                    runOnUiThread(() -> {

                        if (redata.contains("help")) {



                            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                            ad.setIcon(R.mipmap.ic_launcher);
                            ad.setTitle("위험");
                            ad.setMessage("위험상황입니까?");


                            final EditText et = new EditText(MainActivity.this);
                            ad.setView(et);




                            ad.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String result="";
                                    if(redata.contains("crime"))
                                        result = "범죄";
                                    else if(redata.contains("fire"))
                                        result = "화재";
                                    else if(redata.contains("health"))
                                        result = "건강위험";
                                    OS.println(result);//위험상황 맞으면 1보냄
                                    
//                                    OS.close();
                                    Intent intent3 = getIntent();
                                    String userName = intent3.getStringExtra("userName");
                                    String sos = "살려주세요!! "+result+"상황입니다 ! 주소는";
                                    String sendmessage = sos+userName;

                                    String userCall = intent3.getStringExtra("userCall");
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(userCall,null,sendmessage,null,null);


                                  //  String tel = "tel:"+call_num;
                                   // Intent intent = new Intent(Intent.ACTION_CALL);
                                   // intent.setData(Uri.parse(tel));

                                   // startActivity(intent);
                                    //                    OS.flush();
                                }
                            });


                            ad.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    OS.println("0"); //아니면 0보냄
//                                    OS.close();
                                }

                            });
                            ad.show();
                        }
                        else if(redata.contains("timeout"))
                        {
                            Intent intent3 = getIntent();
                            String userName = intent3.getStringExtra("userName");
                            String userCall = intent3.getStringExtra("userCall");
                            String sos = "살려주세요!위험상황입니다 주소는 :";
                            String sendmessage = sos+userName;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(userCall,null,sendmessage,null,null);

                        }
                    });
                }catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }

    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            socket.close(); //소켓을 닫는다.
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}