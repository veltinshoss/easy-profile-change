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
import static de.pepping.android.ringtone.Constants.TAG;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import de.pepping.android.ringtone.R;
import de.pepping.android.ringtone.activity.MainSettingsActivity;
import de.pepping.android.ringtone.fwk.Setting;
import de.pepping.android.ringtone.fwk.SettingHandler;

public class SystemPropertySettingHandler extends SettingHandler {

	private final String mPropertyName;
	private final String mPropertyPage;
	
	public SystemPropertySettingHandler(Setting setting, String propertyName, String propertyPage) {
		super(setting);
		mPropertyName = propertyName;
		mPropertyPage = propertyPage;
	}

	@Override
	public void activate(MainSettingsActivity activity) throws SettingNotFoundException {
		mActivity = activity;
		mSetting.value = mActivity.mProfileSetting.auto_rotation; 
//		boolean enabled = Settings.System.getInt(activity.getContentResolver(), mPropertyName) == 1;
		boolean enabled = mSetting.value == 1;
		
		updateSetting(true, enabled, 0);
	}

	@Override
	public void deactivate() {
		mActivity.mProfileSetting.auto_rotation = mSetting.value; 
	}

	@Override
	public void onSelected(int buttonIndex) {
		try {
			Intent intent = new Intent(mPropertyPage);
			mActivity.startActivity(intent);
		} catch(Exception e) {
			Log.e(TAG, "", e);
		}
	}

	@Override
	public void onSwitched(boolean isSwitched) {
		// update setting
		if(mSetting.directSettingActivation){
			Settings.System.putInt(mActivity.getContentResolver(), mPropertyName, isSwitched ? 1 : 0);
		}
		mSetting.value = isSwitched ? 1 : 0;
		updateSetting(true, isSwitched, 0);
	}

	@Override
	public void onValueChanged(int value) {
		// do nothing
	}

	/**
	 * @param enabled	true / false
	 * @param switched	true / false
	 * @param descrId	resourceId / 0
	 */
	protected void updateSetting(boolean enabled, boolean switched, int descrId) {
		Setting setting = mSetting;
		setting.enabled = enabled;
		setting.checked = switched;
		int resId = descrId == 0 
			? (switched ? R.string.txt_status_enabled : R.string.txt_status_disabled)
			: descrId;
		setting.descr = mActivity.getString(resId);
		setting.updateView();
	}

}