package br.com.dafm.android.buzzzleeper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class ShowAddress extends FragmentActivity {

	private Typeface signikaSemibold;

	private BlrAddress blrAddress;

	private AddressDAO addressDAO;

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_address);
		setup();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setup();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void setupFontTypeFace() {
		signikaSemibold = Typeface.createFromAsset(getAssets(),
				"fonts/Signika-Semibold.ttf");
	}
	
	private void setup(){
		Bundle extras = getIntent().getExtras();

		setupFontTypeFace();

		if (extras != null) {
			addressDAO = new AddressDAO(getApplicationContext());
			String value = extras.get("BLR_ADDRESS_ID").toString();
			blrAddress = addressDAO.findById(Integer.parseInt(value));

			TextView name = (TextView) findViewById(R.id.txtName);
			name.setText(blrAddress.getName());
			name.setTypeface(signikaSemibold);

			TextView address = (TextView) findViewById(R.id.txtAddress);
			address.setText(blrAddress.getAddress());
			address.setTypeface(signikaSemibold);

			TextView buffer = (TextView) findViewById(R.id.txtBuffer);
			buffer.setText(Integer.toString(blrAddress.getBuffer()) + " "
					+ getString(R.string.meters));
			buffer.setTypeface(signikaSemibold);

			TextView ringtone = (TextView) findViewById(R.id.txtRingtone);
			ringtone.setText(blrAddress.getRingtone());
			ringtone.setTypeface(signikaSemibold);

			TextView coordinates = (TextView) findViewById(R.id.txtCoordinates);
			coordinates.setText(String.format("%.7f", blrAddress.getLat())
					+ ", " + String.format("%.7f", blrAddress.getLng()));
			coordinates.setTypeface(signikaSemibold);

			LinearLayout btnRemove = (LinearLayout) findViewById(R.id.btnRemoveBlrAddress);
			((TextView) btnRemove.findViewById(R.id.txtRemove))
					.setTypeface(signikaSemibold);
			btnRemove.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					delete();
				}
			});

			LinearLayout btnEdit = (LinearLayout) findViewById(R.id.btnEditBlrAddress);
			((TextView) btnEdit.findViewById(R.id.txtEdit))
					.setTypeface(signikaSemibold);
			btnEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					update();
				}
			});

			setupMap();
		}
	}

	private void setupMap() {
		SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap));
		googleMap = mapFragment.getMap();
		if (googleMap != null) {
			addMarker(new LatLng(blrAddress.getLat(), blrAddress.getLng()));
		}
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

	private void delete() {
		addressDAO = new AddressDAO(getApplicationContext());

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.removeBusStop);
		alert.setMessage(R.string.msgRemoveBusStop);
		alert.setCancelable(false);
		alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				addressDAO.delete(blrAddress);
				onBackPressed();
			}
		});
		
		alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		alert.show();
	}
	
	private void update(){
		Intent intent = new Intent(getApplicationContext(),AddAddress.class);
		intent.putExtra("BLR_ADDRESS_ID", blrAddress.getId());
		startActivity(intent);
	}

}
