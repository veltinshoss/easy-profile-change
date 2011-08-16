package de.pepping.android.ringtone.handler.bluetooth;

import static de.pepping.android.ringtone.Constants.TAG;

import java.lang.reflect.Method;

import android.app.Activity;
import android.util.Log;

public class BluetoothControl16 implements BluetoothControl {
	
    public static int BLUETOOTH_STATE_UNKNOWN = 0;
    public static int BLUETOOTH_STATE_OFF= 0;
    public static int BLUETOOTH_STATE_TURNING_ON= 0;
    public static int BLUETOOTH_STATE_ON= 0;
    public static int BLUETOOTH_STATE_TURNING_OFF= 0;
    
	public static String BLUETOOTH_ACTION_STATE_CHANGED = "0";
	public static String BLUETOOTH_EXTRA_STATE= "0";
	
	private Object mService;
	private Method[] mMethods = new Method[3]; // [0] enable, [1] disable, [2] getBluetoothState
	
	public BluetoothControl16(Activity mActivity) throws Exception {
		mService = mActivity.getSystemService("bluetooth"); // bluetooth
		Method[] methods = mMethods;
		
		if (mService == null) throw new IllegalStateException("bluetooth service not found");
		Method method;
		
		// get enabled
		method = mService.getClass().getMethod("enable");
		if (method != null) method.setAccessible(true);
		methods[0] = method;
		
		// get disabled
		method = mService.getClass().getMethod("disable");
		if (method != null) method.setAccessible(true);
		methods[1] = method;

		method = mService.getClass().getMethod("getBluetoothState");
		if (method != null) method.setAccessible(true);
		methods[2] = method;
		
		// initialize state
	    BLUETOOTH_STATE_UNKNOWN = -1;
	    BLUETOOTH_STATE_OFF = 0;
	    BLUETOOTH_STATE_TURNING_ON = 1;
	    BLUETOOTH_STATE_ON = 2;
	    BLUETOOTH_STATE_TURNING_OFF = 3;
	    
		BLUETOOTH_ACTION_STATE_CHANGED = "android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED";
		BLUETOOTH_EXTRA_STATE = "android.bluetooth.intent.BLUETOOTH_STATE";
	}
	
	public void setEnabled(boolean enabled) {
		try {
			Method method = mMethods[enabled ? 0 : 1];
			method.invoke(mService);
			return;
		} catch (Exception e) {
			Log.e(TAG, "cannot enable/disable bluetooth", e);
		}
		return;
	}
	
	public int getBluetoothState() {
		try {
			Method method = mMethods[2];
			Integer state = (Integer) method.invoke(mService);
			return state.intValue();
		} catch (Exception e) {
			Log.e(TAG, "cannot getBluetoothState", e);
		}
		return BLUETOOTH_STATE_UNKNOWN;
	}
}	