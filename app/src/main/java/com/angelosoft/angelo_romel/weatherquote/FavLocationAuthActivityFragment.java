package com.angelosoft.angelo_romel.weatherquote;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class FavLocationAuthActivityFragment extends Fragment {
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;
    private ListView listView;
    private ArrayAdapter<String> locationAdapter;
    private FavLocationList favLocationList = new FavLocationList();
    private TextView textQuote;
    //private ImageView imageQuote;
    //private AnimationDrawable animation;

    ReverseGeocoding reverseGeo;
    private final int LOCATION_REFRESH_TIME = 10 * 1000;
    private final int LOCATION_REFRESH_DISTANCE = 50;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private AdRequest adRequest;

    public FavLocationAuthActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uncomment below before generating release build.
        adRequest = new AdRequest.Builder().build();

        //Test device id = 88418297FE29DF850CF429E3442F5CEA
        //Test ad:
        //adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("88418297FE29DF850CF429E3442F5CEA")  // An example device ID
        //        .build();
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putBoolean("scrollToItem", false).apply();
        prefs.edit().putBoolean("isLoggedIn", true).apply();
        double[] coord;
        coord = findCurrentCoordinates();
    }//end onCreate


    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_fav_location_auth, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fav_location_auth, container, false);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_quote_fav_location, container, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer_fav_location, container, false);
        listView = (ListView) view.findViewById(R.id.listview_favlocation);
        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);

        AdView adView = (AdView)header.findViewById(R.id.adViewNowForecast);
        adView.loadAd(adRequest);

        textQuote = ((TextView)header.findViewById(R.id.h_quote_textview));
        //imageQuote = ((ImageView)header.findViewById(R.id.quote_imageView));
        //animation = (AnimationDrawable) imageQuote.getBackground();
        //animation.start();

        //Reference to the Firebase applications URL.
        final Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
        Firebase mUserFireRef = new Firebase(getResources().getString(R.string.firebase_url) +
                "/users/" + prefs.getString("uid", "") + "/favlocations");
        //Store current activity state to be used inside authentication state listener
        //and avoid the nullpointerexception error.
        final Activity act = getActivity();
        //Listen to changes in the authentication state.
        mFireRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if(authData != null) {
                    // The user is logged in via email. Do nothing.
                } else {
                    // The user is not logged in. Go to sign-up screen.
                    prefs.edit().putString("uid", "0").apply();
                    prefs.edit().putString("selectedPlace", "").apply();
                    //mFireRef.unauth();

                    Intent intent = new Intent(act,
                            LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    act.startActivity(intent);
                    act.finish();
                }
            }
        });

        mUserFireRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //loca = new Gson().fromJson((String) snapshot.getValue(), String[].class);
                String[] locList = new Gson().fromJson((String) snapshot.getValue(), String[].class);
                favLocationList.updateLocation(locList);
                favLocationList.writeFile(act, prefs.getString("uid", ""));
                constructList(act);
                seedLocationList();
                /*
                if (favLocationList.isLocationExists(prefs.getString("selectedPlace", "")) &&
                        !prefs.getString("selectedPlace", "").equals("")) {
                    //Scroll to the bottom of the listview:
                    if (prefs.getBoolean("isNewPlaceAdded", false)) {
                        listView.setSelection(locationAdapter.getCount() - 1);
                        prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
                    }
                }
                */

                //Scroll to a specific item in the listview.
                if (prefs.getBoolean("isNewPlaceAdded", false)) {
                    listView.setSelection(favLocationList.getItemPosition(prefs.getString("selectedPlace", "")));
                    prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = locationAdapter.getItem(position - 1); //mForecastAdapter.getItem(position);
                prefs.edit().putString("selectedPlace", forecast).apply();
                Intent intent = new Intent(getActivity(), NowForecastActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                prefs.edit().putString("selectedPlace", "").apply();
                favLocationList.removeLocation(locationAdapter.getItem(position - 1));
                favLocationList.writeFile(getActivity(), prefs.getString("uid", ""));
                constructList(getActivity());
                seedLocationList();
                String js = new Gson().toJson(favLocationList.getLocations());
                final Firebase mFireRef = new Firebase(getActivity().getString(R.string.firebase_url));
                Firebase userRef = mFireRef.child("users").child(prefs.getString("uid", ""));
                Map<String, Object> mfavlocations = new HashMap<String, Object>();
                mfavlocations.put("favlocations", js);
                userRef.updateChildren(mfavlocations);
                return true;
            }
        });

        //imageQuote.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
        //    }
        //});

        textQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
            }
        });

        FloatingActionButton getCurrentPlaceFab = (FloatingActionButton) view.findViewById(R.id.get_current_place_fab);
        getCurrentPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (reverseGeo == null) {
                        prefs.edit().putBoolean("scrollToItem", false).apply();
                        prefs.edit().putBoolean("showNowForecast", true).apply();
                        double[] coord;
                        coord = findCurrentCoordinates();
                        reverseGeo = new ReverseGeocoding();
                        reverseGeo.execute(coord[0], coord[1]);
                    }
                }
                catch(Exception ex) {
                    //Log.e(LOG_TAG, "Error updating list.", ex);
                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_change_password) {
            Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_sign_out) {
            Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
            prefs.edit().putString("uid", "0").apply();
            prefs.edit().putString("selectedPlace", "").apply();
            mFireRef.unauth();

        }
        else if(id == R.id.action_add_location) {
            Intent intent = new Intent(getActivity(),
                    PlacePickerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if(id == R.id.action_submit_quote) {
            Intent intent = new Intent(getActivity(), SubmitQuoteActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
        favLocationList.readFile(getActivity(), prefs.getString("uid", ""));
        updateList();
        seedLocationList();
        super.onResume();
    }

    @Override
    public void onPause() {
        reverseGeo = null;
        super.onPause();
    }

    public void constructList(Activity act) {
        favLocationList.readFile(getActivity(), prefs.getString("uid", ""));

        List<String> locationList =
                new ArrayList<String> (Arrays.asList(this.favLocationList.getLocations()));

        locationAdapter = new ArrayAdapter<String> (act, R.layout.listitem_fav_location,
                R.id.city_textview, locationList);
        listView.setAdapter(locationAdapter);
    }

    public void updateList() {
        try {
            if (!this.favLocationList.isLocationExists(prefs.getString("selectedPlace", "")) &&
                !prefs.getString("selectedPlace", "").equals("")) {
                this.favLocationList.addLocation(prefs.getString("selectedPlace", ""));
                this.favLocationList.writeFile(getActivity(), prefs.getString("uid", ""));
                List<String> locationList =
                        new ArrayList<String>(Arrays.asList(this.favLocationList.getLocations()));

                this.locationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.listitem_fav_location,
                        R.id.city_textview, locationList);
                listView.setAdapter(this.locationAdapter);

                String js = new Gson().toJson(this.favLocationList.getLocations());

                final Firebase mFireRef = new Firebase(getActivity().getString(R.string.firebase_url));
                Firebase userRef = mFireRef.child("users").child(prefs.getString("uid", ""));
                Map<String, Object> mfavlocations = new HashMap<String, Object>();
                mfavlocations.put("favlocations", js);
                userRef.updateChildren(mfavlocations);
            }
            else {
                prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
            }

            //Scroll to a specific item in the listview.
            if(prefs.getBoolean("scrollToItem", false) == true) {
                if (favLocationList.isLocationExists(prefs.getString("selectedPlace", ""))) {
                    //listView.smoothScrollToPositionFromTop(favLocationList.getItemPosition(prefs.getString("selectedPlace", "")),
                    //        0, 500);
                    listView.setSelection(favLocationList.getItemPosition(prefs.getString("selectedPlace", "")));
                    prefs.edit().putBoolean("scrollToItem", false);
                }
            }
        }
        catch(Exception ex) {
            //Log.e(LOG_TAG, "Error in updateList.", ex);
        }
    }

    public void seedLocationList() {
        if(this.favLocationList.isListEmpty()) {
            prefs.edit().putBoolean("showNowForecast", false).apply();
            double[] coord;
            coord = findCurrentCoordinates();
            reverseGeo = new ReverseGeocoding();
            reverseGeo.execute(coord[0], coord[1]);
        }
    }

    private final LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double lat = location.getLatitude();
            double longi = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String stat, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public double[] findCurrentCoordinates() {
        //coord[0] = latitude, coord[1] = longitude
        double[] coord = new double[] {0, 0};
        try {
            LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            try {
                if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

                    if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                                LOCATION_REFRESH_DISTANCE, locListener);
                        Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null) {
                            coord[0] = location.getLatitude();
                            coord[1] = location.getLongitude();

                        }
                        else {
                            coord[0] = 0;
                            coord[1] = 0;
                        }
                    }

                    else if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                                LOCATION_REFRESH_DISTANCE, locListener);
                        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null) {
                            coord[0] = location.getLatitude();
                            coord[1] = location.getLongitude();
                        }
                        else {
                            coord[0] = 0;
                            coord[1] = 0;
                        }
                    }
                    else {
                        coord[0] = 0;
                        coord[1] = 0;
                    }
                }
                else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(getActivity(), "Please allow/set location permission for this app. Certain features of " +
                                "WeatherQuote needs access to your location.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_CODE);
                    }
                }
                return coord;
            }
            catch(Exception ex) {
                Log.e("exception ", "Error ", ex);
                coord[0] = 0;
                coord[1] = 0;
                return coord;

            }
        }
        catch(SecurityException ex) {
            Log.e("security exception", "Error ", ex);
            coord[0] = 0;
            coord[1] = 0;
            return coord;
        }
        catch(Exception ex) {
            Log.e("exception ", "Error ", ex);
            coord[0] = 0;
            coord[1] = 0;
            return coord;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
        else {
        }
        return;
    }

    public void nullifyAsync() {
        reverseGeo = null;
    }

    /*
     *AsyncTask class.
     */
    public class ReverseGeocoding extends AsyncTask<Double, Void, Boolean> {
        private double placeLat = 0;
        private double placeLong = 0;
        StringBuilder placeBuilder = new StringBuilder();
        private SunSpinProgressDialog progressDialog;
        @Override
        public Boolean doInBackground(Double... params) {
            try{
                placeLat = params[0];
                placeLong = params[1];
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List <Address> address = geocoder.getFromLocation(placeLat, placeLong, 1);
                /*
                int maxLine = address.get(0).getMaxAddressLineIndex();
                for(int linePos = 0; linePos < maxLine; linePos ++) {
                    placeBuilder.append(address.get(0).getAddressLine(linePos));
                }
                */
                if(address.get(0).getSubLocality() != null && !address.get(0).getSubLocality().toString().equals("")) {
                    placeBuilder.append(address.get(0).getSubLocality());
                }
                if(address.get(0).getLocality() != null && !address.get(0).getLocality().toString().equals("")) {
                    if(address.get(0).getSubLocality() != null && !address.get(0).getSubLocality().toString().equals("")) {
                        placeBuilder.append(", ");
                    }
                    placeBuilder.append(address.get(0).getLocality());
                }
                if(address.get(0).getCountryName() != null && !address.get(0).getCountryName().toString().equals("")) {
                    if(address.get(0).getLocality() != null && !address.get(0).getLocality().toString().equals("")) {
                        placeBuilder.append(", ");
                    }
                    placeBuilder.append(address.get(0).getCountryName());
                }

                return true;
            }
            catch(Exception ex) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(result == true) {
                try {
                    prefs.edit().putString("selectedPlace", placeBuilder.toString().trim()).apply();
                    if (favLocationList.isLocationExists(prefs.getString("selectedPlace", ""))) {
                        prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
                    }
                    else {
                        prefs.edit().putBoolean("isNewPlaceAdded", true).apply();
                    }
                    updateList();
                    if(prefs.getBoolean("showNowForecast", false) == true) {
                        Intent intent = new Intent(getActivity(), NowForecastActivity.class);
                        startActivity(intent);
                    }
                    nullifyAsync();
                }
                catch(Exception ex) {
                    prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
                    //Log.e(LOG_TAG, "Error in onPostExecute.", ex);
                    nullifyAsync();
                }
            }
            else {
                prefs.edit().putString("selectedPlace", "").apply();
                prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
                Toast.makeText(getActivity(), "Unable to establish your location. Please enable location services.", Toast.LENGTH_SHORT).show();
                nullifyAsync();
            }
        }

        @Override
        public void onPreExecute() {
            progressDialog = new SunSpinProgressDialog(getActivity());
            progressDialog.show();
            super.onPreExecute();
        }
    }

}
