package de.pepping.android.ringtone.activity;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.TextView;
import de.pepping.android.ringtone.R;

public class RingToneSensorActivity  extends GuiceActivity implements SensorEventListener {

	private AudioManager mAudioManager;
	private SensorManager mSensorManager;
    private Menu mMenu;
	private Vibrator v;
	private boolean isSensorOn = false;
	private int selectedButton = 0;
	
    public RingToneSensorActivity(){
    }
//	private LocationManager lm;
	
	@InjectView(R.id.btn_airplane)
	private Button btnAirplane;
	
	@InjectView(R.id.btn_profile01)
	private Button btnRingAndVibrate;
	
	@InjectView(R.id.btn_ringOnly)
	private Button btnRingOnly;
	
	@InjectView(R.id.btn_vibrateOnly)
	private Button btnVibrateOnly;
	
	@InjectView(R.id.btn_silent)
	private Button btnSilent;
	
	private Dialog timerDialog;
	static final int ID_TIMEPICKER = 1;
	private int myHour, myMinute;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(isSensorOn){
        	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        	v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        	// Location Service
        	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
        
        setContentView(R.layout.main);
        
//        registerForContextMenu(btnRingAndVibrate);
//        initLocationService();
    }


	



	private float x, y, z;
	private int rotation;
	private String[] Sides = {"Right Side", "On Head", "Left Side", "Standing Up", "Right Side", "On Head", "Left Side", "Standing Up"};
	// http://www.marcusnoble.co.uk/2010/09/18/get-an-android-devices-position-in-the-real-world/
	// Z is between 9 and 10 = Device is face down
	// Z is between -9 and -10 = Device is face up
	// X is between 9 and 10 = Device is on its left side
	// X is between -9 and -10 = Device is on its right side
	// Y is between 9 and 10 = Device is standing up
	// Y is between -9 and -10 = Device is standing on its head
	long milliseconds = 100;  
    public void onSensorChanged(SensorEvent event) {
    	TextView result = (TextView) findViewById(R.id.displayText);
   	
    	// in der Hosentasche Einstellungen
    	if(false){
    		synchronized (mSensorManager) {
    			switch (event.sensor.getType()){
    			case Sensor.TYPE_ACCELEROMETER:
    				x = event.values[0];
    				y = event.values[1];
    				z = event.values[2];
    				if( (z>9) ){
    					result.setText("Face Up");
    					v.vibrate(milliseconds);  
    				}else if( (z<-9) ){
    					result.setText("Face Down");
    					v.vibrate(milliseconds);  
    				}else if(x>9){
    					result.setText(Sides[2+rotation]);
//                        v.vibrate(milliseconds);  
    				}else if(x<-9){
    					result.setText(Sides[0+rotation]);
//                        v.vibrate(milliseconds);  
    				}else if( y>9){
    					result.setText(Sides[3+rotation]);
    					v.vibrate(milliseconds);  
    				}else if( y<-5){
    					result.setText(Sides[1+rotation]);
    				}else{
    					result.setText("In Between");
//                        v.vibrate(milliseconds);  
    				}
    				
    			}
    		}   
    		
    	}
    	
    	synchronized (mSensorManager) {
    		switch (event.sensor.getType()){
    		case Sensor.TYPE_ACCELEROMETER:
    			x = event.values[0];
    			y = event.values[1];
    			z = event.values[2];
    			if( (z>9) ){
    				result.setText("Face Up");
    			}else if( (z<-9) ){
    				result.setText("Face Down");
    			}
    			
    		}
    	}   
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }	
    
    @Override
    protected void onStop() {
    	if(isSensorOn){
    		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    	}
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorOn){
        	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        			SensorManager.SENSOR_DELAY_FASTEST);
        	mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        			SensorManager.SENSOR_DELAY_FASTEST);
        	mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        			SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
    
	
	private void exitApp(String endText){
		Intent intent = new Intent("de.pepping.android.ringtone.intent.action.CLOSE_ACTIVITY");
		intent.putExtra(CloseActivity.KEY_TEXT_TO_SHOW, endText);
		startActivity(intent);
		finish();
	}
	
	private void initLocationService() {
		final LocationListener locationListener = new LocationListener() {
            
        	public void onLocationChanged(Location l) {
//               textView.setText("lat: " + l.getLatitude() + "\nlon: " + l.getLongitude());
            }

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
        };
            
//        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        
//        lm.requestLocationUpdates("gps",
//        		60000, // 1min
//        		1,   // 10m
//        		locationListener);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;
        
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_icon , menu);
        
        return true;
    }

}