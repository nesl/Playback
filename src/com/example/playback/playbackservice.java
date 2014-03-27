package com.example.playback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class playbackservice extends Service implements SensorEventListener{
	private static String tag = "playbackservice";
	private SensorManager msensormanager;
	private Sensor macc;
	private Sensor mlight;
	private static int k = 0;
	private static float i = 10, j = 0,  l = 10;
	private static float sq[][] = {
		{0f,9f}, 
			{0f,7f}, {0f,5f},{0f,3f},{0f,0f},{0f,-1f}, {0f,-3f},{0f,-5f},
		{2f, -9f}, 
			{1.5f, -9f}, {1f, -9f}, {0.5f, -9f},      
		{0f, -9f}, 
		 	{0f, -7f},  {0f, -5f},  {0f, -3f}, {-1f, -1f},  {-1f, 0f},  {-1f, 2f},  {-1f, 5f},  {-1f, 7f},
		{-1f, 8f},
		 	 {0f, 7f}, {0f, 6f} };
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			Log.i(tag, i + ":" + event.values[0] + "," + event.values[1]+ "," + event.values[2]);

			event.values[0] = sq[k][0];
			event.values[1] = sq[k][1];
			msensormanager.sendEvents(event, event.sensor);
			if (i >= 5) {
				k = (k + 1) % 24;
				i = 0;
			}
			i++;
		} else  if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			Log.i(tag,  "light:" + event.values[0] + "," + event.values[1]+ "," + event.values[2]);
			event.values[0] = l;
			msensormanager.sendEvents(event, event.sensor);
			if (j == 5) {
				l++;
				j = 0;
			}
			j = j + 1;

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		msensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		macc   = msensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mlight = msensormanager.getDefaultSensor(Sensor.TYPE_LIGHT);
		msensormanager.registerListener(this, macc, SensorManager.SENSOR_DELAY_NORMAL);
		msensormanager.registerListener(this, mlight, SensorManager.SENSOR_DELAY_FASTEST);
//		i = 0; k = 1;
//		SensorEvent event = new SensorEvent();
		return 0;
	}
}
