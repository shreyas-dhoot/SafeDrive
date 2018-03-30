package com.example.android.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aditya on 30/3/18.
 */


public class Demo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        calldistance();

    }

    double latlon[][] = {
            {18.521757943838196,73.8410277539673},
            {18.523100789814674,73.8410116607132},
            {18.526513557247437,73.84200407804883},
            {18.53154153599288,73.84483407788139},
            {18.529867529180397,73.85112654023044},
            {18.52952349766339,73.85296117119674},
            {18.5298846264811,73.85717223935012}
    };

    double time [] = {0.0075, 0.01786, 0.0234, 0.01722, 0.0197, 0.0178};
    public  void calldistance () {
        int i;
        TextView latitude = (TextView)findViewById(R.id.lat);
        TextView longitude = (TextView)findViewById(R.id.lon);
        TextView vehicle= (TextView)findViewById(R.id.vehicle);
        TextView call = (TextView)findViewById(R.id.call);
        for( i = 0; i < latlon.length - 1; i++) {
            latitude.setText(String.valueOf(latlon[i][0]));
            longitude.setText(String.valueOf(latlon[i][1]));
            vehicle.setText("Car");
            double d = distance(latlon[i][0], latlon[i][1], latlon[i + 1][0], latlon[i + 1][1]);
            double speed = d / time[i];
            Toast.makeText(this, "Distance = " + String.valueOf(d), Toast.LENGTH_SHORT).show();

        }
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
}
