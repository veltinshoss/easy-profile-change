package de.pepping.android.ringtone.handler;

import java.util.Calendar;

import android.os.Handler;
import android.os.Message;
import de.pepping.android.ringtone.activity.RingToneActivity;

public class UpdateTimer extends Handler {
    public final static int MSG_START = 0;
    public final static int MSG_STOP = 1;
    public final static int MSG_UPDATE = 2;
    public final static int REFRESH_PERIOD = 1000; // in ms
    public final static int SPIN_PERIOD = 100; // in ms 
 
    // pointer to the user interface adapter
    private RingToneActivity mUI;
    // remember the last time the UI was updated
    private long mLastTime;
 
    public UpdateTimer(RingToneActivity theUI)
    {
        super();
        mUI = theUI;
        mLastTime = 0;
    }
 
    // handle messages to implement the screen refresh timer
    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
 
        switch (msg.what)
        {
        case MSG_START:
            // start the timer by sticking an update message
            // into the message queue
            this.sendEmptyMessage(MSG_UPDATE);
            break;
 
        case MSG_UPDATE:
            // the timer loops by sticking delayed messages into the
            // queue at the spin period.  Each spin period, use
            // checkTime() to see if a UI update is required
            this.checkTime();
            this.sendEmptyMessageDelayed(MSG_UPDATE, SPIN_PERIOD);
            break;                                 
 
        case MSG_STOP:
            // stop the timer by removing any and all delayed update
            // messages that have not been processed
            this.removeMessages(MSG_UPDATE);
            break;
 
        default:
            break;
        }
    }
 
    // check how much time has passed and update the UI
    // if required.
    private void checkTime(){
        // how much time has passed since the last update?
        long currTime = Calendar.getInstance().getTimeInMillis();
        long dt = currTime - mLastTime;
        // if it is more than the refresh period, update the UI
        if (dt > REFRESH_PERIOD){
        	mUI.updateTimer();
        	mLastTime = currTime;
        }
    }
}
