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
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.GeocoderNetwork;
import br.com.dafm.android.buzzzleeper.service.ImageService;

public class MainActivity extends Activity {

	private AddressDAO addressDAO;

	private List<BlrAddress> addresses;
	
	GeocoderNetwork geocoderNetwork;
	
	ImageService imageService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupBtnAddAddress();
		
		insertItemsTeste();
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

	private View buildViewItems(final BlrAddress blrAddress) {
		final View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.bus_stop_item, null);
		

		TextView txtName = (TextView) view.findViewById(R.id.blrName);
		txtName.setText(blrAddress.getName());
		
		TextView txtAddress = (TextView) view.findViewById(R.id.blrAddress);
		txtAddress.setText(blrAddress.getAddress());

		ImageView imgMap = (ImageView) view.findViewById(R.id.blrImgMap);
		String imgPath = getFilesDir() + "/" + "blrAddress_"+ blrAddress.getId() + ".png";
		if(BitmapFactory.decodeFile(imgPath)==null){
			new Thread(new Runnable() {
				public void run() {
					imageService = new ImageService();
					imageService.createImageMap(blrAddress, getApplicationContext());
				}
			}).start();
			
		}else{			
			imgMap.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		}
		
		LinearLayout btnShowDetails = (LinearLayout) view.findViewById(R.id.btnShowDetails);
		btnShowDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent k = new Intent(getApplicationContext(),
						ShowAddress.class);
				startActivity(k);
			}
		});
		
		
		return view;
	}
	
	private void insertItemsTeste(){
		addressDAO = new AddressDAO(getApplicationContext());
		List<BlrAddress> list = addressDAO.getAllAddress();
		
		if(list.isEmpty()){		
			for (int i = 0; i < 5; i++) {
				BlrAddress blrAddress = new BlrAddress();
				blrAddress.setName("Address "+ i);
				blrAddress.setAddress("Address "+ i);
				blrAddress.setLat(-19.858617d);
				blrAddress.setLng(-43.918408d);
				blrAddress.setRingtone("Claro");
				blrAddress.setBuffer(300);
				blrAddress.setStatus(true);
				addressDAO.save(blrAddress);
			}
		}
		
	}

}
