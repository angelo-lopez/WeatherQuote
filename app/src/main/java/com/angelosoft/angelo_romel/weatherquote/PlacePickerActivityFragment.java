package com.angelosoft.angelo_romel.weatherquote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class PlacePickerActivityFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    public PlacePickerActivityFragment() {
    }

    //private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private Button buttonGetPlace;
    //private static  LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
    //        new LatLng(54, -2), new LatLng(54, -2));
    private static  LatLngBounds BOUNDS_MOUNTAIN_VIEW;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_picker, container, false);

        float lat = Float.parseFloat(getResources().getString(R.string.lat));
        float lng = Float.parseFloat(getResources().getString(R.string.lng));
        BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                new LatLng(lat, lng),
                new LatLng(lat, lng));
        //getResources().getString(R.string.lat);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(2);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        buttonGetPlace = (Button) view.findViewById(R.id.button_add_place);

        buttonGetPlace.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String selectedPlace = mAutocompleteTextView.getText().toString();
                if(!selectedPlace.equals("")) {
                    prefs.edit().putString("selectedPlace", selectedPlace).apply();
                    prefs.edit().putBoolean("isNewPlaceAdded", true).apply();
                    getActivity().finish();
                }
                else {
                    Toast.makeText(getActivity(), "Please enter a place.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }//end onCreateView

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Need to set options menu to true to make action bar home button to work.
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //No menu layout needed as long as setHasOptionsMenu is set to true.
        //inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle action bar item clicks here. The action bar will automatically
        //handle clicks on the Home/Up button, so long as you specify a
        //parent activity in AndroidManifest.xml. But in this case, parent activity property name
        //in the AndroidManifest to allow more than one fragment access one layout.
        int id = item.getItemId();
        //Catch the event when the user clicks on the action bar's back button.
        if(id == android.R.id.home) {
            prefs.edit().putString("selectedPlace", "").apply();
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            //Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            //Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);

            //Dismiss soft/virtual keyboard.
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAutocompleteTextView.getWindowToken(), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //Log.e(LOG_TAG, "Place query did not complete. Error: " +
                //        places.getStatus().toString());
                places.release();
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            //CharSequence attributions = places.getAttributions();
            places.release();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        //Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.e(LOG_TAG, "Google Places API connection failed with error code: "
        //        + connectionResult.getErrorCode());

        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        //Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}
