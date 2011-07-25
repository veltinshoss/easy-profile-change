package de.pepping.android.ringtone.util;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import de.pepping.android.ringtone.Constants;
import de.pepping.android.ringtone.R;

public class RingtoneUtil implements Constants{

	public static boolean isRingAndVibrate(AudioManager mAudioManager){
		int ringerMode = mAudioManager.getRingerMode();
        int vibrateMode = mAudioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
		return ringerMode==AudioManager.RINGER_MODE_NORMAL && vibrateMode==AudioManager.VIBRATE_SETTING_ON;
	}
	
	public static boolean isRingOnly(AudioManager mAudioManager){
		int ringerMode = mAudioManager.getRingerMode();
		int vibrateMode = mAudioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
		return ringerMode==AudioManager.RINGER_MODE_NORMAL && vibrateMode==AudioManager.VIBRATE_SETTING_OFF;
	}
	
	public static boolean isVibrateOnly(AudioManager mAudioManager){
		int ringerMode = mAudioManager.getRingerMode();
		int vibrateMode = mAudioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
		return ringerMode==AudioManager.RINGER_MODE_VIBRATE && vibrateMode==AudioManager.VIBRATE_SETTING_ON;
	}
	
	public static boolean isSilent(AudioManager mAudioManager){
		int ringerMode = mAudioManager.getRingerMode();
		return ringerMode==AudioManager.RINGER_MODE_SILENT;
	}
	
	public static boolean isAirPlaneModeEnabled(ContentResolver contentResolver) {
		return Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0) == 1;
	}
	
	public static void setRingAndVibrate(AudioManager audiomanager){
		audiomanager.setRingerMode( AudioManager.RINGER_MODE_NORMAL);
		audiomanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
	}
	
	public static void setRingOnly(AudioManager audiomanager){
		audiomanager.setRingerMode( AudioManager.RINGER_MODE_NORMAL);
		audiomanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
	}
	
	public static void setVibrateOnly(AudioManager audiomanager){
		audiomanager.setRingerMode( AudioManager.RINGER_MODE_VIBRATE);
		audiomanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
	}
	
	public static void setSilent(AudioManager audiomanager){
		audiomanager.setRingerMode( AudioManager.RINGER_MODE_SILENT);
	}
	
	public static String getRingToneAsString(AudioManager audiomanager){
		int ringerMode = audiomanager.getRingerMode();
		int vibrateMode = audiomanager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
		if( ringerMode==AudioManager.RINGER_MODE_SILENT){
			return SILENT;
		}
		if(ringerMode==AudioManager.RINGER_MODE_NORMAL && vibrateMode==AudioManager.VIBRATE_SETTING_ON){
			return RING_AND_VIBRATE;
		}
		if(ringerMode==AudioManager.RINGER_MODE_NORMAL && vibrateMode==AudioManager.VIBRATE_SETTING_OFF){
			return RING_ONLY;
		}
		if(ringerMode==AudioManager.RINGER_MODE_VIBRATE && vibrateMode==AudioManager.VIBRATE_SETTING_ON){
			return VIBRATE_ONLY;
		}
		return "";
	}
	
	public static void setRingToneWithString(String ringtone,AudioManager audiomanager){
		if(ringtone==null || ringtone.length()==0){
			return;
		}
		if(RING_AND_VIBRATE.equals(ringtone)){
			setRingAndVibrate(audiomanager);
		}
		if(RING_ONLY.equals(ringtone)){
			setRingOnly(audiomanager);
		}
		if(VIBRATE_ONLY.equals(ringtone)){
			setVibrateOnly(audiomanager);
		}
		if(SILENT.equals(ringtone)){
			setSilent(audiomanager);
		}
	}
	
	public static String convertRingToneProfileToReadable(String ringToneProfile, Context context){
		String result = "";
		if(ringToneProfile==null || ringToneProfile.length()==0){
			return result;
		}
		if(RING_AND_VIBRATE.equals(ringToneProfile)){
			return context.getString(R.string.textRingAndVibrateToast);
		}else if(RING_ONLY.equals(ringToneProfile)){
			return context.getString(R.string.textRingOnlyToast);
		}else if(VIBRATE_ONLY.equals(ringToneProfile)){
			return context.getString(R.string.textVibrateOnlyToast);
		}else if(SILENT.equals(ringToneProfile)){
			return context.getString(R.string.textSilentToast);
		}else{
			return result;
		}
	}
}
