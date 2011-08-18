package de.pepping.android.ringtone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import de.pepping.android.ringtone.Constants;

public class TimerService extends BroadcastReceiver implements Constants{

    @Override
    public void onReceive(Context context, Intent intent){
    	final Bundle extras = intent.getExtras();
        if(extras!=null && extras.containsKey(INTENT_PARAM_PROFILE)){
        	int profileId = extras.getInt(INTENT_PARAM_PROFILE);
    		
        	Intent intentBackgroundProfilActivity = new Intent();
    		intentBackgroundProfilActivity.setClassName(context.getPackageName(), "de.pepping.android.ringtone.activity.RingToneBackgroundActivity");
    		intentBackgroundProfilActivity.putExtra("PROFILE_ID", profileId);
    		intentBackgroundProfilActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		
    		context.startActivity(intentBackgroundProfilActivity);
        }
   }

}
