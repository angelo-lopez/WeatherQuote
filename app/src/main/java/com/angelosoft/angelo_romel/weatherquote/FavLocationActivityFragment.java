package com.angelosoft.angelo_romel.weatherquote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.design.widget.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class FavLocationActivityFragment extends Fragment {
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences prefs;
    private ListView listView;
    private ArrayAdapter<String> locationAdapter;
    private FavLocationCustomAdapter customAdapter;
    private FavLocationList favLocationList = new FavLocationList();
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imageQuote;
    private TextView textQuote;
    private FloatingActionMenu fam;
    //private ImageView imageQuote;
    //private AnimationDrawable animation;

    ReverseGeocoding reverseGeo;
    private final int LOCATION_REFRESH_TIME = 10 * 1000;
    private final int LOCATION_REFRESH_DISTANCE = 50;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private AdRequest adRequest;

    public FavLocationActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uncomment below before generating release build.
        //adRequest = new AdRequest.Builder().build();

        //Test device id = 88418297FE29DF850CF429E3442F5CEA
        //Test ad:
        //adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("88418297FE29DF850CF429E3442F5CEA")  // An example device ID
        //        .build();
        //setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //prefs.edit().putString("isSigned", "true").apply();
        prefs.edit().putBoolean("scrollToItem", false).apply();
        //prefs.edit().putBoolean("isLoggedIn", false).apply();
        //double[] coord;
        //coord = findCurrentCoordinates();
    }//end onCreate


    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater menuInflater) {
        //menuInflater.inflate(R.menu.menu_fav_location, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fav_location, container, false);
        //ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_quote_fav_location, container, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer_fav_location, container, false);
        listView = (ListView) view.findViewById(R.id.listview_favlocation);
        //listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);

        //The solution to allow a ListView to have a ScrollingViewBehavior inside a
        //CoordinatorLayout and NestedScrollView. Will only work for Lollipop API level
        //21 and above. Permanent solution would be to use a RecyclerView instead of a ListView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }

        collapsingToolbar = ((CollapsingToolbarLayout)view.findViewById(R.id.main_collapsing));
        collapsingToolbar.setTitle(getResources().getString(R.string.title_activity_fav_location));
        fam = (FloatingActionMenu)view.findViewById(R.id.fav_location_fab_menu);
        imageQuote = (ImageView)view.findViewById(R.id.main_backdrop);
        imageQuote.setImageResource(HelperMethod.getRandomHeaderBackgroundResource(getActivity()));
        textQuote = (TextView)view.findViewById(R.id.weather_quote_textview);
        textQuote.setText(HelperMethod.getRandomQuote(getActivity()));

        //AdView adView = (AdView)footer.findViewById(R.id.adViewNowForecast);
        //adView.loadAd(adRequest);

        //textQuote = ((TextView)header.findViewById(R.id.h_quote_textview));
        //imageQuote = ((ImageView)header.findViewById(R.id.quote_imageView));
        //animation = (AnimationDrawable) imageQuote.getBackground();
        //animation.start();

        collapsingToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageQuote.setImageResource(HelperMethod.getRandomHeaderBackgroundResource(getActivity()));
                textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = locationAdapter.getItem(position); //mForecastAdapter.getItem(position);
                prefs.edit().putString("selectedPlace", forecast).apply();

                Intent intent = new Intent(getActivity(), NowForecastActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                prefs.edit().putString("selectedPlace", "").apply();
                favLocationList.removeLocation(locationAdapter.getItem(position));
                favLocationList.writeFile(getActivity(), getResources().getString(R.string.storage_name));
                constructList();
                seedLocationList();
                return true;
            }
        });

        FloatingActionButton addNewPlaceFab = (FloatingActionButton)view.findViewById(R.id.add_place_fab);
        addNewPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(true);
                Intent intent = new Intent(getActivity(),
                        PlacePickerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        FloatingActionButton currentPlaceFab = (FloatingActionButton)view.findViewById(R.id.current_place_fab);
        currentPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(true);
                try {
                    if(reverseGeo == null) {
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

        FloatingActionButton addQuoteFab = (FloatingActionButton)view.findViewById(R.id.add_quote_fab);
        addQuoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fam.close(true);
                Intent intent = new Intent(getActivity(), SubmitQuoteActivity.class);
                startActivity(intent);
            }
        });

        /*
        imageQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
            }
        });

        textQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
            }
        });
        */
        /*
        FloatingActionButton getCurrentPlaceFab = (FloatingActionButton) view.findViewById(R.id.get_current_place_fab);
        getCurrentPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(reverseGeo == null) {
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
        */

        constructList();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if(id == R.id.action_sign_in) {
        //    prefs.edit().putString("uid", "0").apply();
        //    prefs.edit().putString("selectedPlace", "").apply();
        //    prefs.edit().putString("isSigned", "false").apply();
        //    Intent intent = new Intent(getActivity(),
        //            LoginActivity.class);
        //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //    startActivity(intent);
        //    getActivity().finish();
        //}
        if(id == R.id.action_add_location) {
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
        //textQuote.setText(HelperMethod.getRandomQuote(getActivity()));
        updateList();
        seedLocationList();
        super.onResume();
    }

    @Override
    public void onPause() {
        reverseGeo = null;
        super.onPause();
    }

    public void constructList() {
        favLocationList.readFile(getActivity(), getResources().getString(R.string.storage_name));

        List<String> locationList =
                new ArrayList<String> (Arrays.asList(this.favLocationList.getLocations()));

        locationAdapter = new ArrayAdapter<String> (getActivity(), R.layout.listitem_fav_location,
                R.id.city_textview, locationList);
        listView.setAdapter(locationAdapter);
    }

    public void updateList() {
        try {
            if (!this.favLocationList.isLocationExists(prefs.getString("selectedPlace", "")) &&
                    !prefs.getString("selectedPlace", "").equals("")) {
                this.favLocationList.addLocation(prefs.getString("selectedPlace", ""));
                this.favLocationList.writeFile(getActivity(), getResources().getString(R.string.storage_name));
            }
            else {
                prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
            }
            this.favLocationList.readFile(getActivity(), getResources().getString(R.string.storage_name));

            List<String> locationList =
                    new ArrayList<String>(Arrays.asList(this.favLocationList.getLocations()));

            this.locationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.listitem_fav_location,
                    R.id.city_textview, locationList);
            listView.setAdapter(this.locationAdapter);

            //Scroll to a specific item in the listview when a new item is added.
            if (prefs.getBoolean("isNewPlaceAdded", false)) {
                listView.setSelection(favLocationList.getItemPosition(prefs.getString("selectedPlace", "")));
                prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
            }

            //Scroll to a specific item in the listview when the user tries to add an existing item.
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
            //Log.e(LOG_TAG, "Error updating list.", ex);
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
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
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
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(getActivity(), "Please allow/set location permission for this app. Certain features of " +
                                "WeatherQuote needs access to your location.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                    placeBuilder.append(", " + address.get(0).getCountryName());
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
            progressDialog= new SunSpinProgressDialog(getActivity());
            progressDialog.show();
            super.onPreExecute();
        }
    }
}
