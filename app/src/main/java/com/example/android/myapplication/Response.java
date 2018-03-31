package com.example.android.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.android.myapplication.MainActivity.db;
import static com.example.android.myapplication.MainActivity.venuesList;

/**
 * Created by Shreyas_Dhoot on 5/10/2017.
 */

class Response extends AsyncTask<View, Void, String> {

//    private final String CLIENT_ID = "P5JRCNT0Q53CYFP1BPVCXVNYBBSPAEG5QGTXGZ2F5FEDYTSA";
//    private final String CLIENT_SECRET = "UJGODO0UKFU3QTXGYDJONYVVKJLFXGXAKFJSKFIVYQPR44SN";

    private final String CLIENT_ID = "553TDCNOZW5A3KAJW5PVPYP0KXSMYONPKNXPJSYHUNUE2M1C";
    private final String CLIENT_SECRET = "XRXHA01SCCOTOERO0UILSKGKIGDAUAUU1H4AGBBAXPR20TX0";


    private RecyclerView        recyclerView;
    private VenueAdapter        myAdapter;
    private Context             mContext;
    private ProgressDialog      mProgressDialog;
    private AlertDialog.Builder alert;
    private String              latitude;
    private String              longitude;

    Response(Context mContext, RecyclerView recyclerView, double latitude, double longitude){
        this.latitude = Double.toString(latitude);
        this.longitude = Double.toString(longitude);
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        initProgressDialog();
        initAlertDialog();
    }

    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ");
        mProgressDialog.setCancelable(false);

    }

    private void initAlertDialog(){
        alert = new AlertDialog.Builder(mContext)
                .setTitle("No Internet Connection")
                .setMessage("Make sure that Wi-fi or cellular mobile data is turned on, then try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Response(mContext,recyclerView, Double.parseDouble(latitude), Double.parseDouble(longitude)).execute();
                    }
                })
                .setNegativeButton("close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(db.getVenuesCount() > 0) {
                            venuesList = db.getAllVenues();
                            Collections.sort(venuesList,new VenueDistanceComparator());
                            myAdapter = new VenueAdapter(mContext, venuesList);
                            recyclerView.setAdapter(myAdapter);
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

    }

    @Override
    protected String doInBackground(View... urls) {
        // make Call to the url
        String temp = makeCall("https://api.foursquare.com/v2/venues/explore?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +"&section=nextVenues&radius=10000&limit=10&venuePhotos=1&v=20130815&ll=" + latitude + "," + longitude);
        return temp;
    }

    public static String makeCall(String urlString) {
        StringBuffer chaine = new StringBuffer("");
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
        } catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }
        // trim the whitespaces
        return (chaine.toString()).trim();
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if (result.equals("")) {
            // we have an error to the call
            // we can also stop the progress bar
            alert.show();
        } else if (!result.equals("")){
            // parseFoursquare venues search result

            venuesList = parseFoursquare(result);
            //Collections.sort(venuesList,new VenueDistanceComparator());
            myAdapter = new VenueAdapter(mContext, venuesList);
            recyclerView.setAdapter(myAdapter);
        }
    }

    private static ArrayList<VenueObject> parseFoursquare(final String response) {

        ArrayList<VenueObject> temp = new ArrayList<>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("groups")) {
                    db.deleteAllVenues();
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items");
                    int venueCount = jsonArray.length();

                    for (int i = 0; i < venueCount; i++) {
                        VenueObject poi = new VenueObject();
                        JSONObject venue = jsonArray.getJSONObject(i).getJSONObject("venue");
                        if (venue.has("name")) {
                            poi.setName(venue.getString("name"));
                            poi.setvenueID(venue.getString("id"));

                            if (venue.has("location")) {
                                if (venue.getJSONObject("location").has("address")) {
                                    if (venue.getJSONObject("location").has("city")) {
                                        poi.setCity(venue.getJSONObject("location").getString("city"));
                                        poi.setDistance(venue.getJSONObject("location").getInt("distance"));
                                        poi.setAddress(venue.getJSONObject("location").getJSONArray("formattedAddress"));
                                    }
                                    if (venue.has("categories")) {
                                        poi.setCategory(venue.getJSONArray("categories").getJSONObject(0).getString("name"));
                                    }
                                    if (venue.has("stats")){
                                        int checkincount = venue.getJSONObject("stats").getInt("checkinsCount");
                                        int tipcount = venue.getJSONObject("stats").getInt("tipCount");
                                        poi.setStat(checkincount, tipcount);
                                    }
                                    if (venue.has("rating")){
                                        poi.setRating(venue.getInt("rating"));
                                    }
                                    temp.add(poi);
                                }
                            }
                        }
                    }
                    VenueObject poi = new VenueObject();
                    poi.setName("Vaishali Restaurant");
                    poi.setvenueID("DEFAULT");
                    poi.setDistance(2200);
                    poi.setAddress("Fergusson College Road, Opposite British Library");
                    poi.setCity("Pune");
                    poi.setCategory("Fast Food");
                    poi.setRating(7);
                    poi.setStat(63,63);
                    temp.add(0,poi);

                    poi = new VenueObject();
                    poi.setName("Rupali Restaurant");
                    poi.setvenueID("DEFAULT_2");
                    poi.setDistance(2200);
                    poi.setAddress("Fergusson College Road, Opposite British Library");
                    poi.setCity("Pune");
                    poi.setCategory("Fast Food");
                    poi.setRating(7);
                    poi.setStat(63,63);
                    temp.add(1, poi);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return temp;
    }
}