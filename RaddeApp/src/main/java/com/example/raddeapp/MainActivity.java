package com.example.raddeapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Gravity;

import android.view.View;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements SearchView.OnQueryTextListener{


    private String stationName;
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private Button mButton;
    private EditText mEditText;
    //private GPSTracker gps;
    private android.location.Location gps;
    private double latitude;
    private double longitude;
    private double dest_latitude;
    private double dest_longitude;
    private JSONArray stations = null;
    private ArrayList<Location> locations;
    Integer radius = 30000;//meters
    private LocationManager locationManager;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    //private final String[] mStrings = Cheeses.sCheeseStrings;
    private ArrayList<String> mStrings;
    private TextToSpeech tts;
    private Integer distanceToTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.searchview_filter);
        locations = new ArrayList<Location>();
        mStrings = new ArrayList<String>();

        setupGPS();

        getNearByStations();

        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.setVisibility(View.INVISIBLE);

        mButton = (Button) findViewById(R.id.button);
        mButton.setVisibility(View.INVISIBLE);
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("Radde123 button clicked");


            }
        });
        mSearchView = (SearchView) findViewById(R.id.searchView);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mStrings));

        mListView.setTextFilterEnabled(true);
        mListView.setVisibility(View.INVISIBLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                //Or do whatever you need.
                //Toast.makeText(getApplicationContext(),
                //       ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                stationName = ((TextView) view).getText().toString();


                for (Location l : locations)
                    if (l.name.equals((stationName))) {
                        GPSDistance d = new GPSDistance();
                        d.setCordinates(latitude,longitude,
                                        l.lattitude,l.longitude);

                        distanceToTarget = d.getDistance();

                        System.out.println("Radde123 " +
                                "Station: " + l.name + " " +
                                " lat: " + l.lattitude + "  " +
                                " lng: " + l.longitude + " distance: " + distanceToTarget + "meters");
                        dest_latitude = l.lattitude;
                        dest_longitude = l.longitude;
                        System.out.println("Radde123 " +
                                "CurPos: lat " + latitude + " lng " + longitude);

                        break;
                    }

                mListView.setVisibility(View.INVISIBLE);
                mButton.setVisibility(View.VISIBLE);

                mEditText.setVisibility(View.VISIBLE);
                mEditText.setGravity(Gravity.CENTER);
                mEditText.setText(stationName);

                hideSoftKeyboard();
            }

            protected void hideSoftKeyboard() {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("Search location");
    }
    private void notifyUserDestinationReached(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    System.out.println("Radde123 SUCCESS ");
                    System.out.println("Radde123 engine: " + tts.getDefaultEngine());

                    int result = tts.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        System.out.println("Radde123 This Language is not supported");
                    } else {
                        AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

                        //If we do not have headset just vibrate
                        if (!amanager.isWiredHeadsetOn()){
                            System.out.println("Radde123 isWiredHeadsetOn");
                            int dot = 200;      // Length of a Morse Code "dot" in milliseconds
                            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
                            int short_gap = 200;    // Length of Gap Between dots/dashes
                            int medium_gap = 500;   // Length of Gap Between Letters
                            int long_gap = 1000;    // Length of Gap Between Words
                            long[] pattern = {
                                    0,  // Start immediately
                                    dot, short_gap, dot, short_gap, dot,    // s
                                    medium_gap,
                                    dash, short_gap, dash, short_gap, dash, // o
                                    medium_gap,
                                    dot, short_gap, dot, short_gap, dot,    // s
                                    long_gap
                            };

                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(pattern,-1);

                        } else if (amanager.isMusicActive()){
                            System.out.println("Radde123 music active");
                            //Turn off music
                            Intent intent = new Intent("com.android.music.musicservicecommand.togglepause");
                            getApplicationContext().sendBroadcast(intent);

                            amanager.setStreamVolume(AudioManager.STREAM_MUSIC,12,0);

                            result = tts.speak("You have arrived at your final destination",
                                    TextToSpeech.QUEUE_FLUSH, null);

                            if (result == TextToSpeech.ERROR)
                                System.out.println("Radde123 speach failed");

                        }
                    }
                } else {
                    System.out.println("Radde123 Initilization Failed!");
                }
            }
        });
    }

    private void setupGPS(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                //LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,
                new LocationListener() {

                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        String msg = "lat: " + location.getLatitude() + " lng: " + location.getLongitude();
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        GPSDistance dist = new GPSDistance();
                        dist.setCordinates(latitude, longitude,
                                dest_latitude, dest_longitude);

                        Integer distanceToDestination = dist.getDistance();
                        System.out.println("Radde123 dist: " + distanceToDestination);

                        if (distanceToDestination < 120){
                            msg = "You have reached your destination";
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            notifyUserDestinationReached();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

        gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (gps == null){
            System.out.println("Radde123 gps == null try network");
            gps = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
    }
    private void getNearByStations(){
        new Background().execute();
    }
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mListView.setFilterText(newText.toString());
            mListView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    class Background extends AsyncTask<String, Integer, String> {
        private void getCurrentPosition(){
            do {
                System.out.println("Radde123 lat: " +
                        gps.getLatitude() + " lng: " + gps.getLongitude());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (gps.getLatitude() == 0.0);
        }
        protected String doInBackground(String... urls) {

            getCurrentPosition();

            stations = new NearbyStations(
                    gps.getLongitude(),
                    gps.getLatitude(),
                    radius).getItems();

            for(int i = 0; i < stations.length(); i++){
                JSONObject c = null;
                Location l = new Location();
                try {
                    c = stations.getJSONObject(i);
                    //System.out.println("Radde123" + c.getString("name") + " " + c.getString("@x") + " " + c.getString("@y"));

                    l.name = c.getString("name");
                    l.longitude = Double.parseDouble(c.getString("@x"));
                    l.lattitude = Double.parseDouble(c.getString("@y"));

                    locations.add(l);
                    mStrings.add(c.getString("name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Get all train stations
            stations = new StationList().getItems();

            for(int i = 0; i < stations.length(); i++){
                JSONObject c = null;
                Location l = new Location();
                try {
                    c = stations.getJSONObject(i);

                    l.name = c.getString("name");
                    l.longitude = c.getDouble("lng");
                    l.lattitude = c.getDouble("lat");

                    locations.add(l);
                    mStrings.add(c.getString("name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addAll(mStrings);
                    mAdapter.notifyDataSetChanged();
                    System.out.println("Radde123 notifyDataSetChanged len: " + mStrings.size());
                }
            });

            return null;
        }
        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}
