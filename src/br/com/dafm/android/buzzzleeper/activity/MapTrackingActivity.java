package br.com.dafm.android.buzzzleeper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.TrackingService;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTrackingActivity extends FragmentActivity {
	
	private LatLng blrPoint;
	
	private GoogleMap googleMap;
	
	private BlrAddress blrAddress;
	
	private AddressDAO addressDAO;

	private BroadcastReceiver receiver;

	private Typeface signikaSemibold;
	
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
	  blrPoint = new LatLng(blrAddress.getLat(), blrAddress.getLng());
	  displayData();
	  setupMap();
	  setupBtnBackHome();
	  registerReceiver();
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	
	private void setupMap() {
		SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
		googleMap = mapFragment.getMap();
		if (googleMap != null) {
			addMarker();
		}
		
	}
	
	private void setupBtnBackHome() {
		RelativeLayout btnAddAddress = (RelativeLayout) findViewById(R.id.btnArrowBack);
		btnAddAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopActivity(true);
				onBackPressed();
			}
		});
	}

	private void addMarker() {
		googleMap.clear();
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(blrPoint);
		circleOptions.radius(blrAddress.getBuffer());
		circleOptions.fillColor(0x4058c2cb);
		circleOptions.strokeColor(Color.parseColor("#58c2cb"));
		circleOptions.strokeWidth(5);
		googleMap.addCircle(circleOptions);
		googleMap.addMarker(new MarkerOptions().position(blrPoint));

		CameraUpdate center = CameraUpdateFactory.newLatLng(blrPoint);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);
		googleMap.setMyLocationEnabled(true);
	}
	
	private void updateMarker(){
		googleMap.clear();
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(blrPoint);
		circleOptions.radius(blrAddress.getBuffer());
		circleOptions.fillColor(0x40d64d4d);
		circleOptions.strokeColor(Color.parseColor("#d64d4d"));
		circleOptions.strokeWidth(5);
		googleMap.addCircle(circleOptions);
		googleMap.addMarker(new MarkerOptions().position(blrPoint));
	}
	
	private void startTracking(){
		Intent intent=new Intent(this, TrackingService.class);
		intent.putExtra("BLR_ADDRESS_ID", blrAddress.getId());
		intent.putExtra("BLR_ADDRESS_NAME", blrAddress.getName());
	    startService(intent);
	}	
	
	private void registerReceiver(){
		receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				
				if(intent.getAction().equals("trackingMap")) {
					Location location = (Location) intent.getExtras().get("location");
					Double distance = getCurrentDistance(location);	    	
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
					if(distance <=  blrAddress.getBuffer()){
						updateMarker();
					}
				}
		    
			    if(intent.getAction().equals("stopTrackingMap")) {
			    	stopTracking();
			    }
		    
			    if(intent.getAction().equals("startTrackingMap")) {
			    	startTracking();
			    }
		  }
		};

		registerReceiver(receiver, new IntentFilter("trackingMap"));
		registerReceiver(receiver, new IntentFilter("stopTrackingMap"));
		registerReceiver(receiver, new IntentFilter("startTrackingMap"));
	}
	
	public Double getCurrentDistance(Location location) {
		Double distance;
		Location locationA = new Location("Posicao Atual");
		locationA.setLatitude(location.getLatitude());
		locationA.setLongitude(location.getLongitude());
		Location locationB = new Location(blrAddress.getName());
		locationB.setLatitude(blrAddress.getLat());
		locationB.setLongitude(blrAddress.getLng());
		distance = (double) locationA.distanceTo(locationB);
		return distance;
	}
	
	private void stopTracking(){
		stopService(new Intent(this, TrackingService.class));
	}

	private void stopActivity(Boolean stopAll){
		stopTracking();
		Intent data = new Intent("stopTrackingInfo");
		sendBroadcast(data);
	}
	
	private void displayData(){
		signikaSemibold = Typeface.createFromAsset(getAssets(),"fonts/Signika-Semibold.ttf");
		TextView name = (TextView) findViewById(R.id.txtTitle);
		name.setText(blrAddress.getName());
		name.setTypeface(signikaSemibold);
	}
	
	


}
