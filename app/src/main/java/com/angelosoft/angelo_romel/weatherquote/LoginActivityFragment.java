package com.angelosoft.angelo_romel.weatherquote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class LoginActivityFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 1;
    //private final String LOG_TAG = MainActivity.class.getSimpleName();
    private EditText textEmail;
    private EditText textPassword;

    private SharedPreferences prefs;
    private SunSpinProgressDialog progressDialog;

    public LoginActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        prefs.edit().putBoolean("isNewPlaceAdded", false).apply();
        textEmail = (EditText) view.findViewById(R.id.edit_new_email);
        textPassword = (EditText) view.findViewById(R.id.edit_new_password);

        final Button buttonLogin = (Button) view.findViewById(R.id.button_login);
        final Button buttonSkipLogin = (Button) view.findViewById(R.id.button_skip_login);
        final Button buttonRegister = (Button) view.findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonLogin.setEnabled(false);
                if (HelperMethod.isValidEmail(textEmail.getText().toString())
                        && !textPassword.getText().toString().isEmpty()) {
                    progressDialog = new SunSpinProgressDialog(getActivity());
                    progressDialog.show();
                    final Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
                    mFireRef.authWithPassword(textEmail.getText().toString(),
                            textPassword.getText().toString(), new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {//authData.getUid()
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                    prefs.edit().putString("uid", authData.getUid()).apply();
                                    Intent intent = new Intent(getActivity(),
                                            FavLocationAuthActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                    buttonLogin.setEnabled(true);
                                    switch(firebaseError.getCode()) {
                                        case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
                                            HelperMethod.displayMessage("The authentication provider is disabled " +
                                            "for this provider.", getActivity());
                                            break;
                                        case FirebaseError.INVALID_AUTH_ARGUMENTS:
                                            HelperMethod.displayMessage("The credentials provided are malformed or " +
                                                    "incomplete.", getActivity());
                                            break;
                                        case FirebaseError.INVALID_CONFIGURATION:
                                            HelperMethod.displayMessage("The authentication provider is " +
                                                    "misconfigured.", getActivity());
                                            break;
                                        case FirebaseError.INVALID_CREDENTIALS:
                                            HelperMethod.displayMessage("Authentication credentials are invalid.",
                                                    getActivity());
                                            break;
                                        case FirebaseError.INVALID_EMAIL:
                                            HelperMethod.displayMessage("The specified email is not valid.", getActivity());
                                            break;
                                        case FirebaseError.INVALID_PASSWORD:
                                            HelperMethod.displayMessage("The specified password is incorrect.", getActivity());
                                            break;
                                        case FirebaseError.NETWORK_ERROR:
                                            HelperMethod.displayMessage("Error connecting to the authentication " +
                                                    "server.", getActivity());
                                            break;
                                        case FirebaseError.UNKNOWN_ERROR:
                                            HelperMethod.displayMessage("An unknown error has occurred.", getActivity());
                                            break;
                                        case FirebaseError.USER_DOES_NOT_EXIST:
                                            HelperMethod.displayMessage("The user account does not exist.", getActivity());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                } else {
                    buttonLogin.setEnabled(true);
                    prefs.edit().putString("uid", "0").apply();
                    HelperMethod.displayMessage("Sorry. Email format is invalid.",
                            getActivity());
                }
            }
        });

        buttonSkipLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefs.edit().putString("uid", "0").apply();
                Intent intent = new Intent(getContext(),
                        FavLocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });



        return view;
    }//end onCreateView

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }
        catch(Exception ex) {
        }

        Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
        AuthData auth = mFireRef.getAuth();
        if (auth != null) {
            prefs.edit().putString("uid", auth.getUid()).apply();

            Intent intent = new Intent(getActivity(), FavLocationAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();

        } else {
            if(prefs.getString("isSigned", "false").equals("true")) {
                prefs.edit().putString("uid", "0").apply();

                Intent intent = new Intent(getActivity(), FavLocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }

    }//end onCreate

    @Override
    public void onCreateOptionsMenu(Menu menu,  MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_login, menu);
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_password_reset) {
            if (HelperMethod.isValidEmail(textEmail.getText().toString())) {
                Firebase ref = new Firebase(getResources().getString(R.string.firebase_url));
                ref.resetPassword(textEmail.getText().toString(), new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        HelperMethod.displayMessage("A temporary password has been sent to your email account.", getActivity());
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        switch(firebaseError.getCode()) {
                            case FirebaseError.AUTHENTICATION_PROVIDER_DISABLED:
                                HelperMethod.displayMessage("Sorry, the authentication provided is disabled.", getActivity());
                                break;
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                HelperMethod.displayMessage("Sorry, the user does not exist.", getActivity());
                                break;
                            case FirebaseError.INVALID_EMAIL:
                                HelperMethod.displayMessage("Sorry, invalid email.", getActivity());
                                break;
                            case FirebaseError.INVALID_CREDENTIALS:
                                HelperMethod.displayMessage("Sorry, invalid credentials.", getActivity());
                                break;
                            case FirebaseError.INVALID_PROVIDER:
                                HelperMethod.displayMessage("Sorry, invalid email provider.", getActivity());
                                break;
                            default:
                                HelperMethod.displayMessage(firebaseError.getDetails().toString(), getActivity());
                                break;
                        }
                    }
                });
            }
            else {
                HelperMethod.displayMessage("Please ensure that you have correctly entered your email address.", getActivity());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
