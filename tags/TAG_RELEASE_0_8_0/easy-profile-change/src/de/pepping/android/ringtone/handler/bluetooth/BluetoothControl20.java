package de.pepping.android.ringtone.handler.bluetooth;

import android.bluetooth.BluetoothAdapter;

public class BluetoothControl20 implements BluetoothControl {

	// BT state abstraction
    public static int BLUETOOTH_STATE_UNKNOWN = 0;
    public static int BLUETOOTH_STATE_OFF= 0;
    public static int BLUETOOTH_STATE_TURNING_ON= 0;
    public static int BLUETOOTH_STATE_ON= 0;
    public static int BLUETOOTH_STATE_TURNING_OFF= 0;
    
	public static String BLUETOOTH_ACTION_STATE_CHANGED = "0";
	public static String BLUETOOTH_EXTRA_STATE= "0";
	
	
	private BluetoothAdapter mAdapter;
	
	public BluetoothControl20() {
		// cache adaptor
		mAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mAdapter == null) throw new UnsupportedOperationException("No default bluetooth adapter");
		
		// initialize state
	    BLUETOOTH_STATE_UNKNOWN = -1;
	    BLUETOOTH_STATE_OFF = BluetoothAdapter.STATE_OFF;
	    BLUETOOTH_STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
	    BLUETOOTH_STATE_ON = BluetoothAdapter.STATE_ON;
	    BLUETOOTH_STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
	    
	    BLUETOOTH_ACTION_STATE_CHANGED = BluetoothAdapter.ACTION_STATE_CHANGED;
	    BLUETOOTH_EXTRA_STATE = BluetoothAdapter.EXTRA_STATE;
	}
	
	public int getBluetoothState() {
		return mAdapter.getState();
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			mAdapter.enable();
		} else {
			mAdapter.disable();
		}
	}
}