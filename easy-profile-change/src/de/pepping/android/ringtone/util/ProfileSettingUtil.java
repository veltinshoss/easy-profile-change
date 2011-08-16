package de.pepping.android.ringtone.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import de.pepping.android.ringtone.database.DataHelper;
import de.pepping.android.ringtone.fwk.ProfileSettings;
import de.pepping.android.ringtone.handler.WifiHopspotSettingHandler.WifiApManager;
import de.pepping.android.ringtone.handler.bluetooth.BluetoothControl;
import de.pepping.android.ringtone.handler.bluetooth.BluetoothControl16;
import de.pepping.android.ringtone.handler.bluetooth.BluetoothControl20;

public class ProfileSettingUtil {
	
	public static void setProfileById(int profileId, Activity activity, DataHelper data ){
		final ProfileSettings newProfile = data.findProfileSettingById(profileId);
		setSystemBrightness(activity ,newProfile.brightness);
		setRinger(activity ,newProfile.ringer_mode);
		setVolumen(activity ,newProfile);
		setBluetooth(activity ,newProfile);
		setWifi(activity ,newProfile);
		setScreenTimeout(activity ,newProfile);
		setAirplaneMode(activity ,newProfile);
		setAutoRotation(activity ,newProfile);
		
	}
	
	private static void setAutoRotation(Activity activity,ProfileSettings newProfile) {
		Settings.System.putInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, newProfile.auto_rotation);
	}

	private static void setAirplaneMode(Activity activity,
			ProfileSettings newProfile) {
		// update setting
		Settings.System.putInt(activity.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, newProfile.airplane_mode==1 ? 1 : 0);
		// notify change
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", newProfile.airplane_mode==1);
		activity.sendBroadcast(intent);
		
	}

	private static void setScreenTimeout(Activity activity,
			ProfileSettings newProfile) {
		Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, newProfile.screen_timeout);
		
	}

	private static void setWifi(Activity activity, ProfileSettings newProfile) {
		
		if (newProfile.wifi==1 && Integer.parseInt(Build.VERSION.SDK) >= 8) {
			WifiApManager wifiApManager = new WifiApManager(activity);
			int state = wifiApManager.getWifiApState();
			if (state == WifiApManager.WIFI_AP_STATE_ENABLED || state == WifiApManager.WIFI_AP_STATE_ENABLING) {
				wifiApManager.setWifiApEnabled(false);
			}
		}
		getWiFiManager(activity).setWifiEnabled(newProfile.wifi==1);
		
	}
	
	private static WifiManager getWiFiManager(Activity activity) {
		WifiManager mWiFiManager =null;
		if (mWiFiManager == null)
			mWiFiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		return mWiFiManager;
	}

	private static void setBluetooth(Activity activity, ProfileSettings newProfile) {
		
		BluetoothControl mBluetoothControl = null;
		try {
			// try with 1.6
			mBluetoothControl = new BluetoothControl16(activity);
		} catch(Exception e) {
			try {
				mBluetoothControl = new BluetoothControl20(); // 2.0+ adapter
			} catch(Exception e1) {
				
			}
		}
		if(mBluetoothControl!=null){
			mBluetoothControl.setEnabled(newProfile.bluetooth==1);
		}
	}

	private static void setVolumen(Activity activity, ProfileSettings newProfile) {
		AudioManager manager = (AudioManager) activity.getSystemService(Activity.AUDIO_SERVICE);
		manager.setStreamVolume(AudioManager.STREAM_ALARM, newProfile.ringer_stream_alarm, 0);
		manager.setStreamVolume(AudioManager.STREAM_RING, newProfile.ringer_stream_ring, 0);
		manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, newProfile.ringer_stream_notification, 0);
		manager.setStreamVolume(AudioManager.STREAM_MUSIC, newProfile.ringer_stream_music, 0);
		manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, newProfile.ringer_stream_voice_call, 0);
		manager.setStreamVolume(AudioManager.STREAM_SYSTEM, newProfile.ringer_stream_system, 0);
	}

	private static void setRinger(Activity mActivity, int ringer) {
		AudioManager manager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		if (manager != null) {

			int ringerMode = AudioManager.RINGER_MODE_NORMAL;
			int vibroMode = AudioManager.VIBRATE_SETTING_ON;
			
			switch(ringer) {
				case 0: // silent
					ringerMode = AudioManager.RINGER_MODE_SILENT;
					vibroMode = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
					break;
				case 1: // vibro
					ringerMode = AudioManager.RINGER_MODE_VIBRATE;
					vibroMode = AudioManager.VIBRATE_SETTING_ON;
					break;
				case 2: // sound
					ringerMode = AudioManager.RINGER_MODE_NORMAL;
					vibroMode = AudioManager.VIBRATE_SETTING_OFF;
					break;
				case 3: // sound and vibro
					ringerMode = AudioManager.RINGER_MODE_NORMAL;
					vibroMode = AudioManager.VIBRATE_SETTING_ON;
					break;
			}
			
			// update manager modes
			
			// update
			manager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, vibroMode);
			manager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, vibroMode);
			manager.setRingerMode(ringerMode);
		}
		
	}

	private static void setSystemBrightness(Activity activity, int brightness) {
		Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
		
		int UPDATE_TIMEOUT = 45; // ms
		LayoutParams attrs = null;
		if (attrs == null) {
			attrs = activity.getWindow().getAttributes();
		}
		int MAXIMUM_BACKLIGHT = 255;
		attrs.screenBrightness = brightness / (float)MAXIMUM_BACKLIGHT;
		// request brightness update
		Window window = activity.getWindow();
		window.setAttributes(attrs);
	}
	
}
