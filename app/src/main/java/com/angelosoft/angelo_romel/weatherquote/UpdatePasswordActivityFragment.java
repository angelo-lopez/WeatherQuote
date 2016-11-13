package com.angelosoft.angelo_romel.weatherquote;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpdatePasswordActivityFragment extends Fragment {
    private EditText textOldPassword;
    private EditText textNewPassword;
    private EditText textConfirmPassword;
    private Button buttonUpdatePassword;
    private Button buttonCancel;
    private SunSpinProgressDialog progressDialog;

    public UpdatePasswordActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        textOldPassword = (EditText)view.findViewById(R.id.edit_old_password);
        textNewPassword = (EditText)view.findViewById(R.id.edit_new_password);
        textConfirmPassword = (EditText)view.findViewById(R.id.edit_confirm_password);
        buttonUpdatePassword = (Button)view.findViewById(R.id.button_update_password);
        buttonCancel = (Button)view.findViewById(R.id.button_cancel);

        buttonUpdatePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!textOldPassword.getText().toString().trim().equals("") &&
                        !textNewPassword.getText().toString().trim().equals("") &&
                        !textConfirmPassword.getText().toString().trim().equals("")) {
                    if (textNewPassword.getText().toString().equals(textConfirmPassword.getText().toString())) {
                        progressDialog = new SunSpinProgressDialog(getActivity());
                        buttonUpdatePassword.setEnabled(false);
                        progressDialog.show();
                        try {
                            String email;
                            Firebase mFireRef = new Firebase(getResources().getString(R.string.firebase_url));
                            AuthData authData = mFireRef.getAuth();
                            email = authData.getProviderData().get("email").toString().trim();
                            mFireRef.changePassword(email, textOldPassword.getText().toString().trim(),
                                    textNewPassword.getText().toString().trim(), new Firebase.ResultHandler() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(getActivity(), "Your password have been " +
                                                    "successfully changed.", Toast.LENGTH_LONG).show();
                                            getActivity().finish();
                                        }

                                        @Override
                                        public void onError(FirebaseError firebaseError) {
                                            buttonUpdatePassword.setEnabled(true);
                                            progressDialog.dismiss();
                                            switch (firebaseError.getCode()) {
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
                        }
                        catch(Exception ex) {
                            buttonUpdatePassword.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    } else {
                        HelperMethod.displayMessage("The passwords does not match.", getActivity());
                    }
                }
                else {
                    HelperMethod.displayMessage("Please enter values for the old password, " +
                            "new password and confirm password.", getActivity());
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Update Your Password");
    }
}
