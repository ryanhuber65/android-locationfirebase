package com.comp259.huber7517.comp262final;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //initialize variables
    private GoogleMap mMap;
    DBHelper dbHelper;
    public static ListView list;
    public int index;
    public static ArrayAdapter<?> incomingPushMsg;
    List<Location> LocationList = new ArrayList<Location>();
    TextView listViewName;
    Location newBackgroundLocation;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // SET UP THE DATABASE
        dbHelper = new DBHelper(getApplicationContext());

        // create our list and custom adapter
        list = (ListView) findViewById(R.id.list);
        list.setPadding(2,2,2,2);
        list.setCacheColorHint(0);
        list.setFadingEdgeLength(0);
        list.setLongClickable(true);
        //click event for listview
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location getLocation = LocationList.get(position);
                LatLng loc = new LatLng(getLocation.getLatitude(), getLocation.getLongitude());
                //clear an markers on map
                mMap.clear();
                //add the new marker and move the camera there
                mMap.addMarker(new MarkerOptions().position(loc).title(getLocation.getMarker().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

                //toast the coordinates selected
                Toast.makeText(getApplicationContext(), String.valueOf(getLocation.getLatitude())
                        + ", "
                        + String.valueOf(getLocation.getLongitude()), Toast.LENGTH_LONG).show();
            }
        });
        // if the database has more than 0 records, proceed
        if (dbHelper.getLocationsCount() != 0)
            //add all the locations to the LocationList array
            LocationList.addAll(dbHelper.getAllLocations());

        //call this method to populate the listview
        populateList();

        //this checks if the background notification has extras, if it does, proceed
        if (getIntent().getExtras() != null) {
            //initialize variables to assign the extracted data to
            String marker = "Marker";
            String lon = null;
            String lat = null;
            String formattedTime = null;
            index = LocationList.size();
            boolean numeric = true;
            //iterate through the intents keys and values
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                //if a key equals "lon", proceed
                if(key.equals("lon")){
                    //assign the value to the String "lon"
                    lon = value.toString();
                }
                //if a key equals "lat", proceed
                if (key.equals("lat")){
                    //assign the value to the String "lat"
                    lat = value.toString();
                }
                if(key.equals("desc")){
                    //add the value to the marker string
                    marker = value.toString();
                }
                //if a key equals the following, proceed
                if(key.equals("google.sent_time")){
                    //assign the date to the long variable called "time"
                    long time = Long.parseLong(value.toString());
                    //format the long time variable into a proper date format
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    formattedTime = formatter.format(calendar.getTime());
                }
                //log the key and value - for testing
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
            //check to make sure the lat and lon variables are numeric
            try{
                Double numlat = Double.parseDouble(lat);
                Double numlon = Double.parseDouble(lon);

            }catch(NumberFormatException e){
                //if the exception is caught, numeric is false
                numeric = false;
            }
            // if lon and lat are not null & numeric is true, proceed
            if (lon != null && lat != null && numeric){

                // add the new location to the Location object class
                newBackgroundLocation = new Location(index, Double.parseDouble(lat), Double.parseDouble(lon), formattedTime, marker);
                //add new location to database
                dbHelper.addLocation(newBackgroundLocation);
                //add now location to the LocationList array
                LocationList.add(newBackgroundLocation);
                //refresh the listview with the new location added
                incomingPushMsg.notifyDataSetChanged();
                //toast for testing purposes
                Toast.makeText(getApplicationContext(), "New Message: " + newBackgroundLocation.getLongitude()
                        + ", " + newBackgroundLocation.getLatitude()
                        + " --- " + newBackgroundLocation.getDate(), Toast.LENGTH_LONG).show();
            }
            // attempt to get data payload
            //refresh the listview
            incomingPushMsg.notifyDataSetChanged();
        }

        //register the broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.O)
            //this method fires after a message has been received
            @Override
            public void onReceive(Context context, Intent intent) {
                // Check the intent type
                    if (intent.getAction().equals(Global.PUSH_NOTIFICATION)) {
                    // FCM message received
                    Log.i (TAG, "Receiver=" + Global.PUSH_NOTIFICATION);

                    //check to see if the data is not null, then proceed
                        if(intent.getStringExtra(Global.LATITUDE) != null && intent.getStringExtra(Global.LONGITUDE) != null){
                            // assign data from the intent into variables
                            //assign date
                            String date = intent.getStringExtra(Global.TIME);
                            //assign the message for logging - testing purposes
                            String newMessage = intent.getStringExtra(Global.LONGITUDE) + ", "
                                    + intent.getStringExtra(Global.LATITUDE) + "--- "
                                    + date;
                            //assign the index the value of the size of the LocationList array
                            index = LocationList.size();
                            //parse the data for latitude and longitude and
                            //store into variables of type double
                            double lat = Double.parseDouble(intent.getStringExtra(Global.LATITUDE));
                            double lon = Double.parseDouble(intent.getStringExtra(Global.LONGITUDE));
                            //assign the marker the value of the notification description
                            String marker = intent.getStringExtra(Global.NOTIFICATION_MESSAGE);

                            //store the data in the Location object class
                            Location newLocation = new Location(index, lat, lon, date, marker);
                            //store the Location object in the SQLite database
                            dbHelper.addLocation(newLocation);
                            //add the location to the LocationList array
                            LocationList.add(newLocation);
                            //refresh the listview with the new data added
                            incomingPushMsg.notifyDataSetChanged();
                            //for testing purposes - toast the data that was stored in the
                            //newMessage variable
                            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
                        }
                    //iterate the the intent extras and log the keys and values
                    if (intent.getExtras() != null) {
                        for (String key : intent.getExtras().keySet()) {
                            Object value = intent.getExtras().get(key);
                            Log.d(TAG, "Key: " + key + " Value: " + value);
                        }
                    }
                    //refresh the listview
                    incomingPushMsg.notifyDataSetChanged();

                }
            }
        };
    }
    //this method populates the listview and sets up the custom array adapter
    private void populateList(){
        incomingPushMsg = new LocationListAdapter();
        list.setAdapter(incomingPushMsg);
    }
    //custom array adapter class
    private class LocationListAdapter extends ArrayAdapter<Location> {
        public LocationListAdapter() {
            super(getApplicationContext(),
                    R.layout.simple_list_item_1, LocationList);
        }

        // this method displays the data in the listview within the textview
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.simple_list_item_1, parent, false);

            //get the position of the most recent added location and create a Location object of it
            Location currentLocation = LocationList.get(position);

            listViewName = (TextView)
                    view.findViewById(R.id.text1);

            listViewName.setText("Desc: "+currentLocation.getMarker() + ", "
                    +"Lat: "+currentLocation.getLatitude()
                    +", Long: " +currentLocation.getLongitude()
                    +" -- Date: "+currentLocation.getDate());


            return view;
        }
    }

    protected void onResume() {
        super.onResume();
        // FCM "registration complete" receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Global.REGISTRATION_COMPLETE));

        // FCM "new message" receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Global.PUSH_NOTIFICATION));
        //populate the listview
        populateList();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if a new location was added in from the background notifications,
        //check to see if the Location object newBackgroundLocation is not null.
        //if it is not, then proceed and display the location on the map
        if(newBackgroundLocation != null){
            LatLng loc = new LatLng(newBackgroundLocation.getLatitude(), newBackgroundLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc).title(newBackgroundLocation.getMarker().toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }
}