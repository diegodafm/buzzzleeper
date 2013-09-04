package br.com.dafm.android.buzzzleeper.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

public class MainActivity extends Activity {

	private AddressDAO addressDAO;

	private List<BlrAddress> addresses;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupBtnAddAddress();
		getBusStopList();
	}

	private void setupBtnAddAddress() {
		ImageView btnAddAddress = (ImageView) findViewById(R.id.btnAddAddress);
		btnAddAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent k = new Intent(getApplicationContext(), AddAddress.class);
				startActivity(k);
			}
		});
	}

	private void getBusStopList() {
		addressDAO = new AddressDAO(getApplicationContext());
		addresses = addressDAO.getAllAddress();

		LinearLayout list = (LinearLayout) findViewById(R.id.list);
		for (BlrAddress blrAddress : addresses) {
			list.addView(buildViewItems(blrAddress));
		}
	}

	private View buildViewItems(BlrAddress blrAddress) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.bus_stop_item, null);
		

		TextView txtName = (TextView) view.findViewById(R.id.blrName);
		txtName.setText(blrAddress.getName());
		
		TextView txtAddress = (TextView) view.findViewById(R.id.blrAddress);
		txtAddress.setText(blrAddress.getAddress());

//		TextView txtBuffer = (TextView) view.findViewById(R.id.blrBuffer);
//		txtBuffer.setText(blrAddress.getBuffer().toString() + " " + getString(R.string.meters));
//		
//		TextView txtRingtone = (TextView) view.findViewById(R.id.blrRingtone);
//		txtRingtone.setText(blrAddress.getRingtone());

		ImageView imgMap = (ImageView) view.findViewById(R.id.blrImgMap);
		String imgPath = getFilesDir() + "/" + "blrAddress_"+ blrAddress.getId() + ".png";
		imgMap.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		
		return view;
	}

}
