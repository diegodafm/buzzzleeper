package br.com.dafm.android.buzzzleeper.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.service.GeocoderNetwork;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Diego Alisson on 8/16/13.
 */
public class AddAddress extends FragmentActivity {

	private LatLng latLng;
	private GoogleMap googleMap;

	private EditText searchAddress;

	private Spinner ringtone;

	private Spinner buffer;

	private GeocoderNetwork geocoderNetwork;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.add_address);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		setUpMap();
		setupBtnSearchAddress();
		setupSpinnerRingtones();
		setupSpinnerBuffer();
	}

	private void setupBtnSearchAddress() {
		geocoderNetwork = new GeocoderNetwork();
		searchAddress = (EditText) this.findViewById(R.id.txtSearchAddress);

		RelativeLayout btnSearchAddress = (RelativeLayout) findViewById(R.id.btnSearchAddress);
		btnSearchAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findAddress();
			}
		});
	}

	private void findAddress() {

		Address address = geocoderNetwork.findAddress(searchAddress.getText()
				.toString(), 1, getApplicationContext());

		latLng = new LatLng(address.getLatitude(), address.getLongitude());

		CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);

		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);
	}

	private void setUpMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap)).getMap();

		googleMap
				.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng point) {
						addMarker(point);
					}
				});
	}

	private void addMarker(LatLng point) {

		if (point != null) {
			Integer radius = Integer.parseInt(buffer.getSelectedItem()
					.toString());

			googleMap.clear();
			CircleOptions circleOptions = new CircleOptions().center(point)
					.radius(radius).fillColor(0x40ff0000)
					.strokeColor(Color.BLUE).strokeWidth(5);
			googleMap.addCircle(circleOptions);
			googleMap.addMarker(new MarkerOptions().position(point));
		}
	}

	private void setupSpinnerRingtones() {
		this.ringtone = (Spinner) findViewById(R.id.spnRingtone);
		List<String> ringtones = this.getListRingtones(this);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ringtones);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ringtone.setAdapter(dataAdapter);

	}

	private List<String> getListRingtones(Context context) {
		List<String> list = new ArrayList<String>();

		RingtoneManager ringtoneManager = new RingtoneManager(context);
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		Cursor cursor = ringtoneManager.getCursor();
		while (!cursor.isAfterLast() && cursor.moveToNext()) {
			Ringtone ringtone = ringtoneManager.getRingtone(cursor
					.getPosition());
			list.add(ringtone.getTitle(context));
		}
		cursor.close();

		return list;
	}

	private void setupSpinnerBuffer() {
		buffer = (Spinner) this.findViewById(R.id.spnBuffer);

		buffer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				addMarker(latLng);
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				return;
			}
		});
	}

}
