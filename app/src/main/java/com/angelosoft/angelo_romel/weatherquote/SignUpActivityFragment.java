package com.angelosoft.angelo_romel.weatherquote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class SignUpActivityFragment extends Fragment {
    private SharedPreferences prefs;

    private EditText editEmail;
    private EditText editNewPassword;
    private EditText editConfirmPassword;

    private SunSpinProgressDialog progressDialog;

    public SignUpActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        editEmail = (EditText) view.findViewById(R.id.edit_new_email);
        editNewPassword = (EditText) view.findViewById(R.id.edit_new_password);
        editConfirmPassword = (EditText) view.findViewById(R.id.edit_confirm_password);

        final Button buttonSignUp = (Button) view.findViewById(R.id.button_proceed_sign_up);
        final Button buttonCancelSignUp = (Button) view.findViewById(R.id.button_cancel_sign_up);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSignUp.setEnabled(false);
                final String email = editEmail.getText().toString();
                final String password = editNewPassword.getText().toString();
                final String confirmPassword = editConfirmPassword.getText().toString();
                if (isValidEmailAndPassword(email, password, confirmPassword)) {
                    progressDialog = new SunSpinProgressDialog(getActivity());
                    progressDialog.show();
                    final Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
                    mFireRef.createUser(email, password,
                            new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Toast.makeText(getActivity(), "Thank you for signing up.", Toast.LENGTH_SHORT).show();
                            login(email, password);
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            progressDialog.dismiss();
                            progressDialog = null;
                            prefs.edit().putString("authType", "none").apply();
                            prefs.edit().putString("uid", "0").apply();
                            buttonSignUp.setEnabled(true);
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
                                case FirebaseError.EMAIL_TAKEN:
                                    HelperMethod.displayMessage("The specified email address is already " +
                                    "in use.", getActivity());
                                default:
                                    break;
                            }
                        }
                    });

                } else {
                    prefs.edit().putString("uid", "0").apply();
                    buttonSignUp.setEnabled(true);
                    HelperMethod.displayMessage("Please make sure that you have" +
                            " entered a valid email and that the passwords match.", getActivity());
                }
            }
        });

        buttonCancelSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValidEmailAndPassword(String email, String password, String confirmPassword){
        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }
        else {
            if(password.equals(confirmPassword)) {
                if(HelperMethod.isValidEmail(email)) {
                    return true;
                }
                else{
                    return false;
                }
            }
            else {
                return false;
            }
        }
    }//end isValidEmailAndPassword

    private void login(final String email, String password) {
        final Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
        mFireRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        progressDialog.dismiss();
                        progressDialog= null;
                        prefs.edit().putString("uid", authData.getUid()).apply();

                        mFireRef.child("users").child(authData.getUid()).child("favlocations").
                                setValue("");
                        mFireRef.child("users").child(authData.getUid()).child("email").
                                setValue(email);

                        Intent intent = new Intent(getActivity(),
                                FavLocationAuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        prefs.edit().putString("uid", "0").apply();
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
    }//end login

}
