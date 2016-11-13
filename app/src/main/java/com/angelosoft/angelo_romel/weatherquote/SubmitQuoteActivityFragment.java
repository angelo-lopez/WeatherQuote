package com.angelosoft.angelo_romel.weatherquote;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubmitQuoteActivityFragment extends Fragment {
    private EditText editQuote;
    private EditText editName;
    private TextView textCharCount;
    private Button buttonSubmitQuote;
    private CollapsingToolbarLayout collapsingToolbar;
    private SunSpinProgressDialog progressDialog;
    //private InterstitialAd interstitialAd;

    AdRequest adRequest;

    public SubmitQuoteActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        //Uncomment below before generating release build.
        adRequest = new AdRequest.Builder().build();

        //Test device id = 88418297FE29DF850CF429E3442F5CEA
        //Test ad:
        //adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("88418297FE29DF850CF429E3442F5CEA")  // An example device ID
        //        .build();
        /*
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if(interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClosed() {}
        });
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_quote, container, false);
        editQuote = (EditText)view.findViewById(R.id.quote_edittext);
        editName = (EditText)view.findViewById(R.id.name_edittext);
        textCharCount = (TextView)view.findViewById(R.id.char_count_textview);
        buttonSubmitQuote = (Button)view.findViewById(R.id.button_submit_quote);

        AdView adView = (AdView)view.findViewById(R.id.adViewNowForecast);
        adView.loadAd(adRequest);

        buttonSubmitQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editQuote.getText().length() == 0) {
                    Toast.makeText(getActivity(), "There is no quote to submit.", Toast.LENGTH_SHORT).show();
                }
                else if(editQuote.getText().length() > 0 && editQuote.getText().length() < 15) {
                    Toast.makeText(getActivity(), "The quote is too short.", Toast.LENGTH_SHORT).show();
                }
                else {
                    buttonSubmitQuote.setEnabled(false);
                    if(progressDialog == null) {
                        progressDialog = new SunSpinProgressDialog(getActivity());
                    }
                    progressDialog.show();

                    Firebase fireRef = new Firebase(getActivity().getString(R.string.firebase_url));
                    Firebase quoteRef = fireRef.child("user_quotes").child("quotes_1");
                    Map<String, Object> userQuote = new HashMap<String, Object>();
                    userQuote.put("quote", editQuote.getText().toString().trim() + " - " +
                            editName.getText().toString().trim());
                    quoteRef.push().setValue(userQuote, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if(firebaseError != null) {
                                HelperMethod.displayMessage("Sorry, we ran into some problems and your quote could " +
                                        "not be saved. Please try again later.", getActivity());
                                buttonSubmitQuote.setEnabled(true);
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            else {
                                Toast.makeText(getActivity(), "Thank you for sharing your quote.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                progressDialog = null;
                                getActivity().finish();
                            }
                        }
                    });
                }
            }
        });

        editQuote.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {}

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                textCharCount.setText("Type your weather quote here (" + text.length() + "/200)");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            /*
            * Code below fires when user hits the Android back button(Button at bottom of screen).
            * Code is needed as meta-data "ParentActivityName" is disabled to allow two fragments to
            * use one xml file.
            * */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    public void requestNewInterstitial() {
        //Uncomment below before generating release build.
        AdRequest adRequest = new AdRequest.Builder().build();

        //Test device id = 88418297FE29DF850CF429E3442F5CEA
        //Test ad:
        //AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice("88418297FE29DF850CF429E3442F5CEA")
        //        .build();

        //interstitialAd.loadAd(adRequest);
    }
    */
}
