package com.example.MindWaveLightOnOff;


import android.app.Activity;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
/*import android.content.Context;
import android.content.Intent;*/
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
/*import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;*/
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/*import android.view.KeyEvent;
import android.widget.Toast;*/

import com.neurosky.thinkgear.TGDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MyActivity extends Activity{


    public static int attention = -200;
    public static int callstate = 0;
    TGDevice tgDevice;
    BluetoothAdapter btAdapter;
    ProgressBar progressBar;
    TextView textView;
    LinearLayout linearLayout;
    Integer r;
    public static Integer theshold = 40;
    public static Thread progressBarThread;
    public static Thread callreceiveThread;
    public static Intent my_intent;
    boolean lightState = false;
    private Handler handler1 = new Handler();

    public static Context mContext;
    public static Context context;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean mindwave_mobile_off = true;

        my_intent = new Intent(MyActivity.this, MyService.class);
        getCallDetails();

        Log.v("TEST", "oncreate()");

        if (btAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(MyActivity.this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();

        } else {


            if (btAdapter.isEnabled()) {
                // Bluetooth is enable :)


                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

                for (BluetoothDevice bt : pairedDevices) {

                    //Toast.makeText(MyActivity.this,  bt.getName(), Toast.LENGTH_SHORT).show();
                    if ((bt.getName()).equals("MindWave Mobile")) {
                        mindwave_mobile_off = false;
                        //Toast.makeText(MyActivity.this, "MindWave Mobile on", Toast.LENGTH_SHORT).show();

                    }
                }

                if (mindwave_mobile_off) {
                    Toast.makeText(MyActivity.this, "MindWave Mobile off", Toast.LENGTH_SHORT).show();
                    //System.exit(0);
                } else {
                    Toast.makeText(MyActivity.this, "MindWave Mobile on", Toast.LENGTH_SHORT).show();
                }
                tgDevice = new TGDevice(btAdapter, handler);
                tgDevice.connect(true);

                tgDevice.start();
                Toast.makeText(MyActivity.this, "Bluetooth connecting", Toast.LENGTH_SHORT).show();

            } else {
                // Bluetooth is not enable :)
                btAdapter.enable();
                Toast.makeText(MyActivity.this, "Bluetooth turning on", Toast.LENGTH_SHORT).show();

                while (!btAdapter.isEnabled()) ;

                tgDevice = new TGDevice(btAdapter, handler);
                tgDevice.connect(true);
                tgDevice.start();
                Toast.makeText(MyActivity.this, "Bluetooth connecting", Toast.LENGTH_SHORT).show();

            }


        }


        //call receive
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                callstate = state;
                String number = incomingNumber;
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Toast.makeText(MyActivity.this, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();

                    //my_intent=new Intent(MyActivity.this,MyService.class);
                    my_intent.putExtra( MyService.ATTENTION , String.valueOf(attention) );
                    my_intent.putExtra( MyService.THESHOLD , String.valueOf(theshold) );

                    //startService(my_intent);
                    //callreceiveThread.start();


                }

                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //Toast.makeText(getApplicationContext(),"CALL_STATE_OFFHOOK",Toast.LENGTH_LONG).show();
                    Toast.makeText(MyActivity.this, "CALLING_STATE", Toast.LENGTH_SHORT).show();
                    callstate = state;
                }

                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    callstate = state;
                    Toast.makeText(getApplicationContext(), "CALL_STATE_IDLE", Toast.LENGTH_LONG).show();



                    try {
                        //my_intent = new Intent(MyActivity.this, MyService.class);

                       // stopService(my_intent);
                        //progressBarThread.interrupt();
                        //callreceiveThread.interrupt();
                        //Toast.makeText(getApplicationContext(),"CALL_STATE_IDLE try",Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(),"CALL_STATE_IDLE catch",Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, phoneStateListener.LISTEN_CALL_STATE);
    }





    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            Toast.makeText(MyActivity.this, "STATE_IDLE", Toast.LENGTH_SHORT).show();
                            break;
                        case TGDevice.STATE_CONNECTING:
                            Toast.makeText(MyActivity.this, "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case TGDevice.STATE_CONNECTED:
                            Toast.makeText(MyActivity.this, "STATE_CONNECTED", Toast.LENGTH_SHORT).show();
                            //tgDevice.start();
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            Toast.makeText(MyActivity.this, "STATE_DISCONNECTED", Toast.LENGTH_SHORT).show();
                            tgDevice.connect(true);
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                        case TGDevice.STATE_NOT_PAIRED:
                        default:
                            Toast.makeText(MyActivity.this, "STATE_NOT_PAIRED", Toast.LENGTH_SHORT).show();
                            tgDevice.connect(true);
                            tgDevice.start();
                            break;
                    }
                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    Log.v("TESTps", "PoorSignal: " + msg.arg1);
                case TGDevice.MSG_ATTENTION:
                    Log.v("TESTa", "Attention: " + msg.arg1);
                    MyActivity.attention=(msg.arg1);
                    //r=(Integer)(attention+155);

                    //linearLayout.setBackgroundColor(Color.rgb(r, 0, 0));
                    //Toast.makeText(MyActivity.this, "attention:"+attention, Toast.LENGTH_SHORT).show();
                    //System.out.print(attention);
                    if(attention<=100) {

                        if ((attention >= theshold) && (lightState == false)) {
                            linearLayout.setBackgroundResource(R.drawable.lighton);
                            lightState = true;
                        } else if ((attention < theshold) && (lightState == true)) {
                            linearLayout.setBackgroundResource(R.drawable.lightoff);
                            lightState = false;
                        }

                    }
                    progressBarThread=  new Thread(new Runnable() {
                        @Override
                        public void run() {

                            handler1.post(new Runnable() {
                                @Override
                                public void run() {

                                    progressBar.setProgress(attention);
                                    textView.setText("Attention:"+attention+"/"+progressBar.getMax());
                                }
                            });

                        }

                    });
                    progressBarThread.start();

                    if((MyActivity.attention>=50)&&(MyActivity.attention<=100) &&(MyActivity.callstate == 0)  ) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:01674370990"));
                            //Intent callIntent = new Intent(Intent.ACTION_ANSWER);

                            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            startActivity(callIntent);

                    }
