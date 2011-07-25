package de.pepping.android.ringtone;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import static de.pepping.android.ringtone.Constants.ACTION_UPDATE_STATUSBAR_INTEGRATION;
import static de.pepping.android.ringtone.Constants.EXTRA_BOOL_INVERSE_COLOR;
import static de.pepping.android.ringtone.Constants.EXTRA_INT_APPEARANCE;
import static de.pepping.android.ringtone.Constants.EXTRA_INT_STATUS;
import static de.pepping.android.ringtone.Constants.PREFS_COMMON;
import static de.pepping.android.ringtone.Constants.PREF_APPEARANCE;
import static de.pepping.android.ringtone.Constants.PREF_INVERSE_VIEW_COLOR;
import static de.pepping.android.ringtone.Constants.PREF_STATUSBAR_INTEGRATION;
import static de.pepping.android.ringtone.Constants.PREF_VERSION;

import roboguice.application.GuiceApplication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.inject.Module;

import de.pepping.android.ringtone.fwk.Setting;
import de.pepping.android.ringtone.fwk.SettingsFactory;
import de.pepping.android.ringtone.preferences.BrightnessPrefs;
import de.pepping.android.ringtone.preferences.CommonPrefs;

public class GuiceNumberGeneratorApplication extends GuiceApplication {

	
	// Start Quic
	private static final int[] IDS = new int[] {
		
		/* visible */
		Setting.GROUP_VISIBLE,
		Setting.BRIGHTNESS,
//		Setting.RINGER,
//		Setting.VOLUME,
//		Setting.BLUETOOTH,
//		Setting.WIFI,
//		Setting.GPS,
//		Setting.MOBILE_DATA,
//		Setting.FOUR_G,
//		
//		/* hidden */
		Setting.GROUP_HIDDEN,
//		Setting.MASTER_VOLUME,
//		Setting.SCREEN_TIMEOUT,
//		Setting.WIFI_HOTSPOT,
//		Setting.AIRPLANE_MODE,
//		Setting.AUTO_SYNC,
//		Setting.AUTO_ROTATE,
//		Setting.LOCK_PATTERN,
//		Setting.MOBILE_DATA_APN
};
	
	// state
	private ArrayList<Setting> mSettings;
	private SharedPreferences mPrefs;
	
	
	   public void onCreate() {
	    	
	    	super.onCreate();
	    	String defaultText = "Das ist der default Text";

	    	// load settings
	    	SharedPreferences prefs = mPrefs = getSharedPreferences(PREFS_COMMON, MODE_WORLD_WRITEABLE);
	    	
	    	// create settings list
	    	ArrayList<Setting> settings = mSettings = new ArrayList<Setting>();
	    	int[] ids = IDS;
	    	int length = ids.length;
	    	Setting setting;
	    	for (int i=0; i<length; i++) {
	    		int id = ids[i];
	    		int index = prefs.getInt(String.valueOf(id), length); // move to end
	    		setting = SettingsFactory.createSetting(id, index, defaultText, this);
	    		if (setting != null) settings.add(setting);
	    	}
	    	
	    	// sort list
	    	Collections.sort(settings, new Comparator<Setting>() {
				public int compare(Setting object1, Setting object2) {
					return object1.index - object2.index;
				}
			});
	    	
	    	// update status bar integration
	    	final int appearance = Integer.parseInt(prefs.getString(PREF_APPEARANCE, "0"));
			final int status = Integer.parseInt(prefs.getString(PREF_STATUSBAR_INTEGRATION, "0"));
			final boolean inverse = prefs.getBoolean(PREF_INVERSE_VIEW_COLOR, false);
			Intent intent = new Intent(ACTION_UPDATE_STATUSBAR_INTEGRATION);
			intent.putExtra(EXTRA_INT_STATUS, status);
			intent.putExtra(EXTRA_INT_APPEARANCE, appearance);
			intent.putExtra(EXTRA_BOOL_INVERSE_COLOR, inverse);
			sendBroadcast(intent);

			String version = prefs.getString(PREF_VERSION, null);
			if (version == null) {
				// update PREF_LIGHT_SENSOR on first start
				boolean hasLightSensor = BrightnessPrefs.hasLightSensor(this);
				String currentVersion = CommonPrefs.getVersionNumber(this);
				prefs.edit().putBoolean(Constants.PREF_LIGHT_SENSOR, hasLightSensor).putString(PREF_VERSION, currentVersion).commit();
			}
			
	    }
		
		public void persistSettings() {
	    	Editor editor = mPrefs.edit();
	    	ArrayList<Setting> settings = mSettings;
	    	int length = settings.size();
	    	for (int i=0; i<length; i++) {
	    		Setting setting = settings.get(i);
	    		editor.putInt(String.valueOf(setting.id), setting.index);
	    	}
	    	editor.commit();
	    }
	    
	    public SharedPreferences getPreferences() {
	    	return mPrefs;
	    }
	    
	    public ArrayList<Setting> getSettings() {
	    	return mSettings;
	    }
	    
	    public Setting getSetting(int id) {
	    	ArrayList<Setting> settings = mSettings;
	    	int length = settings.size();
	    	for (int i=0; i<length; i++) {
	    		Setting setting = settings.get(i);
	    		if (id == setting.id) return setting;
	    	}
	    	return null;
	    }
	
	// End Quick-Settings
	
	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new GuiceModule());
	}

	// Droid-Fu Extension
	private HashMap<String, WeakReference<Context>> contextObjects = new HashMap<String, WeakReference<Context>>();
    
	public synchronized Context getActiveContext(String className) {
        WeakReference<Context> ref = contextObjects.get(className);
        if (ref == null) {
            return null;
        }

        final Context c = ref.get();
        if (c == null) // If the WeakReference is no longer valid, ensure it is removed.
            contextObjects.remove(className);
        return c;
    }
	
	public synchronized void setActiveContext(String className, Context context) {
        WeakReference<Context> ref = new WeakReference<Context>(context);
        this.contextObjects.put(className, ref);
    }

    public synchronized void resetActiveContext(String className) {
        contextObjects.remove(className);
    }
    
    /**
     * <p>
     * Invoked if the application is about to close. Application close is being defined as the
     * transition of the last running Activity of the current application to the Android home screen
     * using the BACK button. You can leverage this method to perform cleanup logic such as freeing
     * resources whenever your user "exits" your app using the back button.
     * </p>
     * <p>
     * Note that you must not rely on this callback as a general purpose "exit" handler, since
     * Android does not give any guarantees as to when exactly the process hosting an application is
     * being terminated. In other words, your application can be terminated at any point in time, in
     * which case this method will NOT be invoked.
     * </p>
     */
    public void onClose() {
        // NO-OP by default
    }
}
