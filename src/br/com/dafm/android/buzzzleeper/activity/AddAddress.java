package br.com.dafm.android.buzzzleeper.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.util.AndroidUtil;
import br.com.dafm.android.buzzzleeper.util.GPSTracker;
import br.com.dafm.android.buzzzleeper.util.GeocoderNetwork;
import br.com.dafm.android.buzzzleeper.util.ImageService;

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
	
	private Typeface signikaSemibold;

	private GeocoderNetwork geocoderNetwork;

	private ImageService imageService;

	private LatLng latLng;

	private GoogleMap googleMap;

	private SeekBar buffer;

	private AddressDAO addressDAO;

	private ArrayList<String> listErros;

	private BlrAddress blrAddress;
	
	private AndroidUtil util;
	
	private Boolean expandedMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_address);
		util = new AndroidUtil(this);
		setupFontFace();
		setupBtnBackHome();
		setupMap();
		setupBtnSearchAddress();
		setupBtnSearchByGPS();
		setupSpinnerRingtones();
		setupSeekBarBuffer();
		setupBtnSave();
		setupBtnCancel();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			addressDAO = new AddressDAO(getApplicationContext());
			String value = extras.get("BLR_ADDRESS_ID").toString();
			blrAddress = addressDAO.findById(Integer.parseInt(value));
			loadData();
		}else{
			blrAddress = new BlrAddress();			
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if(expandedMap){
			
			Float newHeight = util.convertPixelsToDp(getApplicationContext().getResources().getDisplayMetrics().heightPixels);
			//header
			newHeight -= util.convertPixelsToDp(50); 
			//infoMAP
			LinearLayout rlInfoMap = (LinearLayout) this.findViewById(R.id.rlInfoMap);
			rlInfoMap.setVisibility(0);
			newHeight -= util.convertPixelsToDp(rlInfoMap.getHeight());
			
			//header
			newHeight -= 100;
			
			LinearLayout containerMap = (LinearLayout) findViewById(R.id.llContainerMap);
			containerMap.setLayoutParams(new LinearLayout.LayoutParams(
					getApplicationContext().getResources().getDisplayMetrics().widthPixels,
					util.convertDpToPixel(newHeight).intValue()));
		}else{
			LinearLayout containerMap = (LinearLayout) findViewById(R.id.llContainerMap);
			if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				containerMap.setLayoutParams(new LinearLayout.LayoutParams(
						getApplicationContext().getResources().getDisplayMetrics().widthPixels,
						util.convertDpToPixel(100f).intValue()));			
			} else if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {				
				containerMap.setLayoutParams(new LinearLayout.LayoutParams(
						getApplicationContext().getResources().getDisplayMetrics().widthPixels,
						util.convertDpToPixel(150f).intValue()));
			}	
		}
	}

	private void setupFontFace() {
		this.signikaSemibold = Typeface.createFromAsset(getAssets(),"fonts/Signika-Semibold.ttf");
		
		EditText editText = null;
		
		TextView textView = null;
		
		editText =  (EditText) findViewById(R.id.txtAddName);
		editText.setTypeface(signikaSemibold);
		
		editText =  (EditText) findViewById(R.id.txtSearchAddress);
		editText.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtAddressLocation);
		textView.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtCoordinates);
		textView.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtRingtone);
		textView.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtBuffer);
		textView.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtBtnCancel);
		textView.setTypeface(signikaSemibold);
		
		textView =  (TextView) findViewById(R.id.txtBtnConfirm);
		textView.setTypeface(signikaSemibold);
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
		
		EditText searchAddress = (EditText) this.findViewById(R.id.txtSearchAddress);
		searchAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	findAddress();
		            return true;
		        }
		        return false;
		    }
		});
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

	private void setupBtnSearchByGPS() {
		RelativeLayout btnSearchByGPS = (RelativeLayout) findViewById(R.id.btnSearchGPS);
		btnSearchByGPS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GPSTracker gps = new GPSTracker(getApplicationContext());

				// check if GPS enabled
				if (gps.canGetLocation()) {

					LatLng point = new LatLng(gps.getLatitude(), gps.getLongitude());
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

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		EditText searchAddress = (EditText) this.findViewById(R.id.txtSearchAddress);
		imm.hideSoftInputFromWindow(searchAddress.getWindowToken(), 0);

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
			Toast toast = Toast.makeText(this, "ADDRESS NOT FOUND! ", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
			toast.show();
			Log.v(getLocalClassName(), "ADDRESS NOT FOUND!");
		}
	}

	private void setupMap() {

		SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
		googleMap = mapFragment.getMap();

		if (googleMap != null) {
			googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng point) {
					addMarker(point);
					CameraUpdate center = CameraUpdateFactory.newLatLng(point);
					googleMap.moveCamera(center);
				}
			});
			
			expandedMap = false;
			final ImageView btnMapExpand = (ImageView) findViewById(R.id.btnMapExpand);			
			btnMapExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(expandedMap){
						btnMapExpand.setImageResource(R.drawable.ic_map_expand);
					}else{
						btnMapExpand.setImageResource(R.drawable.ic_map_collapse);
					}
					expandedMapSettings();
				}
			});
		}
	}

	private void addMarker(LatLng point) {
		if (point != null) {
			Integer radius = buffer.getProgress();
			if(blrAddress.getBuffer() != null){
				radius = blrAddress.getBuffer();
			}

			googleMap.clear();
			CircleOptions circleOptions = new CircleOptions();
			circleOptions.center(point);
			circleOptions.radius(radius);
			circleOptions.fillColor(0x4058c2cb);
			circleOptions.strokeColor(Color.parseColor("#58c2cb"));
			circleOptions.strokeWidth(5);
			googleMap.addCircle(circleOptions);
			
			/*
			EditText name = (EditText) this.findViewById(R.id.txtAddName);
			SeekBar buffer = (SeekBar) this.findViewById(R.id.seekBuffer);
			String titleMarker = (String) (name.getText() != null ? name.getText() : getString(R.string.setName)); 
			googleMap.addMarker(new MarkerOptions()
				.title(titleMarker)
				.snippet( Integer.toString(buffer.getProgress()))
				.position(point));
			*/
			
			googleMap.addMarker(new MarkerOptions()
				.position(point));

			LinearLayout rlInfoMap = (LinearLayout) this.findViewById(R.id.rlInfoMap);
			rlInfoMap.setVisibility(0);

			TextView coordinates = (TextView) this.findViewById(R.id.txtCoordinates);
			
			StringBuilder coord = new StringBuilder();
			coord.append(getString(R.string.latitude));
			coord.append(" ").append(String.format("%.7f", point.latitude));
			coord.append(", ").append(getString(R.string.longitude));
			coord.append(" ").append(String.format("%.7f", point.longitude));
			coordinates.setText(coord.toString());

			TextView addressLocation = (TextView) this.findViewById(R.id.txtAddressLocation);
			addressLocation.setText(geocoderNetwork.getAddress(point.latitude,point.longitude, 1, getApplicationContext()));

			latLng = point;
		}
	}

	private void setupSpinnerRingtones() {
		final List<String> ringtones = this.getListRingtones(this);
		RelativeLayout icBtnRingtone = (RelativeLayout) this.findViewById(R.id.icBtnRingtone);
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
		final TextView txtRingtone = (TextView) this
				.findViewById(R.id.txtRingtone);
		builder.setTitle(R.string.setRingtone).setItems(ringtones.toArray(new CharSequence[ringtones.size()]),new DialogInterface.OnClickListener() {
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
			Ringtone ringtone = ringtoneManager.getRingtone(cursor.getPosition());
			list.add(ringtone.getTitle(context));
		}
		cursor.close();

		return list;
	}

	private void setupSeekBarBuffer() {

		buffer = (SeekBar) findViewById(R.id.seekBuffer);

		TextView textView = (TextView) findViewById(R.id.txtBuffer);
		textView.setText(getString(R.string.buffer) + ": " + Integer.toString(buffer.getProgress()) + " " + getString(R.string.meters));

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
				textView.setText(getString(R.string.buffer) + ": " + Integer.toString(progress) + " " + getString(R.string.meters));
			}
		});
	}

	private void setupBtnSave() {
		geocoderNetwork = new GeocoderNetwork();

		LinearLayout btnConfirm = (LinearLayout) findViewById(R.id.btnAddConfirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(blrAddress.getId() == null){
					save();
				}else{
					update();
				}
			}
		});
	}

	private void setupBtnCancel() {
		LinearLayout btnCancel = (LinearLayout) findViewById(R.id.btnAddCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
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

		if (blrAddress.getLat() == null || blrAddress.getLng() == null) {
			listErros.add(getString(R.string.positionCannotBeIsEmpty));
		}

		if (blrAddress.getBuffer() <= 0) {
			listErros.add(getString(R.string.bufferCannotBeIsEmpty));
		}

		if (blrAddress.getRingtone().equals("")
				|| blrAddress.getRingtone().equals(
						getString(R.string.setRingtone))) {
			listErros.add(getString(R.string.ringtoneCannotBeIsEmpty));
		}
		return (listErros.isEmpty() ? true : false);
	}

	private void updateValues() {

		EditText name = (EditText) this.findViewById(R.id.txtAddName);
		blrAddress.setName(name.getText().toString());

		TextView address = (TextView) this
				.findViewById(R.id.txtAddressLocation);
		blrAddress.setAddress(address.getText().toString());

		if (latLng != null) {
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
			final BlrAddress savedAddress = addressDAO.save(blrAddress);
			if (savedAddress != null && savedAddress.getId() != null) {

				new Thread(new Runnable() {
					public void run() {
						imageService = new ImageService();
						imageService.createImageMap(blrAddress,
								getApplicationContext());
					}
				}).start();

				AlertDialog alertDialog = new AlertDialog.Builder(this)
						.create();
				alertDialog.setTitle(getString(R.string.success));

				alertDialog.setMessage(getString(R.string.busStopAdded));
				alertDialog.setButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						onBackPressed();
					}
				});
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.show();
			}
		} else {

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
	


	private void update() {
		updateValues();

		if (validate()) {
			addressDAO = new AddressDAO(getApplicationContext());
			addressDAO.update(blrAddress);
			
			new Thread(new Runnable() {
				public void run() {
					imageService = new ImageService();
					imageService.createImageMap(blrAddress,
							getApplicationContext());
				}
			}).start();

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(getString(R.string.success));

			alertDialog.setMessage(getString(R.string.busStopUpdated));
			alertDialog.setButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					onBackPressed();
				}
			});
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.show();
		}
		
	}
	
	private void loadData(){
		
		if(blrAddress.getName() != null){
			EditText name = (EditText) this.findViewById(R.id.txtAddName);
			name.setText(blrAddress.getName());
		}
		
		if(blrAddress.getAddress() != null){
			TextView address = (TextView) this.findViewById(R.id.txtAddressLocation);
			address.setText(blrAddress.getAddress());
		}
		
		if(blrAddress.getBuffer() != null){
			buffer.setProgress(blrAddress.getBuffer());
		}
		
		if(blrAddress.getLat() != null && blrAddress.getLng() != null){
			LatLng point = new LatLng(blrAddress.getLat(), blrAddress.getLng());
			addMarker(point);
			
			CameraUpdate center = CameraUpdateFactory.newLatLng(point);
			googleMap.moveCamera(center);

			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			googleMap.animateCamera(zoom);
		}
		
		if(blrAddress.getRingtone() != null){
			TextView ringtone = (TextView) this.findViewById(R.id.txtRingtone);
			ringtone.setText(blrAddress.getRingtone());
		}
	}
	
	private void expandedMapSettings(){
		if(!expandedMap){
			
			Float newHeight = util.convertPixelsToDp(getApplicationContext().getResources().getDisplayMetrics().heightPixels);
			//header
			newHeight -= util.convertPixelsToDp(50); 
			//infoMAP
			LinearLayout rlInfoMap = (LinearLayout) this.findViewById(R.id.rlInfoMap);
			rlInfoMap.setVisibility(0);
			newHeight -= util.convertPixelsToDp(rlInfoMap.getHeight());
			
			//header
			newHeight -= 100;
			
			LinearLayout containerMap = (LinearLayout) findViewById(R.id.llContainerMap);
			containerMap.setLayoutParams(new LinearLayout.LayoutParams(
					getApplicationContext().getResources().getDisplayMetrics().widthPixels,
					util.convertDpToPixel(newHeight).intValue()));
			expandedMap = true;
		}else{
			LinearLayout containerMap = (LinearLayout) findViewById(R.id.llContainerMap);
			if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				containerMap.setLayoutParams(new LinearLayout.LayoutParams(
						getApplicationContext().getResources().getDisplayMetrics().widthPixels,
						util.convertDpToPixel(100f).intValue()));			
			} else if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {				
				containerMap.setLayoutParams(new LinearLayout.LayoutParams(
						getApplicationContext().getResources().getDisplayMetrics().widthPixels,
						util.convertDpToPixel(150f).intValue()));
			}	
			expandedMap= false;
		}
	}

}
