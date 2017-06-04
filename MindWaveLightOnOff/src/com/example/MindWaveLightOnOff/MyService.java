package com.example.MindWaveLightOnOff;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ASUS on 2/28/2016.
 */
public class MyService extends IntentService {

    public int recieved = 0;
    public static String ATTENTION = "0";
    public static String THESHOLD = "0";
    public static int CALLLOOP = 0;
    public static String PARAM_OUT_MSG = "0";
    public static String attention_list = "";
    public static String mediabutton_list_str = "";
    public static int mediabutton_list_int = 0;
    private static Context mContext;
    public static TelephonyManager tm;

    public MyService() {
        //super("MyService");
        super(MyService.class.getName());

        //tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //tm=(TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);


    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Service Started"+MyActivity.attention, Toast.LENGTH_LONG).show();
//        //Log.v("TEST", "onStartCommand ");
//        recieved=0;
//        return super.onStartCommand(intent, flags, startId);
//        //return START_STICKY;
//    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //String ATTENTION_STR = intent.getStringExtra(ATTENTION);
        //int ATTENTION_INT=Integer.parseInt( ATTENTION_STR);

        String THESHOLD_STR = intent.getStringExtra(THESHOLD);
        int THESHOLD_INT = Integer.parseInt(THESHOLD_STR);
        CALLLOOP = 0;
        attention_list = "";



        while (MyActivity.callstate == 0) {


            CALLLOOP = CALLLOOP + 1;
            attention_list = attention_list + " " + String.valueOf(MyActivity.attention);




            if((MyActivity.attention>50)&&(MyActivity.attention<100)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:01674370990"));
                        //Intent callIntent = new Intent(Intent.ACTION_ANSWER);

                            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            startActivity(callIntent);

            }


                //PARAM_OUT_MSG=" ATTENTION_STR:"+ATTENTION_STR+" THESHOLD_STR:"+THESHOLD_STR;


                //break;


                PARAM_OUT_MSG=" THESHOLD_STR:"+THESHOLD_INT;
        //Log.v("TEST", "onHandleIntent ");
     }//while end
    }







    @Override
    public void onDestroy() {
        super.onDestroy();

        //Log.v("TEST", "onDestroy() ");

        Toast.makeText(this,"onDestroy()"+"mediabutton_list_int:"+mediabutton_list_int,Toast.LENGTH_LONG).show();
        Toast.makeText(this,"onDestroy()"+"attention_list:"+attention_list,Toast.LENGTH_LONG).show();
        Toast.makeText(this,"onDestroy()"+"loop:"+CALLLOOP,Toast.LENGTH_LONG).show();
        Toast.makeText(this,"onDestroy()"+PARAM_OUT_MSG,Toast.LENGTH_LONG).show();

        if(recieved==10){
            //Toast.makeText(this,"Phone received by Concentration level",Toast.LENGTH_LONG).show();
            Toast.makeText(this,"Attention(onDestroy):"+MyActivity.attention,Toast.LENGTH_LONG).show();
            Toast.makeText(this,"theshold(onDestroy):"+MyActivity.theshold,Toast.LENGTH_LONG).show();
            Toast.makeText(this,"callstate(onDestroy):"+MyActivity.callstate,Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this,"Service Stoped",Toast.LENGTH_LONG).show();
        MyActivity.callstate=0;
        stopSelf();
    }




}
