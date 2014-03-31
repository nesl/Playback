package edu.ucla.ee.nesl.ipshield.playbackservice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.playback.R;

public class MainActivity extends FragmentActivity {

	private SensorManager sm;
	private Sensor acc;
	private static final String TAG = "Playback";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new PlaybackListFragment());
        fragmentTransaction.commit();
	}

    @Override
    protected void onResume() {
      super.onResume();
//      Intent i = new Intent(this, PlaybackService.class);
//      startService(i);
//      sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
