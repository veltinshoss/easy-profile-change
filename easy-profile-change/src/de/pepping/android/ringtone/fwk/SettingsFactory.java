package de.pepping.android.ringtone.fwk;



import static de.pepping.android.ringtone.Constants.SDK_VERSION;
import static de.pepping.android.ringtone.fwk.Setting.AIRPLANE_MODE;
import static de.pepping.android.ringtone.fwk.Setting.AUTO_ROTATE;
import static de.pepping.android.ringtone.fwk.Setting.AUTO_SYNC;
import static de.pepping.android.ringtone.fwk.Setting.BLUETOOTH;
import static de.pepping.android.ringtone.fwk.Setting.BRIGHTNESS;
import static de.pepping.android.ringtone.fwk.Setting.FOUR_G;
import static de.pepping.android.ringtone.fwk.Setting.GPS;
import static de.pepping.android.ringtone.fwk.Setting.GROUP_HIDDEN;
import static de.pepping.android.ringtone.fwk.Setting.GROUP_VISIBLE;
import static de.pepping.android.ringtone.fwk.Setting.LOCK_PATTERN;
import static de.pepping.android.ringtone.fwk.Setting.MASTER_VOLUME;
import static de.pepping.android.ringtone.fwk.Setting.MOBILE_DATA;
import static de.pepping.android.ringtone.fwk.Setting.MOBILE_DATA_APN;
import static de.pepping.android.ringtone.fwk.Setting.RINGER;
import static de.pepping.android.ringtone.fwk.Setting.SCREEN_TIMEOUT;
import static de.pepping.android.ringtone.fwk.Setting.VOLUME;
import static de.pepping.android.ringtone.fwk.Setting.WIFI;
import static de.pepping.android.ringtone.fwk.Setting.WIFI_HOTSPOT;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import de.pepping.android.ringtone.R;
import de.pepping.android.ringtone.handler.AirplaneModeSettingHandler;
import de.pepping.android.ringtone.handler.BluetoothSettingHandler;
import de.pepping.android.ringtone.handler.BrightnessSettingHandler;
import de.pepping.android.ringtone.handler.BrightnessSettingHandlerX10;
import de.pepping.android.ringtone.handler.GpsSettingHandler;
import de.pepping.android.ringtone.handler.MasterVolumeSettingHandler;
import de.pepping.android.ringtone.handler.MobileDataSettingHandler2;
import de.pepping.android.ringtone.handler.RingerSettingHandler;
import de.pepping.android.ringtone.handler.ScreenTimeoutSettingHandler;
import de.pepping.android.ringtone.handler.SystemPropertySettingHandler;
import de.pepping.android.ringtone.handler.UnlockPatternSettingHandler;
import de.pepping.android.ringtone.handler.UnlockPatternSettingHandler22;
import de.pepping.android.ringtone.handler.VolumeControlSettingHandler;
import de.pepping.android.ringtone.handler.WiFiSettingHandler;
import de.pepping.android.ringtone.handler.WifiHopspotSettingHandler;
import de.pepping.android.ringtone.handler.apn.MobileDataSettingHandler;
import de.pepping.android.ringtone.handler.autosync.AutoSyncSettingHandler;
import de.pepping.android.ringtone.handler.wimax.FourGEvoSettingHandler;
import de.pepping.android.ringtone.preferences.AirplaneModePrefs;
import de.pepping.android.ringtone.preferences.BrightnessPrefs;
import de.pepping.android.ringtone.preferences.MobileDataPrefs;


public class SettingsFactory {

