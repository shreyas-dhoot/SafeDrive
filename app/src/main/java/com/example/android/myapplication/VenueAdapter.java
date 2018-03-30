package com.example.android.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static com.example.android.myapplication.MainActivity.db;



/**
 * Created by Shreyas_Dhoot on 5/10/2017.
 */

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.MyViewHolder> {


//    private final String        CLIENT_ID = "P5JRCNT0Q53CYFP1BPVCXVNYBBSPAEG5QGTXGZ2F5FEDYTSA";
//    private final String        CLIENT_SECRET = "UJGODO0UKFU3QTXGYDJONYVVKJLFXGXAKFJSKFIVYQPR44SN";

    private final String CLIENT_ID = "553TDCNOZW5A3KAJW5PVPYP0KXSMYONPKNXPJSYHUNUE2M1C";
    private final String CLIENT_SECRET = "XRXHA01SCCOTOERO0UILSKGKIGDAUAUU1H4AGBBAXPR20TX0";
    private final String        PREFIX_URL = "https://api.foursquare.com/v2/venues/";
    private final String        SUFFIX_URL = "?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v=20130815";
    private final String        SIZE_PHOTO = "100x100";

    private Context             mContext;
    private List<VenueObject>   venueList;
    private ImageLoader         imageLoader;
    private DisplayImageOptions options;
    private String              address;
    private String              stat;
    private float               rating;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView     title, category, address, timing, distance, rating, stat;
        public ImageView    overflow;
        public LinearLayout addressLayout;
        public LinearLayout timingLayout;
        public LinearLayout imageLayout;


        public MyViewHolder(View view) {
            super(view);
            final Context context = view.getContext();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setTitle("Navigate")
                            .setMessage("Do you want to start?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!title.getText().equals("Vaishali Restaurant")) {
                                        Intent intent = new Intent(context, Navigate.class);

                                        context.startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(context, Demo.class);

                                        context.startActivity(intent);

                                    }
                                }
                            })
                            .setNegativeButton("No", null)                      //Do nothing on no
                            .show();

                }
            });
            title = (TextView) view.findViewById(R.id.title);
            category = (TextView) view.findViewById(R.id.category);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            address = (TextView) view.findViewById(R.id.address);
            timing = (TextView) view.findViewById(R.id.timing);
            addressLayout = (LinearLayout) view.findViewById(R.id.addressLayout);
            timingLayout = (LinearLayout) view.findViewById(R.id.timingLayout);
            distance = (TextView) view.findViewById(R.id.distance);
            rating = (TextView) view.findViewById(R.id.rating);
            stat = (TextView) view.findViewById(R.id.stat);
            imageLayout = (LinearLayout) view.findViewById(R.id.linear);
        }
    }

    public VenueAdapter(Context mContext, ArrayList<VenueObject> VenueList) {
        this.mContext = mContext;
        this.venueList = VenueList;
        address = "";
        rating = 0;
        stat = "";
        initImageLoader();
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_card, parent, false);

        return new VenueAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        VenueObject venue = venueList.get(position);
        address = venue.getAddress();
        if(!address.equals("")) {
                holder.address.setText(address);
        }
        else {
            holder.timingLayout.setPadding(holder.timingLayout.getPaddingLeft(),
                    dpToPx(10),holder.timingLayout.getPaddingRight(),holder.timingLayout.getPaddingBottom());
            holder.addressLayout.setVisibility(LinearLayout.GONE);
        }
        //Don't need overflow
        holder.overflow.setVisibility(ImageView.GONE);
        stat = venue.getStat();

        //Bind Rating
        rating = venue.getRating();
        holder.rating.setText(Float.toString(rating));
        int a = getColor((int)rating * 10);
        Drawable background = holder.rating.getBackground();
        GradientDrawable backgroundGradient = (GradientDrawable) background;
        backgroundGradient.setColor(a);
        //holder.rating.setBackgroundColor(a);

        //Bind Distance
        if (venue.getDistance() != 0) {
            float distance = venue.getDistance();
            if(distance > 999) {
                int truncate = ((int)distance) / 100;
                distance = truncate / 10.0f;
                holder.distance.setText(Float.toString(distance) + " km");
            }
            else {
                holder.distance.setText(((int)distance) + " mts");
            }

        }
        else
            holder.distance.setVisibility(TextView.GONE);
        //
        holder.title.setText(venue.getName());
        holder.category.setText(venue.getCategory());
        holder.timing.setText("22:00 to 2:00");

        if(venue.getvenueID().equals("DEFAULT")) {
            new downloadData(holder, PREFIX_URL + "4b3b174af964a520cd7025e3" + "/photos" + SUFFIX_URL, venue).execute();
            new downloadData(holder, PREFIX_URL + "4b3b174af964a520cd7025e3" + "/likes" + SUFFIX_URL, venue).execute();
        }

        else if(venue.getvenueID().equals("DEFAULT_2")){
            new downloadData(holder, PREFIX_URL + "54ba1c10498e0f55cafc996e" + "/photos" + SUFFIX_URL, venue).execute();
            new downloadData(holder, PREFIX_URL + "54ba1c10498e0f55cafc996e" + "/likes" + SUFFIX_URL, venue).execute();
        }

        else{
            if(venue.getphotJSON().equals("")) {
                new downloadData(holder, PREFIX_URL + venue.getvenueID() + "/photos" + SUFFIX_URL, venue).execute();
                new downloadData(holder, PREFIX_URL + venue.getvenueID() + "/likes" + SUFFIX_URL, venue).execute();
            }
            else {
                parsePhotoANDLikesJSON(venue.getphotJSON(), holder, venue);
                int aa = venue.getLikes();
                holder.stat.setText(Integer.toString(aa) + " Likes : " + stat);
            }
        }
        // loading album cover using Glide library
