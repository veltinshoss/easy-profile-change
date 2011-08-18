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

import java.util.HashMap;

import de.pepping.android.ringtone.activity.MainSettingsActivity;
import de.pepping.android.ringtone.fwk.Setting;
import de.pepping.android.ringtone.fwk.SettingHandler;


public class VolumeControlSettingHandler extends SettingHandler {

	private VolumeDialog mDialog;

	public VolumeControlSettingHandler(Setting setting) {
		super(setting);
	}

	@Override
	public void activate(MainSettingsActivity activity) throws Exception {
		mActivity = activity;
		if(mSetting.valueList==null){
			mSetting.valueList = new HashMap<String, Integer>();
		}
		mSetting.valueList.put("5", mActivity.mProfileSetting.ringer_stream_system);
		mSetting.valueList.put("4", mActivity.mProfileSetting.ringer_stream_voice_call);
		mSetting.valueList.put("3", mActivity.mProfileSetting.ringer_stream_alarm);
		mSetting.valueList.put("2", mActivity.mProfileSetting.ringer_stream_music);
		mSetting.valueList.put("1", mActivity.mProfileSetting.ringer_stream_notification);
		mSetting.valueList.put("0", mActivity.mProfileSetting.ringer_stream_ring);
		mDialog = new VolumeDialog(activity, mSetting);
	}

	@Override
	public void deactivate() {
		mActivity.mProfileSetting.ringer_stream_system = mSetting.valueList.get("5");
		mActivity.mProfileSetting.ringer_stream_voice_call = mSetting.valueList.get("4");
		mActivity.mProfileSetting.ringer_stream_alarm = mSetting.valueList.get("3");
		mActivity.mProfileSetting.ringer_stream_music = mSetting.valueList.get("2");
		mActivity.mProfileSetting.ringer_stream_notification = mSetting.valueList.get("1");
		mActivity.mProfileSetting.ringer_stream_ring = mSetting.valueList.get("0");
		mDialog.dismiss();
		mDialog = null;
	}

	@Override
	public void onSelected(int buttonIndex) {
		VolumeDialog dialog = mDialog;
		if (dialog != null) dialog.show();
	}

	@Override
	public void onSwitched(boolean switched) {
		// do nothing
	}

	@Override
	public void onValueChanged(int value) {
		// do nothing
	}
}
