package com.example.android.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
/**
 * Created by abhishek on 31/3/18.
 */

public class PhoneCallReceiver extends BroadcastReceiver {
    Context context = null;
    private static final String TAG = "Phone call";
    private ITelephony telephonyService;
    private String number = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Receving....");
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.v(TAG, number);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            //telephonyService.silenceRinger();
            if(MainActivity.getCheck() && MainActivity.getDriverMode()) {
                telephonyService.endCall();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, "The number you are trying to call is driving right now.", null, null);


                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
