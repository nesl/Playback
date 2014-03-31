package edu.ucla.ee.nesl.ipshield.playbackservice;

import android.content.Context;

public class SensorType {
	public static final int GPS_ID = 18;
	private static final int DBID_UNDEFINED = -1;
	private Context context;
	private int dbId; // the sensors ID in our database
	private int androidId; // the sensor type constant according to android
	private String name = null;
	private String file = null;
	
	private static class AndroidSensorIdData { // {{{
		protected String sensorName;
		protected String[] valueNames;
		protected String[] valueUnits;
		protected float[] defaultValues;

		protected AndroidSensorIdData (String newName, String[] newValueNames, String[] newValueUnits) {
			this.sensorName = newName;
			this.valueNames = newValueNames;
			this.valueUnits = newValueUnits;

			this.defaultValues= new float[]{0f, 0f, 0f, 0f, 0f}; // default value for the default values
		}

//		protected AndroidSensorIdData (String newName, String[] newValueNames, String[] newValueUnits, float[] newDefaultValues) {
//			this(newName, newValueNames, newValueUnits);
//
//			this.defaultValues = newDefaultValues;
//		}
	} // }}}
	private static AndroidSensorIdData[] androidSensorIdData = { // {{{
		/* 0x00 */ new AndroidSensorIdData("(Unused sensor ID)", new String[]{}, new String[]{}),
		/* 0x01 */ new AndroidSensorIdData("Accelerometer", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x02 */ new AndroidSensorIdData("Magnetic field", new String[]{"X", "Y", "Z"}, new String[]{"\u03bcT", "\u03bcT", "\u03bcT"}),
		/* 0x03 */ new AndroidSensorIdData("Orientation", new String[]{"Aziumuth", "Pitch", "Roll"}, new String[]{"degrees", "degrees", "degrees"}), // orientation type is deprecated
		/* 0x04 */ new AndroidSensorIdData("Gyroscope", new String[]{"X", "Y", "Z"}, new String[]{"rad/s", "rad/s", "rad/s"}),
		/* 0x05 */ new AndroidSensorIdData("Light", new String[]{"Illuminance"}, new String[]{"lx"}),
		/* 0x06 */ new AndroidSensorIdData("Pressure", new String[]{"Pressure"}, new String[]{"millibars"}),
		/* 0x07 */ new AndroidSensorIdData("Temperature (deprecated)", new String[]{}, new String[]{}), // deprecated for TYPE_AMBIENT_TEMPERATURE
		/* 0x08 */ new AndroidSensorIdData("Proximity", new String[]{"Distance"}, new String[]{"cm"}),
		/* 0x09 */ new AndroidSensorIdData("Gravity", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x0a */ new AndroidSensorIdData("Linear acceleration", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x0b */ new AndroidSensorIdData("Rotation vector", new String[]{"X", "Y", "Z", "\u03b8", "Accuracy"}, new String[]{"unitless", "unitless", "unitless", "radians", "radians"}),
		/* 0x0c */ new AndroidSensorIdData("Relative humidity", new String[]{"Air humidity"}, new String[]{"%"}),
		/* 0x0d */ new AndroidSensorIdData("Ambient temperature", new String[]{"Room temperature"}, new String[]{"\u00b0C"}),
		/* 0x0e */ new AndroidSensorIdData("Magnetic field (uncalibrated)", new String[]{"Uncalib. X", "Uncalib. Y", "Uncalib. Z"}, new String[]{"\u03bcT", "\u03bcT", "\u03bcT"}),
		/* 0x0f */ new AndroidSensorIdData("Game rotation vector", new String[]{"X", "Y", "Z", "\u03b8", "Accuracy"}, new String[]{"unitless", "unitless", "unitless", "radians", "radians"}),
		/* 0x10 */ new AndroidSensorIdData("Gyroscope (uncalibrated)", new String[]{"Uncalib. X", "Uncalib. Y", "Uncalib. Z"}, new String[]{"rad/s", "rad/s", "rad/s"}),
		/* 0x11 */ new AndroidSensorIdData("Significant motion", new String[]{"Motion"}, new String[]{"unitless"}),
		/* 0x12 */ new AndroidSensorIdData("GPS", new String[]{"Latitute", "Longitude"}, new String[]{"unitless", "unitless"})
	}; // }}}

	@Override public boolean equals (Object obj) { // {{{
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (! (obj instanceof SensorType)) {
			return false;
		}

		SensorType sObj = (SensorType) obj;

		return (this.getAndroidId() == sObj.getAndroidId());
	} // }}}
	@Override public int hashCode () { // {{{
		return this.getAndroidId();
	} // }}}

	private SensorType () { // this class should not be instantiated directly {{{
	} /// }}}


	public static SensorType defineFromAndroid (int androidId, Context _context) { // {{{
		SensorType st = new SensorType();
		st.androidId = androidId;
		st.dbId = DBID_UNDEFINED;
		st.context = _context;
		st.name = androidSensorIdData[st.androidId].sensorName;

		return st;
	} // }}}

	public String toString () { // {{{
		return getName();
	} // }}}
	public String getName () { // {{{
		if (name == null) {
			name = androidSensorIdData[getAndroidId()].sensorName;
		}

		return name;
	} // }}}

	public int getAndroidId () { // {{{
		return androidId;
	} // }}}
	public String[] getAndroidValueNames () { // {{{
		return androidSensorIdData[androidId].valueNames;
	} // }}}
	public String[] getAndroidValueUnits () { // {{{
		return androidSensorIdData[androidId].valueUnits;
	} // }}}
	public float[] getDefaultValues () { // {{{
		return androidSensorIdData[androidId].defaultValues;
	} // }}}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}
