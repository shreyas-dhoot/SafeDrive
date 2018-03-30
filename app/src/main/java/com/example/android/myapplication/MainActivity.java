package com.example.android.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MenuItem item1, item2;

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;


    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private final int UPDATE_INTERVAL = 10000000; // 10 sec
    private final int FASTEST_INTERVAL = 5000000; // 5 sec
    private final int DISPLACEMENT = 10000; // 10 meters

    private final int REQUEST_LOCATION = 2;

    final private int MY_ACCESS_FINE_LOCATION = 1;
    static int SORTING = 0; //1 - RATING
                            //0 - DISTANCE

    static boolean INAPPDRIVERMODE = false;
    static boolean DRIVERMODE = false;
    private boolean refresh_activity = true;
    private boolean isGPSOn = false;
    private ProgressDialog mProgressDialog;
    public static DatabaseHandler db;
    private VenueAdapter myAdapter;

    private double latitude, longitude;

    static ArrayList<VenueObject> venuesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Drive Mode is set!!", Toast.LENGTH_SHORT).show();
        setContentView(com.example.android.myapplication.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.example.android.myapplication.R.id.toolbar);
        setSupportActionBar(toolbar);

        initProgressDialog();

        initCollapsingToolbar();

        initDatabase();

        recyclerView = (RecyclerView) findViewById(com.example.android.myapplication.R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.example.android.myapplication.R.id.swipe_refresh_layout);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(1, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        try {
            Glide.with(this).load(R.drawable.food).into((ImageView) findViewById(com.example.android.myapplication.R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        SORTING = 0;
                        refresh_activity = true;
                        isLocationOn();
                        swipeRefreshLayout.setRefreshing(false);
                        //changeLocation();
                    }
                }
        );
        setupGoogleApiClient();
    }

    public void initDatabase(){
        db = new com.example.android.myapplication.DatabaseHandler(this);
    }


    private void setupGoogleApiClient() {
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        isLocationOn();
    }

    /*
            Used to check if Location is on
        */
    private void isLocationOn(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        //Location is on
                        case LocationSettingsStatusCodes.SUCCESS:
                            isGPSOn = true;
                            refresh_activity = true;
                            changeLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MainActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
    }

    //Callback from Location Settings
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This log is never called
        Log.d("onActivityResult()", Integer.toString(resultCode));

        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        isGPSOn = true;
                        refresh_activity = true;
                        changeLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        if(db.getVenuesCount() > 0) {
                            venuesList = db.getAllVenues();
                            myAdapter = new VenueAdapter(this, venuesList);
                            recyclerView.setAdapter(myAdapter);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /*
        Used to inflate menu of toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.android.myapplication.R.menu.menu_main, menu);
        item1 = menu.findItem(com.example.android.myapplication.R.id.sortByDistance);
        item1.setVisible(false);
        item2 = menu.findItem(com.example.android.myapplication.R.id.sortByRating);
        item2.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.example.android.myapplication.R.id.sortByRating:
                if((venuesList != null) && SORTING == 0){
                    Collections.sort(venuesList,new VenueRatingComparator());
                    myAdapter = new VenueAdapter(this, venuesList);
                    recyclerView.setAdapter(myAdapter);
                    //Toast.makeText(getApplicationContext(), "Sorted by Rating",
                    //        Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.root),"Sorted by Rating", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                SORTING = 1;
                return true;
            case com.example.android.myapplication.R.id.sortByDistance:
                if((venuesList != null) && SORTING == 1){
                    Collections.sort(venuesList,new VenueDistanceComparator());
                    myAdapter = new VenueAdapter(this, venuesList);
                    recyclerView.setAdapter(myAdapter);
//                    Toast.makeText(getApplicationContext(), "Sorted by Distance",
//                            Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.root),"Sorted by Distance", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                SORTING = 0;
                return true;
            case R.id.inAppDriverMode:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    INAPPDRIVERMODE = false;
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    INAPPDRIVERMODE = true;
                }
                return true;
            case R.id.driverMode:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    DRIVERMODE = false;
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    DRIVERMODE = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(com.example.android.myapplication.R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(com.example.android.myapplication.R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(com.example.android.myapplication.R.string.app_name));
                    item1.setVisible(true);
                    item2.setVisible(true);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    item1.setVisible(false);
                    item2.setVisible(false);
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        changeLocation();
    }

    private void changeLocation() {
        if (refresh_activity) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission(MY_ACCESS_FINE_LOCATION);
                return;
            }
            if (isGPSOn) {
                refresh_activity = false;
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Retreiving location");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                final Context mContext = this;

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //Toast.makeText(getApplicationContext(), latitude + ", " + longitude,
                        //    Toast.LENGTH_LONG).show();
                        new Response(MainActivity.this,recyclerView, latitude, longitude).execute();
                    }

                };

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        while (isGPSOn) {
                            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mLastLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                latitude = mLastLocation.getLatitude();
                                longitude = mLastLocation.getLongitude();
                                break;
                            }
                        }
                        pd.dismiss();
                        handler.sendEmptyMessage(0);
                    }
                };
                mThread.start();
            }
        }
    }

    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Retreiving Location ");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);

    }

    public void permission(int ACCESS) {
        if(ACCESS == MY_ACCESS_FINE_LOCATION) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refresh_activity = true;
                    isLocationOn();
                    //changeLocation();
              }
                return;
            }
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public DividerItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh_activity = true;
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        //isLocationOn();
        //changeLocation();
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}