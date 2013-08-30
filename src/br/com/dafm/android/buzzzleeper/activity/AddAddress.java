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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
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

	private EditText name;

	private EditText searchAddress;

	private Spinner ringtone;

	private SeekBar buffer;

	private GeocoderNetwork geocoderNetwork;

	private AddressDAO addressDAO;

	private ArrayList<String> listErros;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_address);

		searchAddress = (EditText) this.findViewById(R.id.txtSearchAddress);
		name = (EditText) this.findViewById(R.id.txtAddName);

		setupMap();
		setupBtnSearchAddress();
		setupSpinnerRingtones();
		setupSeekBarBuffer();
		setupBtnSave();

	}

	private void setupBtnSearchAddress() {
		geocoderNetwork = new GeocoderNetwork();

		RelativeLayout btnSearchAddress = (RelativeLayout) findViewById(R.id.btnSearchAddress);
		btnSearchAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findAddress();
			}
		});
	}

	private void setupBtnSave() {
		geocoderNetwork = new GeocoderNetwork();

		RelativeLayout btnConfirm = (RelativeLayout) findViewById(R.id.btnAddConfirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
	}

	private void findAddress() {

		Address address = geocoderNetwork.findAddress(searchAddress.getText()
				.toString(), 1, getApplicationContext());

		if (address != null) {
			latLng = new LatLng(address.getLatitude(), address.getLongitude());

			CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

			googleMap.moveCamera(center);
			googleMap.animateCamera(zoom);
			addMarker(latLng);
		} else {
			Toast toast = Toast.makeText(this, "ADDRESS NOT FOUND! ",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
			toast.show();
			Log.v(getLocalClassName(), "ADDRESS NOT FOUND!");
		}
	}

	private void setupMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap)).getMap();

		googleMap
				.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng point) {
						addMarker(point);
						CameraUpdate center = CameraUpdateFactory
								.newLatLng(point);
						googleMap.moveCamera(center);
					}
				});
	}

	private void addMarker(LatLng point) {
		if (point != null) {
			Integer radius = buffer.getProgress();

			googleMap.clear();
			CircleOptions circleOptions = new CircleOptions().center(point)
					.radius(radius).fillColor(0x40ff0000)
					.strokeColor(Color.BLUE).strokeWidth(5);
			googleMap.addCircle(circleOptions);
			googleMap.addMarker(new MarkerOptions().position(point));

			RelativeLayout rlInfoMap = (RelativeLayout) this
					.findViewById(R.id.rlInfoMap);
			rlInfoMap.setVisibility(0);

			TextView coordinates = (TextView) this
					.findViewById(R.id.txtCoordinates);
			coordinates.setText("Latitude: "
					+ String.format("%.7f", point.latitude) + ", Longitude: "
					+ String.format("%.7f", point.longitude));

			searchAddress.setText(geocoderNetwork.getAddress(point.latitude,
					point.longitude, 1, getApplicationContext()));
			TextView addressLocation = (TextView) this
					.findViewById(R.id.txtAddressLocation);
			addressLocation.setText(searchAddress.getText());

			latLng = point;
		}
	}

	private void setupSpinnerRingtones() {

		List<String> ringtones = this.getListRingtones(this);

		final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ringtones);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		RelativeLayout icBtnRingtone = (RelativeLayout) this
				.findViewById(R.id.icBtnRingtone);
		icBtnRingtone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}

		});
	}

	private List<String> getListRingtones(Context context) {
		List<String> list = new ArrayList<String>();

		RingtoneManager ringtoneManager = new RingtoneManager(context);
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		Cursor cursor = ringtoneManager.getCursor();

		list.add(getString(R.string.setRingtone));
		while (!cursor.isAfterLast() && cursor.moveToNext()) {
			Ringtone ringtone = ringtoneManager.getRingtone(cursor
					.getPosition());
			list.add(ringtone.getTitle(context));
		}
		cursor.close();

		return list;
	}

	private void setupSeekBarBuffer() {

		buffer = (SeekBar) findViewById(R.id.seekBuffer);

		TextView textView = (TextView) findViewById(R.id.txtBuffer);
		textView.setText(getString(R.string.bufferDistance) + ": "
				+ Integer.toString(buffer.getProgress()) + " "
				+ getString(R.string.meters));

		buffer.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				addMarker(latLng);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// add here your implementation
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				TextView textView = (TextView) findViewById(R.id.txtBuffer);
				textView.setText(getString(R.string.bufferDistance) + ": "
						+ Integer.toString(progress) + " "
						+ getString(R.string.meters));
			}
		});
	}

	private Boolean validate() {
		Integer errors = 0;

		listErros = new ArrayList<String>();

		if (name.getText().toString() == "") {
			listErros.add("");
			errors++;
		} else if (searchAddress.getText().toString() == "") {
			errors++;
		} else if (latLng == null) {
			errors++;
		} else if (buffer.getProgress() < 0) {
			errors++;
		} else if (ringtone.getSelectedItem().toString() == "") {
			errors++;
		}

		return (errors == 0 ? true : false);
	}

	private void save() {
		if (validate()) {
			BlrAddress address = new BlrAddress();
			address.setName(name.getText().toString());
			address.setAddress(searchAddress.getText().toString());
			address.setLat(latLng.latitude);
			address.setLng(latLng.longitude);
			address.setBuffer(buffer.getProgress());
			address.setRingtone(ringtone.getSelectedItem().toString());
			address.setStatus(true);

			addressDAO = new AddressDAO(getApplicationContext());
			BlrAddress savedAddress = addressDAO.save(address);
			if (savedAddress != null && savedAddress.getId() != null) {
				Toast toast = Toast.makeText(this, "ADDRESS ADDED!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
				toast.show();
				Log.v(getLocalClassName(), "ADDRESS ADDED!");
			} else {
				Toast toast = Toast.makeText(this, "ERROR ON ADDING ADDRESS!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}
}
