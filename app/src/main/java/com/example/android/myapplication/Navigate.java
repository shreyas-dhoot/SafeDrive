package com.example.android.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

/**
 * Created by aditya on 30/3/18.
 */

public class Navigate extends AppCompatActivity{
    TextView viewspeed = null, mode = null;
    EditText lati = null, longi = null;
    SeekBar speedo = null;
    String spd = "10";
    String vehicle = "0";
    String lat = null;
    String lon = null;
    String address = null;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigate);
        lati = (EditText)findViewById(R.id.lati);
        lati.setText("18.52175");
        longi = (EditText)findViewById(R.id.longi);
        longi.setText("73.8410277");
        speedo = (SeekBar)findViewById(R.id.speedo);
        mode = (TextView)findViewById(R.id.mode);
        viewspeed = (TextView)findViewById(R.id.viewspeed);
        speedo.setMax(120);
        speedo.setProgress(10);
        speedo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                spd = Integer.toString(progress);
                viewspeed.setText(spd + " kmph");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                lat = lati.getText().toString();
                lon = longi.getText().toString();
                address = getAddress(Double.parseDouble(lati.getText().toString()), Double.parseDouble(longi.getText().toString()));
                boolean ch = sendtoserver(lat, lon, spd, vehicle, address);
                if(MainActivity.getDriverMode()) {
                    if (ch)
                        mode.setText("Call Rejection Off");
                    else
                        mode.setText("Call Rejection On");
                }
                else
                    mode.setText("Call Rejection Off");
                MainActivity.setCheck(!ch);

            }
        });
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(Navigate.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

            Log.v("IGA", "Address" + add);
            return add;
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "LOL";
    }


    public void onvehicle(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.bike:
                if (checked)
                    vehicle = "0";
                    break;
            case R.id.car:
                if (checked)
                    vehicle = "1";
                    break;
            case R.id.bus:
                if (checked)
                    vehicle = "2";
                    break;
        }
    }

    public void Start(View view){
        lat = lati.getText().toString();
        lon = longi.getText().toString();
        address = getAddress(Double.parseDouble(lati.getText().toString()), Double.parseDouble(longi.getText().toString()));
        boolean ch = sendtoserver(lat, lon, spd, vehicle, address);
        if(ch)
            mode.setText("Call Rejection Off");
        else
            mode.setText("Call Rejection On");
        MainActivity.setCheck(!ch);
    }

    public boolean sendtoserver(String lat, String longi, String speed, String type, String address){
        DatagramSocket socket = null;
        String message = lat + "#" + longi + "#" + speed + "#" + type + "#" + address;
        byte[] messageData = message.getBytes();
        boolean ret = false;
        try {
            InetAddress addr = InetAddress.getByName("192.168.43.9");
            int port = 5006;
            DatagramPacket sendPacket = new DatagramPacket(messageData, 0, messageData.length, addr, port);

            socket = new DatagramSocket(port);
            socket.send(sendPacket);
            socket.disconnect();
            socket.close();
            String text;

            int server_port = 5006;
            byte[] msg = new byte[1];
            DatagramPacket p = new DatagramPacket(msg, msg.length);
            DatagramSocket s = new DatagramSocket(server_port);
            s.receive(p);
            text = new String(msg, 0, p.getLength());
            if(text.equals("0"))
                ret = false;
            else
                ret = true;
            s.disconnect();
            s.close();

        } catch (UnknownHostException e) {
            Log.e("MainActivity sendPacket", "getByName failed");
        } catch (IOException e) {
            Log.e("MainActivity sendPacket", "send failed");
        }

        return ret;
    }



}
