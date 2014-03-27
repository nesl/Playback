package com.example.playback;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager sm;
	private Sensor acc;
	private static final String TAG = "Playback";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/
	@Override
    public final void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "my sensor is " + event.sensor.getName() + "timestamp = " + event.timestamp + "values: " + event.values[0] + event.values[1] + event.values[2]);
        event.values[2] = 5;
        sm.sendEvents(event, event.sensor);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
      // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
      super.onResume();
      Intent i = new Intent(this, playbackservice.class);
      startService(i);
//      sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
