package com.example.android.myapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class weatherpage extends AppCompatActivity {
    TextView viewspeed = null, mode = null, viewjam = null;
    SeekBar speedo = null, jam = null;
    String spd = "10";
    String vehicle = "0";
    String weath = "0";
    String traffic = "3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherpage);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        viewjam = (TextView)findViewById(R.id.trafficview);
        speedo = (SeekBar)findViewById(R.id.speedo);
        mode = (TextView)findViewById(R.id.mode);
        viewspeed = (TextView)findViewById(R.id.viewspeed);
        jam = (SeekBar)findViewById(R.id.jam);
        jam.setMax(100);
        jam.setProgress(30);
        speedo.setMax(120);
        speedo.setProgress(10);
        jam.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                traffic = Double.toString((double)progress/10);
                viewjam.setText(traffic + " t");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                boolean ch = sendtoserver(spd, vehicle, traffic, weath);
                if(ch)
                    mode.setText("Call Rejection Off");
                else
                    mode.setText("Call Rejection On");
                MainActivity.setCheck(!ch);
            }
        });
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
                boolean ch = sendtoserver(spd, vehicle, traffic, weath);
                if(ch)
                    mode.setText("Call Rejection Off");
                else
                    mode.setText("Call Rejection On");
                MainActivity.setCheck(!ch);

            }
        });
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

        boolean ch = sendtoserver(spd, vehicle, traffic, weath);
        if(ch)
            mode.setText("Call Rejection Off");
        else
            mode.setText("Call Rejection On");
        MainActivity.setCheck(!ch);

    }
    public void onweather(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.thunder:
                if (checked)
                    weath = "0";
                break;
            case R.id.rain:
                if (checked)
                    weath = "1";
                break;
            case R.id.cloudy:
                if (checked)
                    weath = "2";
                break;
        }

        boolean ch = sendtoserver(spd, vehicle, traffic, weath);
        if(ch)
            mode.setText("Call Rejection Off");
        else
            mode.setText("Call Rejection On");
        MainActivity.setCheck(!ch);
    }



    public boolean sendtoserver(String speed, String vehicle, String traffic, String weath){
        DatagramSocket socket = null;
        String message = speed + "#" + vehicle + "#" + traffic + "#" + weath;
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