//                    new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                Runtime.getRuntime().exec("input keyevent " +
//                                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
//                            } catch (IOException e) {
//                                // Runtime.exec(String) had an I/O problem, try to fall back
//                                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
//                                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
//                                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
//                                                KeyEvent.KEYCODE_HEADSETHOOK));
//                                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
//                                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
//                                                KeyEvent.KEYCODE_HEADSETHOOK));
//
//                                mContext.sendOrderedBroadcast(btnDown, enforcedPerm);
//                                mContext.sendOrderedBroadcast(btnUp, enforcedPerm);
//                            }
//                        }
//
//                    }).start();

//                    new Thread(new Runnable() {
//                        boolean flag_s=true;
//                        @Override
//                        public void run() {
//
//
//                                Toast.makeText(MyActivity.this, "try:", Toast.LENGTH_SHORT).show();
//
//                            if((MyActivity.attention>50)&&(MyActivity.attention<100)&&(callstate==0) ){
//                            Intent callIntent = new Intent(Intent.ACTION_CALL);
//                            callIntent.setData(Uri.parse("tel:0377778888"));
//                            //Intent callIntent = new Intent(Intent.ACTION_ANSWER);
//
//                            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

//                                //    Activity#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for Activity#requestPermissions for more details.
//                                return;
//                            }
//                            startActivity(callIntent);
//
//                                }
//
//
//                        }
//
//                    }).start();
                    break;
            /*    case TGDevice.MSG_RAW_DATA:
                    int rawValue = msg.arg1;
                    break;*/
                case TGDevice.MSG_BLINK:
                    //int blink = msg.arg1;
                    //Log.v("TESTb", "Blink: " +msg.arg1);
                    break;
           /*     case TGDevice.MSG_EEG_POWER:TESTb
                    // TGEegPower ep =(TGEegPower)msg.arg1;
                    // Log.v("HelloEEG", "Delta: " + ep.delta);*/
                default:
                    break;
            }
        }
    };


    public void getCallDetails() {

//        final String[] projection = null;
//        final String selection = null;
//        final String[] selectionArgs = null;
//        final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
//        Cursor cursor = null;
//        try{
//            cursor = context.getContentResolver().query(
//                    Uri.parse("content://call_log/calls"),
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder);
//            while (cursor.moveToNext()) {
//                String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
//                String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
//                String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
//                String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
//                String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
//
//                Toast.makeText(MyActivity.this, "callNumber:"+callNumber, Toast.LENGTH_SHORT).show();
//
////                if(Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0){
////                    if (_debug) Log.v("Missed Call Found: " + callNumber);
////                }
//            }
//        }catch(Exception ex){
//            //if (_debug) Log.e("ERROR: " + ex.toString());
//            Toast.makeText(MyActivity.this, "callNumber _debug ERROR", Toast.LENGTH_SHORT).show();
//
//        }finally{
//            cursor.close();
//        }
    }


}
