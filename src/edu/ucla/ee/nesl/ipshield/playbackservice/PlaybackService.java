package edu.ucla.ee.nesl.ipshield.playbackservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements SensorEventListener{
	private static String TAG = "PlaybackService";
	private SensorManager mSensorManager;
	private LocationManager mLocationManager;
	private static int buffer_index = 0, loc_index = 0;
	private static int delay_count, loc_delay_count = 10;
	private static ArrayList<ArrayList<SensorVector>> sensorBuffer;
	private static int count[];
	
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		PlaybackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlaybackService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		sensorBuffer = new ArrayList<ArrayList<SensorVector>>();
		count = new int[19];
		for (int i = 0; i <= 18; i++) {
			sensorBuffer.add(new ArrayList<SensorVector>());
			count[i] = 0;
		}
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		return mBinder;
	}
	
	class SensorVector {
		private float[] data;
		
		public SensorVector(int dim) {
			data = new float[dim];
		}
		
		public void set(int index, float value) {
			data[index] = value;
		}
		
		public float get(int index) {
			return data[index];
		}
	}
	
	public void setBuffer(String filename, int sensorID) {
		Log.i(TAG, "filename=" + filename);
		sensorBuffer.get(sensorID).clear();
		count[sensorID] = 0;
		
		int dim = SensorType.getDimension(sensorID);
		Log.i(TAG, "dim=" + dim);
		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));
			String str = null;
			while ((str = input.readLine()) != null) {
				Log.i(TAG, str);
				String[] axs = str.split(",");
				SensorVector e = new SensorVector(dim);
				for (int i = 0; i < dim; i++) {
					e.set(i, Float.valueOf(axs[i]));
					Log.i(TAG, "v[i]=" + Float.valueOf(axs[i]));
				}
				sensorBuffer.get(sensorID).add(e);
				count[sensorID]++;
				Log.i(TAG, "count=" + count[sensorID]);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.i(TAG, "sensor=" + sensorID + ", data count=" + count[sensorID]);
	}
	
	public void startPlay() {
		for (int sensorID = 1; sensorID < 18; sensorID++) {
			if (count[sensorID] > 0) {
				Sensor mSensor = mSensorManager.getDefaultSensor(sensorID);
				mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
		
		// play gps data
		if (count[18] > 0) {
			mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Log.i(TAG, "gps " + LocationManager.GPS_PROVIDER);
			setTimer();
			loc_index = 0;
		}
	}
	
	public void stopPlay() {
		mSensorManager.unregisterListener(this);
	}
	
	private void setTimer() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Location l = new Location("gps");
				l.reset();
				l.setProvider("playback");
				l.setLatitude(sensorBuffer.get(18).get(loc_index).get(0));
				l.setLongitude(sensorBuffer.get(18).get(loc_index).get(1));
				
				mLocationManager.setLocation(l);
				
				if (loc_delay_count == 10) {
					loc_index = (loc_index + 1) % count[18];
					loc_delay_count = 0;
				}
				loc_delay_count++;
				setTimer();
			}
		}, 1000);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		for (int sensorID = 1; sensorID < 18; sensorID++) {
			if(event.sensor.getType() == sensorID && count[sensorID] > 0) {
				Log.i(TAG, delay_count + ":" + event.values[0] + "," + event.values[1]+ "," + event.values[2]);
				int dim = SensorType.getDimension(sensorID);
				for (int i = 0; i < dim; i++) {
					event.values[i] = sensorBuffer.get(sensorID).get(buffer_index).get(i);
				}
				mSensorManager.sendEvents(event, event.sensor);
				
				if (delay_count >= 5) {
					buffer_index = (buffer_index + 1) % count[sensorID];
					delay_count = 0;
				}
				delay_count++;
			}
		}
/*		else  if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			Log.i(TAG,  "light:" + event.values[0] + "," + event.values[1]+ "," + event.values[2]);
			event.values[0] = l;
			mSensorManager.sendEvents(event, event.sensor);
			if (j == 5) {
				l++;
				j = 0;
			}
			j = j + 1;

		}*/
	}
}
