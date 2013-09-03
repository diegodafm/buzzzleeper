package br.com.dafm.android.buzzzleeper.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.GPSTracker;
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

	private SeekBar buffer;

	private GeocoderNetwork geocoderNetwork;

	private AddressDAO addressDAO;

	private ArrayList<String> listErros;

	private BlrAddress blrAddress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_address);

		searchAddress = (EditText) this.findViewById(R.id.txtSearchAddress);

		blrAddress = new BlrAddress();
		setupMap();
		setupBtnSearchAddress();
		setupBtnSearchByGPS();
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
	
	private void setupBtnSearchByGPS() {
		RelativeLayout btnSearchByGPS = (RelativeLayout) findViewById(R.id.btnSearchGPS);
		btnSearchByGPS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GPSTracker gps = new GPSTracker(getApplicationContext());

				// check if GPS enabled
				if (gps.canGetLocation()) {
					
					LatLng point = new LatLng( gps.getLatitude(), gps.getLongitude());
					CameraUpdate center = CameraUpdateFactory.newLatLng(point);
					CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
					googleMap.moveCamera(center);
					googleMap.animateCamera(zoom);
					addMarker(point);

				} else {
					gps.showSettingsAlert();
				}
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
		final List<String> ringtones = this.getListRingtones(this);
		RelativeLayout icBtnRingtone = (RelativeLayout) this
				.findViewById(R.id.icBtnRingtone);
		icBtnRingtone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					showListRingtones(ringtones);
				} catch (Exception e) {
					new RuntimeException();
					Log.v("ERROR", e.getMessage());
				}
			}
		});
	}

	private void showListRingtones(final List<String> ringtones) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final TextView txtRingtone = (TextView) this.findViewById(R.id.txtRingtone);
		builder.setTitle(R.string.setRingtone).setItems(
				ringtones.toArray(new CharSequence[ringtones.size()]),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						blrAddress.setRingtone(ringtones.get(which));
						txtRingtone.setText(blrAddress.getRingtone());
					}
				});
		builder.create();
		builder.show();
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
	
	private void setupBtnSave() {
		geocoderNetwork = new GeocoderNetwork();

		LinearLayout btnConfirm = (LinearLayout) findViewById(R.id.btnAddConfirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
	}

	private Boolean validate() {
		listErros = new ArrayList<String>();

		if (blrAddress.getName().equals("")) {
			listErros.add(getString(R.string.nameCannotBeIsEmpty));
		}
		
		if (blrAddress.getAddress().equals("")) {
			listErros.add(getString(R.string.addressCannotBeIsEmpty));
		}
		
		if (blrAddress.getLat() == null || blrAddress.getLng() == null ) {
			listErros.add(getString(R.string.positionCannotBeIsEmpty));
		}
		
		if (blrAddress.getBuffer() <= 0) {
			listErros.add(getString(R.string.bufferCannotBeIsEmpty));
		}
		
		if (blrAddress.getRingtone().equals("") || blrAddress.getRingtone().equals(getString(R.string.setRingtone))) {
			listErros.add(getString(R.string.ringtoneCannotBeIsEmpty));
		}
		return (listErros.isEmpty() ? true : false);
	}
	
	private void updateValues(){
		
		EditText name = (EditText) this.findViewById(R.id.txtAddName);
		blrAddress.setName(name.getText().toString());
		
		TextView address = (TextView) this.findViewById(R.id.txtAddressLocation);
		blrAddress.setAddress(address.getText().toString());

		if(latLng != null){
			blrAddress.setLat(latLng.latitude);
			blrAddress.setLng(latLng.longitude);
		}
		
		SeekBar buffer = (SeekBar) this.findViewById(R.id.seekBuffer);
		blrAddress.setBuffer(buffer.getProgress());
		
		TextView ringtone = (TextView) this.findViewById(R.id.txtRingtone);
		blrAddress.setRingtone(ringtone.getText().toString());
		
		blrAddress.setStatus(true);
	}

	private void save() {
		updateValues();
		
		if (validate()) {

			addressDAO = new AddressDAO(getApplicationContext());
			BlrAddress savedAddress = addressDAO.save(blrAddress);
			if (savedAddress != null && savedAddress.getId() != null) {
				
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle(getString(R.string.success));
				
				alertDialog.setMessage(getString(R.string.busStopAdded));
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
					   // TODO Add your code for the button here.
				   }
				});
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.show();
			}
		}else{
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(getString(R.string.pleaseFixItFirst));
			
			StringBuilder erros = new StringBuilder();
			for (String error : listErros) {
				erros.append(error).append("\n");
			}
			alertDialog.setMessage(erros.toString());
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // TODO Add your code for the button here.
			   }
			});
			// Set the Icon for the Dialog
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.show();
		}
	}
}
