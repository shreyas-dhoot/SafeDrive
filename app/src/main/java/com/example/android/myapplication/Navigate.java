package com.example.android.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by aditya on 30/3/18.
 */

public class Navigate extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigate);


    }
    String vehicle = null;
    String lat = null;
    String lon = null;

    public void onvehicle(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.bike:
                if (checked)
                    vehicle = "bike";
                    break;
            case R.id.car:
                if (checked)
                    vehicle = "car";
                    break;
            case R.id.bus:
                if (checked)
                    vehicle = "bus";
                    break;
        }
    }

    public void onlocation(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.lat1:
                if (checked)
                    lat = "";
                    lon = "";
                    break;
            case R.id.lat2:
                if (checked)
                    lat = "";
                    lon = "";
                    break;
            case R.id.lat3:
                if (checked)
                    lat = "";
                    lon = "";
                    break;
        }
    }
}
