package com.example.MindWaveTestValue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
/*import android.content.Context;
import android.content.Intent;*/
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
/*import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;*/
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/*import android.view.KeyEvent;
import android.widget.Toast;*/

import com.neurosky.thinkgear.TGDevice;

public class MyActivity extends Activity {


    public static int attention=0;
    public static int callstate=0;
    TGDevice tgDevice;
    BluetoothAdapter btAdapter;
    ProgressBar progressBar;
    TextView textView;
    LinearLayout linearLayout;
    Integer r;
    private  Handler handler1=new Handler();//handler use to handle  progressBar
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        textView=(TextView)findViewById(R.id.textView);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null) {
            tgDevice = new TGDevice(btAdapter, handler);

        }

        tgDevice.connect(true);
        tgDevice.start();

        //call receive
        PhoneStateListener phoneStateListener=new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                callstate=state;
                String number=incomingNumber;
                if (state== TelephonyManager.CALL_STATE_RINGING){
                    Toast.makeText(MyActivity.this, "Phone is Ringing", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(MyActivity.this,MyService.class);
                    startService(intent);

                }

                if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                    Toast.makeText(getApplicationContext(),"Phone is Currently in a call",Toast.LENGTH_LONG).show();
                }

                if(state==TelephonyManager.CALL_STATE_IDLE){
                    Toast.makeText(getApplicationContext(),"Phone is neither ringing nor call",Toast.LENGTH_LONG).show();
                    try {
                        Intent intent = new Intent(MyActivity.this, MyService.class);
                        stopService(intent);
                    }catch (Exception e){
                        e.printStackTrace();
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
             /*       switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                        case TGDevice.STATE_NOT_PAIRED:
                        default:
                            break;
                    }*/
                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    Log.v("TESTps", "PoorSignal: " + msg.arg1);
                case TGDevice.MSG_ATTENTION:
                    Log.v("TESTa", "Attention: " + msg.arg1);
                    attention=(msg.arg1);
                    r=(Integer)(attention+155);

                    linearLayout.setBackgroundColor(Color.rgb(r, 0, 0));


                    new Thread(new Runnable() {
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

                    }).start();
                    break;
            /*    case TGDevice.MSG_RAW_DATA:
                    int rawValue = msg.arg1;
                    break;*/
                case TGDevice.MSG_BLINK:
                    int blink = msg.arg1;
                    Log.v("TESTb", "Blink: " +msg.arg1);
                    break;
           /*     case TGDevice.MSG_EEG_POWER:TESTb
                    // TGEegPower ep =(TGEegPower)msg.arg1;
                    // Log.v("HelloEEG", "Delta: " + ep.delta);*/
                default:
                    break;
            }
        }
    };


}
