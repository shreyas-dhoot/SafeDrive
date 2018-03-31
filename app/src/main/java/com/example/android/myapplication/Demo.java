package com.example.android.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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


public class Demo extends AppCompatActivity {
    TextView latitude, longitude, vehicle, call, speed1;
    ProgressBar progressBar;
    int progressStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        vehicle = (TextView) findViewById(R.id.vehicle);
        call = (TextView) findViewById(R.id.call);
        speed1 = (TextView) findViewById(R.id.speed);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        call();
    }

    double latlon[][] = {
            {18.521757943838196, 73.8410277539673},
            {18.523100789814674, 73.8410116607132},
            {18.526513557247437, 73.84200407804883},
            {18.53154153599288, 73.84483407788139},
            {18.529867529180397, 73.85112654023044},
            {18.52952349766339, 73.85296117119674},
            {18.5298846264811, 73.85717223935012}
    };


    double time[] = {0.0075, 0.01786, 0.0234, 0.01722, 0.0197, 0.0178};

    public void call() {

        latitude.setText(String.valueOf(latlon[0][0]));
        longitude.setText(String.valueOf(latlon[0][1]));
        vehicle.setText("Car");
    }

    public void calldistance(View view) {


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < latlon.length - 1 && progressStatus < 200; i++) {
                    final int value = i;

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressStatus = progressStatus + 16;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            latitude.setText(String.valueOf(latlon[value][0]));
                            longitude.setText(String.valueOf(latlon[value][1]));
                            vehicle.setText("Car");
                            double d = distance(latlon[value][0], latlon[value][1], latlon[value + 1][0], latlon[value + 1][1]);
                            double speed = d / time[value];

                            speed1.setText(String.valueOf(speed));
                            if(value != 5)
                                progressBar.setProgress(progressStatus);
                            else
                                progressBar.setProgress(100);

                            //String address = getAddress(Double.parseDouble(String.valueOf(latlon[value][0])), Double.parseDouble(String.valueOf(latlon[value][1])));
                            boolean ch = sendtoserver(String.valueOf(speed), "1", "3", "2");
                            if (ch)
                                call.setText("Yes");
                            else
                                call.setText("No");
                            MainActivity.setCheck(!ch);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();

        /*int i;
        for( i = 0; i < latlon.length - 1; i++) {
            //new Delay(latitude, longitude, vehicle, call, speed1, time, latlon, i).execute(1000);
            final int temp = i;
            latitude.setText(String.valueOf(latlon[i][0]));
            longitude.setText(String.valueOf(latlon[i][1]));
            vehicle.setText("Car");
            double d = distance(latlon[i][0], latlon[i][1], latlon[i + 1][0], latlon[i + 1][1]);
            double speed = d / time[i];
            speed1.setText(String.valueOf(speed));
            boolean ch = sendtoserver(String.valueOf(latlon[i][0]),String.valueOf(latlon[i][1]),String.valueOf(speed), "1");
            if(ch)
                call.setText("Yes");
            else
                call.setText("No");
            try {
                Thread.sleep(1000);
            }catch(InterruptedException e) {

            }
        }*/

    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(Demo.this, Locale.getDefault());
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
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

   /* public boolean sendtoserver(String lat, String longi, String speed, String type) {
        DatagramSocket socket = null;
        String message = lat + "#" + longi + "#" + speed + "#" + type ;
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
            if (text.equals("0"))
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
    }*/
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
/*
class Delay extends AsyncTask<Integer, Void, Boolean> {
    double speed;
    boolean ch;
    TextView latitude, longitude, vehicle, call, speed1;
    double time[];
    double latlon[][];
    int i;
    Delay(TextView latitude, TextView longitude, TextView vehicle, TextView call, TextView speed1, double time[], double latlon[][], int i){

        this.latitude = latitude;
        this.longitude = longitude;
        this.vehicle = vehicle;
        this.time = time;
        this.latlon = latlon;
        this.i = i;
        this.call = call;
        this.speed1 = speed1;
    }

    @Override
    protected void onPreExecute() {
        latitude.setText(""+i);
        longitude.setText(""+i);
        vehicle.setText("Car");
        //double d = distance(latlon[i][0], latlon[i][1], latlon[i + 1][0], latlon[i + 1][1]);
        //speed = d / time[i];
        speed1.setText(""+i);
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (ch)
            call.setText("Yes");
        else
            call.setText("No");
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    public boolean sendtoserver(String lat, String longi, String speed, String type){
        DatagramSocket socket = null;
        String message = lat + "#" + longi + "#" + speed + "#" + type;
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

}*/
