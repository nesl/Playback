package edu.ucla.ee.nesl.ipshield.playbackservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
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
	private final static String SOURCEFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "playback_source";
	PlaybackService mService;
    boolean mBound = false;
    
    public ArrayList<String> source;
    
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
		
		Button start = (Button)findViewById(R.id.start_play);
		start.setOnClickListener(this);
		
		Button stop = (Button)findViewById(R.id.stop_play);
		stop.setOnClickListener(this);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_placeholder, new PlaybackListFragment());
        fragmentTransaction.commit();
        
        source = new ArrayList<String>();
        source.add("null for sensor#0");
	}
    
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
			BufferedWriter output = new BufferedWriter(new FileWriter(SOURCEFILE));
			for (int i = 0; i < 17; i++) {
				output.write(source.get(i + 1));
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	try {
			BufferedReader input = new BufferedReader(new FileReader(SOURCEFILE));
			for (int i = 0; i < 17; i++) {
				source.add(input.readLine());
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void onClick(View arg0) {
		if (mBound) {
			if (arg0.getId() == R.id.start_play) {
				mService.startPlay();
			}
			else if (arg0.getId() == R.id.stop_play) {
				mService.stopPlay();
			}
		}
	}
	
	public void setBuffer(String filename, int sensorID) {
		source.set(sensorID, filename);
		if (mBound) {
			mService.setBuffer(filename, sensorID);
		}
		else {
			Log.e(TAG, "Service not bound!");
		}
	}

}
