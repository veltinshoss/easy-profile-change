package de.pepping.android.ringtone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import de.pepping.android.ringtone.Constants;
import de.pepping.android.ringtone.util.RingtoneUtil;

public class TimerService extends BroadcastReceiver implements Constants{

    @Override
    public void onReceive(Context context, Intent intent){
    	final Bundle extras = intent.getExtras();
        if(extras!=null && extras.containsKey(INTENT_PARAM_PROFILE)){
        	SharedPreferences pref = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        	boolean isVibrate = pref.getBoolean(PREF_NOTIFICATION_VIBRATE, false);
        	
        	if(RING_AND_VIBRATE.equals(extras.getString(INTENT_PARAM_PROFILE))){
        		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        		RingtoneUtil.setRingAndVibrate(mAudioManager);
        	}
        	if(RING_ONLY.equals(extras.getString(INTENT_PARAM_PROFILE))){
        		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        		RingtoneUtil.setRingOnly(mAudioManager);
        	}
        	if(VIBRATE_ONLY.equals(extras.getString(INTENT_PARAM_PROFILE))){
        		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        		RingtoneUtil.setVibrateOnly(mAudioManager);
        	}
        	if(SILENT.equals(extras.getString(INTENT_PARAM_PROFILE))){
        		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        		RingtoneUtil.setSilent(mAudioManager);
        	}
        	
        	if(isVibrate){
        		((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
        	}
        }
   }

}
