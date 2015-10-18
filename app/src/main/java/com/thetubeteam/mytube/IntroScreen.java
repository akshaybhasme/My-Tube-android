package com.thetubeteam.mytube;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class IntroScreen extends Activity {

	private Button btnOAuthGooglePlus;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		SharedPreferencesCredentialStore store = new SharedPreferencesCredentialStore(prefs);

		if(store.getAccessToken(Oauth2Params.YOUTUBE.getUserId()) != null){
			startMainScreen(Oauth2Params.YOUTUBE);
		}
		
		btnOAuthGooglePlus = (Button)findViewById(R.id.btn_oauth_googleplus);
		
		btnOAuthGooglePlus.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startOauthFlow(Oauth2Params.YOUTUBE);
			}
		});

	}

	/**
	 * Starts the main screen where we show the API results.
	 * 
	 * @param oauth2Params
	 */
	private void startMainScreen(Oauth2Params oauth2Params) {
		Constants.OAUTH2PARAMS = oauth2Params;
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * Starts the activity that takes care of the OAuth2 flow
	 * 
	 * @param oauth2Params
	 */
	private void startOauthFlow(Oauth2Params oauth2Params) {
		Constants.OAUTH2PARAMS = oauth2Params;
		startActivity(new Intent().setClass(this,OAuthAccessTokenActivity.class));
	}	
	
	/**
	 * Clears our credentials (token and token secret) from the shared preferences.
	 * We also setup the authorizer (without the token).
	 * After this, no more authorized API calls will be possible.
	 * @throws IOException 
	 */
    private void clearCredentials(Oauth2Params oauth2Params)  {
		try {
			new OAuth2Helper(prefs,oauth2Params).clearCredentials();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
	}

}
