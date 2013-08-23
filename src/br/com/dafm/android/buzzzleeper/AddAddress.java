package br.com.dafm.android.buzzzleeper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Diego Alisson on 8/16/13.
 */
public class AddAddress extends FragmentActivity {

	private double latLng;

	private GoogleMap googleMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_address);
//		setUpMap();

	}

	private void setUpMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap)).getMap();

		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				40.76793169992044, -73.98180484771729));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);

	}

}
