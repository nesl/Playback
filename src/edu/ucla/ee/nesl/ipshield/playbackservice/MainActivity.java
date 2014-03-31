package edu.ucla.ee.nesl.ipshield.playbackservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.playback.R;

import edu.ucla.ee.nesl.ipshield.playbackservice.PlaybackService.LocalBinder;

public class MainActivity extends FragmentActivity implements OnClickListener{
	private final static String TAG = "PlaybackMainActivity";
	PlaybackService mService;
    boolean mBound = false;
    
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button)findViewById(R.id.start_play);
		button.setOnClickListener(this);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_placeholder, new PlaybackListFragment());
        fragmentTransaction.commit();
	}
    
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

	@Override
	public void onClick(View arg0) {
	      Intent i = new Intent(this, PlaybackService.class);
	      startService(i);
	}
	
	public void setBuffer(String filename, int sensorID) {
		if (mBound) {
			mService.setBuffer(filename, sensorID);
		}
		else {
			Log.e(TAG, "Service not bound!");
		}
	}

}
