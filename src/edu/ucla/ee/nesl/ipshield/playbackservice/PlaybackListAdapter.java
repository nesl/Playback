package edu.ucla.ee.nesl.ipshield.playbackservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class PlaybackListAdapter extends ArrayAdapter<SensorType> implements OnClickListener {
	private final Context context;
	private final ArrayList<SensorType> sensorList;
	private int curPosition;
	private PlaybackListAdapter self;
	
	public PlaybackListAdapter(Context context, List<SensorType> sensors) {
		super(context, R.layout.playback_list_entry, sensors);
		this.context = context;
		this.sensorList = (ArrayList<SensorType>) sensors;
		self = this;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.playback_list_entry, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.playback_list_entry_name);
        TextView subTitleView = (TextView) rowView.findViewById(R.id.playback_list_entry_filename);
        
        Button bt = (Button) rowView.findViewById(R.id.playback_list_entry_select);
       // Button btc = (Button) rowView.findViewById(R.id.playback_list_entry_cancel);
        titleView.setText(sensorList.get(position).getName());
        String fileName = sensorList.get(position).getFile();
        subTitleView.setText(fileName == null || fileName.equals("null") ? "No file selected." : fileName);
        bt.setText("Select File");
        bt.setTag(position);
       // btc.setText("Stop Playing");
       // btc.setTag(position);
        bt.setOnClickListener(this);
       // btc.setOnClickListener(this);
        return rowView;
	}

	@Override
	public void onClick(View v) {
		curPosition = (Integer) v.getTag();
		
		if (v.getId() == R.id.playback_list_entry_select) {
			FileChooserDialog dialog = new FileChooserDialog(context);
			dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
		         public void onFileSelected(Dialog source, File file) {
		             source.hide();
		             sensorList.get(curPosition).setFile(file.getAbsolutePath());
		             self.notifyDataSetChanged();
		             
		             // start to play data
		             //PlaybackService.startPlay(file.getAbsolutePath() + file.getName(), curPosition);
		             ((MainActivity) context).setBuffer(file.getAbsolutePath(), curPosition + 1);
		             	             
		             Toast toast = Toast.makeText(source.getContext(), "File selected: " + file.getName(), Toast.LENGTH_LONG);
		             toast.show();
		         }
		         public void onFileSelected(Dialog source, File folder, String name) {

		         }
		     });
		    dialog.show();
		}
	}
}
