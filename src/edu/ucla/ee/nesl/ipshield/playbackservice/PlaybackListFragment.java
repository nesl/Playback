package edu.ucla.ee.nesl.ipshield.playbackservice;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;


public class PlaybackListFragment extends ListFragment {
	private PlaybackListAdapter mAdapter;
	private Context context;
	private ArrayList<SensorType> sensorList;


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	private void initSensorList() {
		sensorList = new ArrayList<SensorType>();
		for (int i = 1; i <= 18; i++) {
			SensorType st = SensorType.defineFromAndroid(i, context);
			st.setFile(((MainActivity) context).source.get(i));
			sensorList.add(st);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.getContext();
        initSensorList();
        mAdapter = new PlaybackListAdapter(getActivity(), sensorList);
        setListAdapter(mAdapter);
	}
}
