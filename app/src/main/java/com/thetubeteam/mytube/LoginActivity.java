package com.thetubeteam.mytube;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity {

    public static final String TAG = "LoginActivty";

    /* RequestCode for resolutions involving sign-in */
    private static final int RC_SIGN_IN = 1;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSignInClicked();
            }

        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void onSignInClicked(){
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected:" + bundle);
            mShouldResolve = false;
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
//                String personPhoto = currentPerson.getImage().getUrl();
//                String personGooglePlusProfile = currentPerson.getUrl();
                Toast.makeText(LoginActivity.this, "Signed in as "+personName, Toast.LENGTH_LONG).show();
                new GetAccessTokenAsync(accountName).execute();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

    };

    GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed:" + connectionResult);

            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(LoginActivity.this, RC_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    // Could not resolve the connection result, show the user an
                    // error dialog.
                    showErrorDialog(connectionResult);
                }
            }
        }

    };

    private void showErrorDialog(ConnectionResult connectionResult) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mShouldResolve = false;
                            }
                        }).show();
            } else {
                Log.w(TAG, "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

                mShouldResolve = false;
            }
        }
    }

    public class GetAccessTokenAsync extends AsyncTask<String, Integer, String>{

        String accountName;

        public GetAccessTokenAsync(String accountName){
            this.accountName = accountName;
        }

        @Override
        protected String doInBackground(String... strings) {
            String token = null;
            try{
                token = GoogleAuthUtil.getToken(LoginActivity.this, accountName, "oauth2:https://www.googleapis.com/auth/userinfo.profile");
            }catch (Exception e){
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
//            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
//            PlaylistUpdates.ACCESS_TOKEN = s;
            Log.e(TAG, s);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
