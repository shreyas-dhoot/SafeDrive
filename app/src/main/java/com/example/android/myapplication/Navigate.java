package com.example.android.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
    double latlon[][] = {
        {18.521757943838196,73.8410277539673},
        {18.523100789814674,73.8410116607132},
        {18.526513557247437,73.84200407804883},
        {18.53154153599288,73.84483407788139},
        {18.529867529180397,73.85112654023044},
        {18.52952349766339,73.85296117119674},
        {18.5298846264811,73.85717223935012}
    };

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
