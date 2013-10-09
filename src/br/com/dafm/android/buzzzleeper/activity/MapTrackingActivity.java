package br.com.dafm.android.buzzzleeper.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTrackingActivity extends FragmentActivity {
	
	private GoogleMap googleMap;
	
	private BlrAddress blrAddress;
	
	private AddressDAO addressDAO;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.map);
	  Bundle extras = getIntent().getExtras();
	  if (extras != null) {
		addressDAO = new AddressDAO(getApplicationContext());
		String value = extras.get("BLR_ADDRESS_ID").toString();
		blrAddress = addressDAO.findById(Integer.parseInt(value));
	  }
	  setupMap();
	  setupBtnBackHome();
	}
	
	private void setupMap() {
		SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap));
		googleMap = mapFragment.getMap();
		if (googleMap != null) {
			addMarker(new LatLng(blrAddress.getLat(), blrAddress.getLng()));
		}
		
	}
	
	private void setupBtnBackHome() {
		RelativeLayout btnAddAddress = (RelativeLayout) findViewById(R.id.btnArrowBack);
		btnAddAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	private void addMarker(LatLng point) {
		googleMap.clear();
		CircleOptions circleOptions = new CircleOptions().center(point)
				.radius(blrAddress.getBuffer()).fillColor(0x40ff0000)
				.strokeColor(Color.BLUE).strokeWidth(5);
		googleMap.addCircle(circleOptions);
		googleMap.addMarker(new MarkerOptions().position(point));

		CameraUpdate center = CameraUpdateFactory.newLatLng(point);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);
	}

}
