package de.pepping.android.ringtone.activity;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.pepping.android.ringtone.Constants;
import de.pepping.android.ringtone.R;
import de.pepping.android.ringtone.database.DataHelper;
import de.pepping.android.ringtone.dialog.InstructionDialog;
import de.pepping.android.ringtone.fwk.ProfileSettings;
import de.pepping.android.ringtone.handler.UpdateTimer;
import de.pepping.android.ringtone.service.TimerService;
import de.pepping.android.ringtone.util.RingtoneUtil;

public class RingToneActivity extends GuiceActivity implements Constants{

	private AudioManager mAudioManager;

	private Menu mMenu;
	
    public RingToneActivity(){
    }
    
	@InjectView(R.id.btn_airplane)
	private Button btnAirplane;
	
	@InjectView(R.id.btn_ringAndVibrate)
	private Button btnRingAndVibrate;
	
	@InjectView(R.id.btn_ringOnly)
	private Button btnRingOnly;
	
	@InjectView(R.id.btn_vibrateOnly)
	private Button btnVibrateOnly;
	
	@InjectView(R.id.btn_silent)
	private Button btnSilent;
	
	@InjectView(R.id.btn_stopTimer)
	private ImageButton btnStopTimer;
	
	@InjectView(R.id.displayTimer)
	private TextView txtDisplayTimer;
	
	private Dialog timerDialog;
	static final int ID_TIMEPICKER = 1;
	
	private String standardProfile ="";
	private String selectedProfileForTimer="";
	private Calendar startDate =null;
	private Calendar endDate = null;
	
	private UpdateTimer mTimer; 
	
	private DataHelper dh;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        setContentView(R.layout.main);
        
        initButtonBackground();
        
        // Anpassungen fuer die Oberflaeche
        adjustDisplayComponents();
        
        // Anpassungen durch den Timer
        displayTimerChanges();
        
        // Button longClick initilisieren
        initButtonListeners();
        
