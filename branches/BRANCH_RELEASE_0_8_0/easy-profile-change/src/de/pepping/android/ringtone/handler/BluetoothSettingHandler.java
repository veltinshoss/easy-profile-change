/*
 * Copyright (C) 2010 beworx.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.pepping.android.ringtone.handler;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import de.pepping.android.ringtone.Constants;
import de.pepping.android.ringtone.R;
import de.pepping.android.ringtone.activity.MainSettingsActivity;
import de.pepping.android.ringtone.fwk.Setting;
import de.pepping.android.ringtone.fwk.SettingHandler;
import de.pepping.android.ringtone.handler.bluetooth.BluetoothControl;



public class BluetoothSettingHandler extends SettingHandler implements Constants{

    public static int BLUETOOTH_STATE_UNKNOWN = 0;
    public static int BLUETOOTH_STATE_OFF= 0;
    public static int BLUETOOTH_STATE_TURNING_ON= 0;
    public static int BLUETOOTH_STATE_ON= 0;
    public static int BLUETOOTH_STATE_TURNING_OFF= 0;
    
	public static String BLUETOOTH_ACTION_STATE_CHANGED = "0";
	public static String BLUETOOTH_EXTRA_STATE= "0";
	

	
    class BluetoothStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(BLUETOOTH_EXTRA_STATE, BLUETOOTH_STATE_UNKNOWN);
			onBluetoothStateChanged(state);
		}
	}
	
    public class BluetoothControl20 implements BluetoothControl {

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
			if(mSetting.directSettingActivation){
				if (enabled) {
					mAdapter.enable();
				} else {
					mAdapter.disable();
				}
			}else{
				onBluetoothStateChanged(enabled?BLUETOOTH_STATE_ON:BLUETOOTH_STATE_OFF);
			}
			mSetting.value = enabled?1:0;
		}
    }
    
    
	public class BluetoothControl16 implements BluetoothControl {
		
		private Object mService;
		private Method[] mMethods = new Method[3]; // [0] enable, [1] disable, [2] getBluetoothState
		
		public BluetoothControl16() throws Exception {
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
			if(mSetting.directSettingActivation){
				try {
					Method method = mMethods[enabled ? 0 : 1];
					method.invoke(mService);
					return;
				} catch (Exception e) {
					Log.e(TAG, "cannot enable/disable bluetooth", e);
				}
			}else{
				onBluetoothStateChanged(enabled?BLUETOOTH_STATE_ON:BLUETOOTH_STATE_OFF);
			}
			mSetting.value = enabled?1:0;
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

	private BluetoothControl mBluetoothControl;
	private BluetoothStateReceiver mReceiver;
	
	public BluetoothSettingHandler(Setting setting) {
		super(setting);
	}
	
	@Override
	public void activate(MainSettingsActivity activity) throws Exception {
		mActivity = activity;

		// create bluetooth control
		try {
			// try with 1.6
			mBluetoothControl = new BluetoothControl16();
		} catch(Exception e) {
			try {
				mBluetoothControl = new BluetoothControl20(); // 2.0+ adapter
			} catch(Exception e1) {
				throw new Exception("No Bluetooth-Adapter");
			}
		}
		
		mSetting.value = activity.mProfileSetting.bluetooth;
		
		if(mSetting.directSettingActivation){
			mBluetoothControl.setEnabled(mSetting.value==1);
			// get state pro-actively, as we won't be notified immediately
			onBluetoothStateChanged(mBluetoothControl.getBluetoothState());
			
			// register bluetooth event receiver
			IntentFilter filter = new IntentFilter(BLUETOOTH_ACTION_STATE_CHANGED);
			if (mReceiver == null) mReceiver = new BluetoothStateReceiver();
			activity.registerReceiver(mReceiver, filter);
		}else{
			onBluetoothStateChanged(mSetting.value==1?BLUETOOTH_STATE_ON:BLUETOOTH_STATE_OFF);
		}
		
	}

	@Override
	public void deactivate() {
		mActivity.mProfileSetting.bluetooth = mSetting.value;
		mActivity.unregisterReceiver(mReceiver);
	}

	@Override
	public void onSelected(int buttonIndex) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
		mActivity.startActivitiesSafely(intent, new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}

	@Override
	public void onSwitched(boolean isSwitched) {
		mSetting.value = isSwitched?1:0;
		mBluetoothControl.setEnabled(isSwitched);
	}

	@Override
	public void onValueChanged(int value) {
		// do nothing
	}
	
	/**
	 * Handles state and updates description, check- and enable-states
	 * @param state
	 */
	private void onBluetoothStateChanged(int state) {

		Setting setting = mSetting;
		int stateText = R.string.txt_status_unknown;
		boolean enabled = false;
		
		if (state == BLUETOOTH_STATE_OFF) {
			setting.checked = false;
			enabled = true;
			stateText = R.string.txt_status_turned_off; 
		} else if (state == BLUETOOTH_STATE_TURNING_OFF) {
			stateText = R.string.txt_status_turning_off;
		} else if (state == BLUETOOTH_STATE_ON) {
			setting.checked = true;
			enabled = true;
			stateText = R.string.txt_status_turned_on; 
		} else if (state == BLUETOOTH_STATE_TURNING_ON) {
			stateText = R.string.txt_status_turning_on;
		}
		
		setting.enabled = enabled;
		setting.descr = mActivity.getString(stateText);
		setting.updateView();
	}
	
}