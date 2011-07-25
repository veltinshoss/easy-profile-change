package de.pepping.android.ringtone;

import android.os.Build;


public interface Constants {

	public static final String INTENT_PARAM_PROFILE = "INTENT_PARAM_PROFILE";
	public static final String RING_AND_VIBRATE = "RING_AND_VIBRATE";
	public static final String RING_ONLY = "RING_ONLY";
	public static final String VIBRATE_ONLY = "VIBRATE_ONLY";
	public static final String SILENT = "SILENT";
	
	public static final String PREF_NOTIFICATION_VIBRATE = "prefNotificationVibrate";
	public static final String PREF_SPLASH_SCREEN = "prefSplahScreen";
	public static final String PREF_EXIT_APP = "prefExitApp";
	
	
	// QuickSettings
public static final String TAG = "bwx.qs";
	
	public static final String PREFS_COMMON = "Common";
	public static final String PREFS_RUNTIME = "Runtime";

	public static final String PREF_STATUSBAR_INTEGRATION = "statusBarIntegration";
	public static final String PREF_APPEARANCE = "viewMode";
	public static final String PREF_INVERSE_VIEW_COLOR = "inverseSatusbarColor";
	public static final String PREF_HAPTIC = "hapticFeedback";
	public static final String PREF_LIGHT_SENSOR = "lightSensor";
	public static final String PREF_FLASHLIGHT = "flashlight";
	public static final String PREF_FLASHLIGHT_TYPE = "flashlightType";
	public static final String PREF_FLASHLIGHT_SWITCH = "flightSwitch";
	public static final String PREF_DISABLE_MMS = "disableMms";
	public static final String PREF_APN_MODIFIER = "apnModifier";
	public static final String PREF_RESTORE_PREFERRED_APN = "restorePreferredApn";
	public static final String PREF_MOBILE_DISABLE_MSG_OK = "disableMobileOk";
	public static final String PREF_PREFERRED_APN_ID = "preferredApn";
	public static final String PREF_NO_CONFIRM_AIRMODE = "noConfirmationAirmode";
	public static final String PREF_EULA_ACCEPTED = "eulaAccepted";
	public static final String PREF_VERSION = "_version"; // old "version" property was an integer, new "_version" is a string
	public static final String PREF_ABOUT = "about";
	
	public static final String ACTION_UPDATE_STATUSBAR_INTEGRATION = "com.bwx.bequick.UPDATE_STATUSBAR_INTEGRATION";
	public static final String ACTION_START_QS = "com.bwx.bequick.START_QS";
	public static final String EXTRA_INT_STATUS = "status";
	public static final String EXTRA_INT_APPEARANCE = "appearence";
	public static final String EXTRA_BOOL_INVERSE_COLOR = "inversed";

	public static final String ACTION_VOLUME_UPDATED = "com.bwx.bequick.VOLUME_UPDATED";

	public static final int STATUS_WHITE_ICON = 3;
	public static final int STATUS_BLACK_ICON = 2;
	public static final int STATUS_NO_ICON = 1;
	public static final int STATUS_NO_INTEGRATION = 0;

	// 1.5 compatible version value
	public static final int SDK_VERSION = Integer.parseInt(Build.VERSION.SDK);
}