//        Glide.with(mContext).load(img).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /*
        Change the color according to the value of the rating
     */
    public int getColor(int progress) {
        int startColor = Color.parseColor("#FF9B99");
        int endColor = Color.parseColor("#006400");

        int start_red = Color.red(startColor);
        int start_green = Color.green(startColor);
        int start_blue = Color.blue(startColor);
        int end_red = Color.red(endColor);
        int end_green = Color.green(endColor);
        int end_blue = Color.blue(endColor);

        int progress_red = start_red + (end_red - start_red) * progress / 100;
        int progress_green = start_green + (end_green - start_green) * progress / 100;
        int progress_blue = start_blue + (end_blue - start_blue) * progress / 100;

        progress_blue = progress_blue < 0 ? 0:progress_blue;
        progress_red = progress_red < 0 ? 0:progress_red;
        progress_green = progress_green < 0 ? 0:progress_green;

        progress_blue = progress_blue > 255 ? 255:progress_blue;
        progress_red = progress_red > 255 ? 255:progress_red;
        progress_green = progress_green > 255 ? 255:progress_green;

        int progress_color = Color.rgb(progress_red, progress_green, progress_blue);
        return progress_color;
    }

    class downloadData extends AsyncTask<View, Void, String>{
        private String src = "";
        private MyViewHolder holder;
        private VenueObject venue;
        downloadData(MyViewHolder holder, String src, VenueObject venue){
            this.src = src;
            this.holder = holder;
            this.venue = venue;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(View... params) {
            StringBuffer chaine = new StringBuffer("");
            try {
                URL url = new URL(src);
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
        protected void onPostExecute(String result) {
            parsePhotoANDLikesJSON(result, holder, venue);
            //imageLoader.displayImage(url, holder.thumbnail, options);
            //Glide.with(mContext).load(result).into(holder.thumbnail);
        }
    }

    public void parsePhotoANDLikesJSON(String result, MyViewHolder holder, VenueObject venue){
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getJSONObject("response").has("photos")) {
                venue.setphotoJSON(result);
                JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONObject("photos").getJSONArray("items");
                //String id = jsonArray.getJSONObject(0).getString("id");
                int length = Math.min(12, jsonArray.length());
                for (int i = 0; i < length; i++) {
                    String prefix = jsonArray.getJSONObject(i).getString("prefix");
                    String suffix = jsonArray.getJSONObject(i).getString("suffix");
                    final ImageView imageView = new ImageView(mContext);
                    imageView.setId(i);
                    int px_padding = dpToPx(4);
                    int px_margin = dpToPx(4);
                    imageView.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, px_margin, px_margin, 0);
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    holder.imageLayout.addView(imageView);
                    imageView.getLayoutParams().height = dpToPx(100);
                    imageView.getLayoutParams().width = dpToPx(100);
                    //imageLoader.displayImage(prefix + SIZE_PHOTO + suffix, imageView, options);
                    String url = prefix + SIZE_PHOTO + suffix;
                    imageLoader.displayImage(url, imageView);
//                    imageLoader.loadImage(url, new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(mContext.getResources(), loadedImage);
//                            dr.setCornerRadius(dpToPx(8));
//                            imageView.setImageDrawable(dr);
//                        }
//                    });
                }
            }
            else if (jsonObject.getJSONObject("response").has("likes")){
                int likes = jsonObject.getJSONObject("response").getJSONObject("likes").getInt("count");
                venue.setLikes(likes);
                db.addVenue(venue);
                holder.stat.setText(Integer.toString(likes)+" Likes : "+stat);
            }
            else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.food_not_found)
                .showImageForEmptyUri(R.drawable.food_not_found)
                .showImageOnFail(R.drawable.food_not_found)
                .displayer(new RoundedBitmapDisplayer(100))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration mImageLoaderConfig =
                new ImageLoaderConfiguration.Builder(mContext)
                        .defaultDisplayImageOptions(options)
                        .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(mImageLoaderConfig);

    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    private int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
