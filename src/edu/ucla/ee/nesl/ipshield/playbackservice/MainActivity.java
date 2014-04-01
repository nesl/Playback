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
import edu.ucla.ee.nesl.ipshield.playbackservice.PlaybackService.LocalBinder;

public class MainActivity extends FragmentActivity implements OnClickListener{
	private final static String TAG = "PlaybackMainActivity";
	//private final static String SOURCEFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/playback_source";
	PlaybackService mService;
    boolean mBound = false;
    
    //public ArrayList<String> source;
    
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            
//            for (int i = 0; i <= 17; i++) {
//            	String str = source.get(i + 1);
//            	if (str != null && !str.equals("null")) {
//            		if (mBound) {
//            			mService.setBuffer(source.get(i + 1), i + 1);
//            		}
//            	}
//            }
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
		
//        source = new ArrayList<String>();
//        for (int i = 0; i <= 19; i++) {
//        	source.add("null");
//		}
		
		Button start = (Button)findViewById(R.id.start_play);
		start.setOnClickListener(this);
		
		Button stop = (Button)findViewById(R.id.stop_play);
		stop.setOnClickListener(this);
		
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
    
    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
//    @Override
//    protected void onPause() {
//    	super.onPause();
//    	//Log.i(TAG, "on paused called!");
//    	try {
//			BufferedWriter output = new BufferedWriter(new FileWriter(SOURCEFILE));
//			for (int i = 0; i <= 17; i++) {
//				output.write(source.get(i + 1) + "\n");
//			}
//			output.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	
//
//    }
//    
//    @Override
//    protected void onResume() {
//    	super.onResume();
//    	try {
//			BufferedReader input = new BufferedReader(new FileReader(SOURCEFILE));
//			for (int i = 0; i <= 17; i++) {
//				source.set((i + 1), input.readLine());
//			}
//			input.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }

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
		//source.set(sensorID, filename);
		if (mBound) {
			mService.setBuffer(filename, sensorID);
		}
		else {
			Log.e(TAG, "Service not bound!");
		}
	}

}
