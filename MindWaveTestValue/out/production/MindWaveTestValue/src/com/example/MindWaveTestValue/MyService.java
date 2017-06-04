package com.example.MindWaveTestValue;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by ASUS on 2/28/2016.
 */
public class MyService extends IntentService{

    public int recieved=0;
    public MyService() {


        super("My Worker Thread");
        Log.v("TEST", "MyService()");
        recieved=0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.v("TEST", "onStartCommand ");
        recieved=0;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v("TEST", "onDestroy() ");
        if(recieved==10){ Toast.makeText(this,"Phone received by Concentration level",Toast.LENGTH_LONG).show();}
        Toast.makeText(this,"Service Stoped",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while ((MyActivity.callstate==1)){




            if((MyActivity.attention>=60)) {
                Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                getApplicationContext().sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
                recieved=10;
                 break;
            }
        Log.v("TEST", "onHandleIntent ");
        }
    }
}
