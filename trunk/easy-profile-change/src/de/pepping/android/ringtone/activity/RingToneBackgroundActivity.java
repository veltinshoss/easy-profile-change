package de.pepping.android.ringtone.activity;

import roboguice.activity.GuiceActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import de.pepping.android.ringtone.Constants;
import de.pepping.android.ringtone.database.DataHelper;
import de.pepping.android.ringtone.fwk.ProfileSettings;
import de.pepping.android.ringtone.util.ProfileSettingUtil;

public class RingToneBackgroundActivity extends GuiceActivity implements Constants{

	private DataHelper dh;
	private int activeProfileId;
	
    public RingToneBackgroundActivity(){}
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Bundle extras = getIntent().getExtras(); 
        if(extras!=null && extras.containsKey("PROFILE_ID")){
        	activeProfileId = extras.getInt("PROFILE_ID");
        }
        
        // init DB
        this.dh = new DataHelper(this);
        
        new ProfileSettingUtil().setProfileById(activeProfileId, this, dh);
        
        Handler	handler = new Handler();
        ExitThread bt = new ExitThread();
		handler.removeCallbacks(bt);
		int UPDATE_TIMEOUT = 45;
		handler.postAtTime(bt, SystemClock.uptimeMillis() + UPDATE_TIMEOUT);
		
        // NOTIFICATION - das neues Profil drin ist

    }
	
	private void exitApp(String endText){

    	final SharedPreferences.Editor editor = getCustomPreferences().edit();
    	editor.putInt("activeProfileId", activeProfileId);
    	editor.commit();
    	
    	boolean isSplashScreen  = getCustomPreferences().getBoolean(PREF_SPLASH_SCREEN, false);
    	if(isSplashScreen){
    		Intent intent = new Intent("de.pepping.android.ringtone.intent.action.CLOSE_ACTIVITY");
    		intent.putExtra(CloseActivity.KEY_TEXT_TO_SHOW, endText);
    		startActivity(intent);
    	}
   		finish();
	}
	
    private SharedPreferences getCustomPreferences(){
    	return getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }
    
	class ExitThread implements Runnable{
		@Override
		public void run() {
	        ProfileSettings findProfileSettingById = dh.findProfileSettingById(activeProfileId);
			exitApp(findProfileSettingById.profileName);
		}
	}
	
}