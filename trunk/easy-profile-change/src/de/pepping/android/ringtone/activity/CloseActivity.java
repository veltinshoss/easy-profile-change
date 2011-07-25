package de.pepping.android.ringtone.activity;

import roboguice.activity.GuiceActivity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import de.pepping.android.ringtone.R;

public class CloseActivity  extends GuiceActivity  {

	public static final String KEY_TEXT_TO_SHOW = "KEY_TEXT_TO_SHOW";	
	static final int DIALOG_SHOW_ID = 0;
	private static String textToShow = "";
	private TextView showText;
	private long timeToSleep = 1500;
	
    public CloseActivity(){
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras(); 
        if(extras!=null && extras.containsKey(KEY_TEXT_TO_SHOW)){
        	textToShow = extras.getString(KEY_TEXT_TO_SHOW);
        }
        
        setContentView(R.layout.close_activity);
        
        final Dialog dialog = new Dialog(CloseActivity.this);
        dialog.setContentView(R.layout.close_activity_dialog);

        dialog.setTitle(getString(R.string.textCloseActivityDialogTitle));
        
        showText = (TextView) dialog.findViewById(R.id.text);
        showText.setText(textToShow);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.title_icon2);
        adjustDisplayComponents();
        dialog.show();
     
        new Thread(new Runnable() {
            public void run() {
            	dialog.show();
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
                CloseActivity.this.finish();
            }
        }).start();
    }
	
	private void adjustDisplayComponents() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        
        if(height <500){
        	showText.setTextSize(20);
        	timeToSleep = 2500;
        }
	}

}