        // init DB
        this.dh = new DataHelper(this);
        
    }
	
	
	private void initButtonListeners() {
		btnRingAndVibrate.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// AUFRUF TIMER
//				selectedProfileForTimer = RING_AND_VIBRATE;
//				showDialog(ID_TIMEPICKER);
//				// true if the event is been consumed
//				return true;
				// AUFRUF TIMER - ENDE
				
				
				// NEU
				ProfileSettings ps = new ProfileSettings();
				
//				dh.deleteAll();
//				dh.insert(2,"Ein Name");
//				List<ProfileSettings> selectAll = dh.selectAll();
				
				ProfileSettings findProfileSettingById = dh.findProfileSettingById(1);
				if(findProfileSettingById==null || findProfileSettingById.id==0){
					dh.insert(1, "myName", 10);
				}
				
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), "de.pepping.android.ringtone.activity.MainSettingsActivity");
				intent.putExtra("PROFILE_ID", 1);
				// launch real activity depending on the configuration
				try {
					// start activity
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Log.e(TAG, "cannot dispatch launch request", e);
					// this could only happen if installation went wrong and
					// Manifest.xml was not applied
					AlertDialog.Builder builder = new AlertDialog.Builder(RingToneActivity.this);
					builder.setMessage(R.string.msg_reinstall_required).setNeutralButton(R.string.btn_close,
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finish(); // finish this activity
								}
							}).create().show();
				}
				return true;
			}
		});
		btnRingOnly.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				selectedProfileForTimer = RING_ONLY;
				showDialog(ID_TIMEPICKER);
				// true if the event is been consumed
				return true;
			}
		});
		btnVibrateOnly.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				selectedProfileForTimer = VIBRATE_ONLY;
				showDialog(ID_TIMEPICKER);
				// true if the event is been consumed
				return true;
			}
		});
		btnSilent.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				selectedProfileForTimer = SILENT;
				showDialog(ID_TIMEPICKER);
				// true if the event is been consumed
				return true;
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
			case ID_TIMEPICKER:
					timerDialog = new Dialog(this);
					timerDialog.setContentView(R.layout.timer_dialog);
					timerDialog.setTitle(R.string.textDialogTimerTitleText);
					
					Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			        int height = display.getHeight(); 
			        if(height <500){
			        	RadioGroup radioGroup =  (RadioGroup) timerDialog.findViewById(R.id.dialogTimerRadiogroup);
			        	int childCount = radioGroup.getChildCount();
			        	for(int i=0 ;i<childCount;i++){
			        		RadioButton row  = (RadioButton) radioGroup.getChildAt(i);
			            	row.setPadding(50, 0, 0, 0);
			            }
			        }
					
					Button buttonOK = (Button) timerDialog.findViewById(R.id.buttonOK);
					buttonOK.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							EditText mInputText = (EditText) timerDialog.findViewById(R.id.dialogTimerInputTime);
							String minutesStr = mInputText.getText().toString();
							Integer minutes = null;
							if(minutesStr!=null && minutesStr.length()>0){
								try {
								   minutes = Integer.parseInt(minutesStr);
								} catch(NumberFormatException nfe) {
								   
								} 
							}
							if(minutes!=null){
								Calendar calendar = Calendar.getInstance();
								calendar.add(Calendar.MINUTE, minutes);
								executeTimerDialogChoice(calendar,minutesStr);	
							}else{
								
							}
						}
					});
					
					RadioGroup mRadioGroup= (RadioGroup) timerDialog.findViewById(R.id.dialogTimerRadiogroup);
					mRadioGroup.clearCheck();
					mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(System.currentTimeMillis());
							
							String minutes = "";
							if(checkedId==R.id.dialogTimer1minute){
								calendar.add(Calendar.SECOND, 60);
								minutes = "1";
							}else if(checkedId==R.id.dialogTimer10minute){
								calendar.add(Calendar.SECOND, (60*10));
								minutes = "10";
							}else if(checkedId==R.id.dialogTimer30minute){
								calendar.add(Calendar.SECOND, (60*30));
								minutes = "30";
							}else if(checkedId==R.id.dialogTimer60minute){
								calendar.add(Calendar.SECOND, (60*60));
								minutes = "60";
							}else if(checkedId==R.id.dialogTimer120minute){
								calendar.add(Calendar.SECOND, (60*120));
								minutes = "120";
							}else{
								calendar.add(Calendar.SECOND, 10);
								minutes = "15";								
							}
							
							if(calendar!=null && minutes!=null){
								executeTimerDialogChoice(calendar,minutes);	
							}
						}
					});
                return timerDialog;
			default:
				return null;
		}
	}
	
	private void executeTimerDialogChoice(Calendar endDateNew, String minutes){
		standardProfile = RingtoneUtil.getRingToneAsString(mAudioManager);
		Intent intent = new Intent(RingToneActivity.this, TimerService.class);
		intent.putExtra(INTENT_PARAM_PROFILE, standardProfile);
		PendingIntent mSender = PendingIntent.getBroadcast(RingToneActivity.this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		startDate = Calendar.getInstance();
		startDate.setTimeInMillis(System.currentTimeMillis());
		
		endDate = endDateNew;
		
		String text = String.format(getString(R.string.textTimerEndApp), RingtoneUtil.convertRingToneProfileToReadable(selectedProfileForTimer, RingToneActivity.this) , minutes);
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, endDateNew.getTimeInMillis(), mSender);
		timerDialog.cancel();
		RingtoneUtil.setRingToneWithString(selectedProfileForTimer, mAudioManager);
		exitApp(text);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(R.id.btn_ringAndVibrate == v.getId() ){
			getMenuInflater().inflate(R.menu.button_action_menu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.btn_act_on_head:{
			Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
			return true;
		}
		}
		return super.onContextItemSelected(item);
		
	}

	private void initButtonBackground() {
        
        if(RingtoneUtil.isRingAndVibrate(mAudioManager)){
        	btnRingAndVibrate.setBackgroundResource(R.drawable.btn_green);
        }else{
        	btnRingAndVibrate.setBackgroundResource(R.drawable.btn_blackwert);
        }
        if(RingtoneUtil.isRingOnly(mAudioManager)){
        	btnRingOnly.setBackgroundResource(R.drawable.btn_green);
        }else{
        	btnRingOnly.setBackgroundResource(R.drawable.btn_blackwert);
        }
        if(RingtoneUtil.isVibrateOnly(mAudioManager)){
        	btnVibrateOnly.setBackgroundResource(R.drawable.btn_green);
        }else{
        	btnVibrateOnly.setBackgroundResource(R.drawable.btn_blackwert);
        }
        if(RingtoneUtil.isSilent(mAudioManager)){
        	btnSilent.setBackgroundResource(R.drawable.btn_green);
        }else{
        	btnSilent.setBackgroundResource(R.drawable.btn_blackwert);
        }
		if (RingtoneUtil.isAirPlaneModeEnabled(getContentResolver())) {
			btnAirplane.setText(getString(R.string.textAirplaneOn));
			btnAirplane.setBackgroundResource(R.drawable.btn_blue);
		}else{
			btnAirplane.setText(getString(R.string.textAirplaneOff));
			btnAirplane.setBackgroundResource(R.drawable.btn_blackwert);
		}
        
	}

	private void adjustDisplayComponents() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight(); 
        LinearLayout tableLayout =  (LinearLayout) findViewById(R.id.button_layout);
        int countRows = tableLayout.getChildCount();
        int cellHeight = (height+70)/8;
        // Cell hoehe anpassen
        for(int i=0 ;i<countRows;i++){
        	Button row  = (Button) tableLayout.getChildAt(i);
        	row.setHeight(cellHeight);
        }
        if(height <500){
        	// Ueberschrift anpassen
        	TextView view  = (TextView) findViewById(R.id.displayText);
        	view.setTextSize(19);
        	view.setPadding(0, 0, 0, 0);
        	txtDisplayTimer.setTextSize(19);
        	txtDisplayTimer.setPadding(0, 0, 0, 0);
        	
        	// Buttons anpassen
        	for(int i=0 ;i<countRows;i++){
        		Button row  = (Button) tableLayout.getChildAt(i);
        		row.setTextSize(20);
        		row.setHeight(60);
        	}
        	
        	// Layouts-Rand anpassen
        	LinearLayout buttonLayout  = (LinearLayout) findViewById(R.id.button_layout);
        	buttonLayout.setPadding(buttonLayout.getPaddingLeft(), 0, buttonLayout.getPaddingRight(), buttonLayout.getPaddingBottom());
        	LinearLayout topButtonLayout  = (LinearLayout) findViewById(R.id.topButtonsLayout);
        	topButtonLayout.setPadding(buttonLayout.getPaddingLeft(), 0, buttonLayout.getPaddingRight(), 0);
        	
        	
        }
	}

	public void onStopTimer(final View button){
		if(endDate!=null){
			RingtoneUtil.setRingToneWithString(standardProfile, mAudioManager);
			stopTimerAndUpdateUi();
		}else{
			Dialog dialog = null;
			InstructionDialog.Builder customBuilder = new InstructionDialog.Builder(RingToneActivity.this);
			customBuilder.setTitle(getString(R.string.textDialogInstructionTitleText))
            	.setMessage(R.string.textDialogInstructionMainText)
//            	.setNegativeButton("Cancel", 
//	                    new DialogInterface.OnClickListener() {
//			                public void onClick(DialogInterface dialog, int which) {
//			                    
//			                }
//            			})
            	.setPositiveButton("Ok", 
                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                    dialog.dismiss();
		                }
		             })
            			;
			dialog = customBuilder.create();
        
            //now that the dialog is set up, it's time to show it    
            dialog.show();
		}
	}
	
	private void stopTimerAndUpdateUi(){
		Intent intent = new Intent(RingToneActivity.this, TimerService.class);
		PendingIntent sender = PendingIntent.getBroadcast(RingToneActivity.this,0, intent,0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.cancel(sender);
		startDate = null;
		endDate = null;
		selectedProfileForTimer = "";
		btnStopTimer.setImageResource(R.drawable.timer_disabled);
		if(mTimer!=null){
			mTimer.sendEmptyMessage(UpdateTimer.MSG_STOP);
		}
		txtDisplayTimer.setText("");
		initButtonBackground();
	}
	
	public void onStartPreferences(final View button){
		Intent intent = new Intent(RingToneActivity.this, PreferencesActivity.class);
		startActivity(intent);
	}
	
	public void onRingAndVibrate(final View button){
		if(endDate!=null){
			stopTimerAndUpdateUi();
		}
		RingtoneUtil.setRingAndVibrate(mAudioManager);
		exitApp(getString(R.string.textRingAndVibrateToast));
	}
	
	public void onRingOnly(final View button){
		if(endDate!=null){
			stopTimerAndUpdateUi();
		}
		RingtoneUtil.setRingOnly(mAudioManager);
		exitApp(getString(R.string.textRingOnlyToast));
	}
	
	public void onVibrateOnly(final View button){
		if(endDate!=null){
			stopTimerAndUpdateUi();
		}
		RingtoneUtil.setVibrateOnly(mAudioManager);
		exitApp(getString(R.string.textVibrateOnlyToast));
	}
	
	public void onSilent(final View button){
		if(endDate!=null){
			stopTimerAndUpdateUi();
		}
		RingtoneUtil.setSilent(mAudioManager);
		exitApp(getString(R.string.textSilentToast));
	}
	
	public void onAirplane(final View button){
		boolean isEnabled = RingtoneUtil.isAirPlaneModeEnabled(getContentResolver());
		Settings.System.putInt(getContentResolver(),Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", !isEnabled);
		sendBroadcast(intent);
		exitApp(isEnabled?getString(R.string.textAirplaneOnToast):getString(R.string.textAirplaneOffToast));
	}

    @Override
    protected void onStop() {
        super.onStop();
//        finish();
    }

    @Override
    protected void onPause() {
    	final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
    	if(startDate!=null){
    		editor.putLong("startdate", startDate.getTimeInMillis());
    	}else{
    		editor.remove("startdate");
    	}
    	if(endDate!=null){
    		editor.putLong("enddate", endDate.getTimeInMillis());
    	}else{
    		editor.remove("enddate");
    	}
    	editor.putString("selectedProfileForTimer", selectedProfileForTimer);
    	editor.putString("standardProfile", standardProfile);
    	editor.commit();
    	
    	super.onPause();
    	if(mTimer!=null){
    		mTimer.sendEmptyMessage(UpdateTimer.MSG_STOP);
    	}
    }
    
    @Override
    protected void onResume() {
    	final SharedPreferences pref = getPreferences(MODE_PRIVATE);
    	Long startDateAsLong = pref.getLong("startdate", -1);
    	Long endDateAsLong = pref.getLong("enddate", -1);
    	selectedProfileForTimer = pref.getString("selectedProfileForTimer", "");
    	standardProfile = pref.getString("standardProfile", "");
    	
    	if(startDateAsLong!=null && startDateAsLong!=-1){
    		startDate = Calendar.getInstance();
    		startDate.setTimeInMillis(startDateAsLong);
    	}
    	if(endDateAsLong!=null && endDateAsLong!=-1){
    		endDate = Calendar.getInstance();
    		endDate.setTimeInMillis(endDateAsLong);
    	}

    	displayTimerChanges();
   	
        super.onResume();
        
        if(endDate!=null){
        	mTimer = new UpdateTimer(RingToneActivity.this);
        	mTimer.sendEmptyMessage(UpdateTimer.MSG_START);
        }
    }
    
    private void displayTimerChanges(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
    	initButtonBackground();
    	if(endDate!=null && endDate.after(calendar)){
    		if(standardProfile!=null){
    			if(standardProfile.equals(Constants.RING_AND_VIBRATE)){
    				btnRingAndVibrate.setBackgroundResource(R.drawable.btn_grey);
    			}else if(standardProfile.equals(Constants.RING_ONLY)){
    				btnRingOnly.setBackgroundResource(R.drawable.btn_grey);
    			}else if(standardProfile.equals(Constants.VIBRATE_ONLY)){
    				btnVibrateOnly.setBackgroundResource(R.drawable.btn_grey);
				} else if (standardProfile.equals(Constants.SILENT)) {
					btnSilent.setBackgroundResource(R.drawable.btn_grey);
				}
    		}
    		btnStopTimer.setImageResource(R.drawable.timer_enabled);
    		btnStopTimer.setClickable(true);
    	}else{
    		btnStopTimer.setImageResource(R.drawable.timer_disabled);
    	}
    }
	
	private void exitApp(String endText){
		
    	boolean isSplashScreen  = getCustomPreferences().getBoolean(PREF_SPLASH_SCREEN, false);
    	boolean isExitApp  = getCustomPreferences().getBoolean(PREF_EXIT_APP, true);
    	if(isSplashScreen){
    		Intent intent = new Intent("de.pepping.android.ringtone.intent.action.CLOSE_ACTIVITY");
    		intent.putExtra(CloseActivity.KEY_TEXT_TO_SHOW, endText);
    		startActivity(intent);
    	}
    	if(isExitApp){
    		finish();
    	}else{
    		initButtonBackground();
    		if(endDate!=null){
    			mTimer = new UpdateTimer(RingToneActivity.this);
    			mTimer.sendEmptyMessage(UpdateTimer.MSG_START);
    		}
    		displayTimerChanges();
    	}
    	
	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_icon , menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()){
    	case R.id.menu_preferences:
    		Intent intent = new Intent(RingToneActivity.this, PreferencesActivity.class);
    		startActivity(intent);
    	}
    	return super.onOptionsItemSelected(item);
    }

    public void updateTimer(){
    	SimpleDateFormat f =  new java.text.SimpleDateFormat("HH:mm:ss");
    	f.setTimeZone(TimeZone.getTimeZone("GMT"));
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
    	if(endDate!=null && endDate.after(calendar)){
    		long difference = endDate.getTimeInMillis() - calendar.getTimeInMillis();
//    	    int days = (int) (difference / (1000 * 60 * 60 * 24));
//    	    int millis = (int) (difference % 1000);
    	    int hours = (int) (difference / (1000 * 60 * 60) % 24);
    	    int minutes = (int) (difference / (1000 * 60) % 60);
    	    int seconds = (int) (difference / 1000 % 60);
    	    txtDisplayTimer.setText("" + String.format("%02d",hours) + ":" +  String.format("%02d",minutes) + ":" +  String.format("%02d",seconds));
    	    
    		
//			txtDisplayTimer.setText("" + hour + ":"+f.format(tmpDate));
    	}else{
    		mTimer.sendEmptyMessage(UpdateTimer.MSG_STOP);
    		txtDisplayTimer.setText("");
    		displayTimerChanges();
    	}
    }
    
    public void onExit(final View button){
        finish();
    }
    
    private SharedPreferences getCustomPreferences(){
    	return getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }
}