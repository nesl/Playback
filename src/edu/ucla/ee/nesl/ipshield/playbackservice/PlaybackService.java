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
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements SensorEventListener{
	private static String TAG = "PlaybackService";
	private SensorManager mSensorManager;
	private static int buffer_index = 0;
	private static int delay_count = 10;
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
		for (int i = 0; i < 18; i++) {
			sensorBuffer.add(new ArrayList<SensorVector>());
			count[i] = 0;
		}
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		return mBinder;
	}
	
	class SensorVector {
		private float x, y, z;

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getZ() {
			return z;
		}

		public void setZ(float z) {
			this.z = z;
		}
	}
	
	public void setBuffer(String filename, int sensorID) {
		Log.i(TAG, "filename=" + filename);
		sensorBuffer.get(sensorID).clear();
		count[sensorID] = 0;
		
		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));
			String str = null;
			while ((str = input.readLine()) != null) {
				String[] axs = str.split(",");
				SensorVector e = new SensorVector();
				e.setX(Float.valueOf(axs[0]));
				e.setY(Float.valueOf(axs[1]));
				e.setZ(Float.valueOf(axs[2]));
				sensorBuffer.get(sensorID).add(e);
				count[sensorID]++;
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
	}
	
	public void stopPlay() {
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		for (int sensorID = 1; sensorID < 18; sensorID++) {
			if(event.sensor.getType() == sensorID && count[sensorID] > 0) {
				Log.i(TAG, delay_count + ":" + event.values[0] + "," + event.values[1]+ "," + event.values[2]);

				event.values[0] = sensorBuffer.get(sensorID).get(buffer_index).getX();
				event.values[1] = sensorBuffer.get(sensorID).get(buffer_index).getY();
				event.values[2] = sensorBuffer.get(sensorID).get(buffer_index).getZ();
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