	/**
	 * Factory method
	 * @param id			setting ID
	 * @param index			setting index (within a sorted list)
	 * @param defaultText	setting's default description
	 * @param context		instance of Context (never null)
	 * @return				an instance of configured setting
	 */
	public static Setting createSetting(int id, int index, String defaultText, Context context) {
    	Setting setting;
    	switch (id) {
    	
	    	case WIFI:
	    		setting = new Setting(WIFI, R.drawable.ic_wifi, R.string.wifi_title, defaultText);
	    		break;
	    		
	    	case GPS:
	    		setting = new Setting(GPS, R.drawable.ic_gps, R.string.gps_title, defaultText);
	    		break;

	    	case RINGER:
	    		setting = new Setting(RINGER, R.drawable.ic_sound, R.string.txt_ringer, defaultText);
	    		setting.hasPopup = true;
	    		break;
	    	
	    	case BRIGHTNESS: {
	    		RangeSetting set = new RangeSetting(BRIGHTNESS, R.drawable.ic_brightness, R.string.txt_brightness, 0, 100);
	    		set.minIconId = R.drawable.ic_brightness_min;
	    		set.maxIconId = R.drawable.ic_brightness_max;
	    		set.prefs = BrightnessPrefs.class;
	    		setting = set;
	    		break;
    		}

	    	case AIRPLANE_MODE:
	    		setting = new Setting(AIRPLANE_MODE, R.drawable.ic_airplane, R.string.airmode_title, defaultText);
	    		setting.prefs = AirplaneModePrefs.class;
	    		break;

	    	case MOBILE_DATA_APN:
	    		if (isCDMA(context)) {
	    			return null; // not supported
	    		} else {
	    			setting = new Setting(MOBILE_DATA_APN, R.drawable.ic_apn, R.string.txt_apn_control, defaultText);
	    			setting.prefs = MobileDataPrefs.class;
	    		}
	    		break;
	    	
	    	case BLUETOOTH:
	    		setting = new Setting(BLUETOOTH, R.drawable.ic_bt, R.string.txt_bt, defaultText);
	    		break;
	    	
	    	case SCREEN_TIMEOUT:
	    		setting = new Setting(SCREEN_TIMEOUT, R.drawable.ic_screentimeout, R.string.txt_screen_timeout, defaultText);
	    		break;

	    	case VOLUME:
	    		setting = new Setting(VOLUME, R.drawable.ic_volume_control, R.string.txt_volume, context.getString(R.string.txt_volume_descr));
	    		break;
	    		
	    	case AUTO_SYNC:
	    		setting = new Setting(AUTO_SYNC, R.drawable.ic_autosync, R.string.txt_auto_sync, defaultText);
	    		break;

	    	case AUTO_ROTATE:
	    		setting = new Setting(AUTO_ROTATE, R.drawable.ic_rotate, R.string.txt_auto_rotate, defaultText);
	    		break;

	    	case LOCK_PATTERN:
	    		setting = new Setting(LOCK_PATTERN, R.drawable.ic_lock, R.string.txt_unlock_pattern, defaultText);
	    		break;

	    	case MASTER_VOLUME: {
	    		RangeSetting set = new RangeSetting(MASTER_VOLUME, R.drawable.ic_volume, R.string.txt_master_volume, 0, 15);
	    		set.minIconId = R.drawable.ic_volume_min;
	    		set.maxIconId = R.drawable.ic_volume_max;
	    		setting = set;
	    		break;
	    	}
	    		
	    	case WIFI_HOTSPOT:
	    		if (SDK_VERSION >= 8) {
	    			setting = new Setting(WIFI_HOTSPOT, R.drawable.ic_wifi_hs, R.string.txt_wifi_hotspot, defaultText);
	    		} else {
	    			return null; // not supported on this device
	    		}
	    		break;
	    	
	    	case MOBILE_DATA:
	    		if (SDK_VERSION < 10) { /*2.3.3*/
	    			setting = new Setting(MOBILE_DATA, R.drawable.ic_3g, R.string.txt_mobile_data, defaultText);
	    		} else {
	    			return null;
	    		}
	    		break;
	    		
	    	case GROUP_VISIBLE:
	    		setting = new Setting(id, R.string.txt_visible_settings);
	    		break;
	    		
	    	case GROUP_HIDDEN:
	    		setting = new Setting(id, R.string.txt_hidden_settings);
	    		break;
    		
	    	case FOUR_G:
	    		if (SDK_VERSION >= 8 && Build.MODEL.equals("PC36100")) {
	    			setting = new Setting(FOUR_G, R.drawable.ic_4g, R.string.txt_four_g, defaultText);
	    		} else {
	    			return null; // not supported
	    		}
	    		break;
	    	
	    	default: 
	    		throw new IllegalStateException("unsupported setting type: " + id);
	    		
    	}
    	
    	setting.index = index;
    	return setting;
	}
	
	/**
	 * Factory method
	 * @param setting	a setting for which a handler will be created
	 * @return			a setting handler
	 */
    public static SettingHandler createSettingHandler(Setting setting) {
    	
    	switch (setting.id) {
    	
	    	// create handlers
    		case WIFI: return new WiFiSettingHandler(setting);
	    	case GPS: return new GpsSettingHandler(setting);
	    	case RINGER: return new RingerSettingHandler(setting);
	    	case BRIGHTNESS: {
	    		if (SDK_VERSION == BrightnessSettingHandlerX10.SDK_VERSION && Build.DEVICE.equals(BrightnessSettingHandlerX10.DEVICE)) {
	    			return new BrightnessSettingHandlerX10(setting);
	    		} else {
	    			return new BrightnessSettingHandler(setting);
	    		}
	    	} 
	    	case AIRPLANE_MODE: return new AirplaneModeSettingHandler(setting);
	    	case MOBILE_DATA_APN: return new MobileDataSettingHandler(setting);
	    	case BLUETOOTH: return new BluetoothSettingHandler(setting);
	    	case SCREEN_TIMEOUT: return new ScreenTimeoutSettingHandler(setting);
	    	case VOLUME: return new VolumeControlSettingHandler(setting);
	    	case AUTO_SYNC: return new AutoSyncSettingHandler(setting);
	    	case AUTO_ROTATE: return new SystemPropertySettingHandler(setting, Settings.System.ACCELEROMETER_ROTATION, Settings.ACTION_DISPLAY_SETTINGS);
	    	case LOCK_PATTERN: return SDK_VERSION >= 8 ? new UnlockPatternSettingHandler22(setting) : new UnlockPatternSettingHandler(setting);
	    	case MASTER_VOLUME: return new MasterVolumeSettingHandler(setting);
	    	case WIFI_HOTSPOT: return new WifiHopspotSettingHandler(setting);
	    	case MOBILE_DATA: return new MobileDataSettingHandler2(setting);
	    	case FOUR_G : return new FourGEvoSettingHandler(setting);
	    	
	    	// null-handlers
	    	case Setting.GROUP_VISIBLE:
	    	case Setting.GROUP_HIDDEN:
    		default: 
    			return null;
    	}
    }

    private static final boolean isCDMA(Context context) {
    	TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	return telephony != null && telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA;
    }
	
}
