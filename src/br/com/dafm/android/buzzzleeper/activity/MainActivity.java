package br.com.dafm.android.buzzzleeper.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.util.AndroidUtil;
import br.com.dafm.android.buzzzleeper.util.GeocoderNetwork;
import br.com.dafm.android.buzzzleeper.util.ImageService;

import com.google.inject.Inject;

public class MainActivity extends Activity {

	private AddressDAO addressDAO;

	private List<BlrAddress> addresses;
	
	private Typeface signikaSemibold;
	
	@Inject
	private AndroidUtil util;

	GeocoderNetwork geocoderNetwork;

	ImageService imageService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		util = new AndroidUtil(getApplicationContext());
		setupBtnAddAddress();
		setupFontFace();
		insertItemsTeste();
		getBusStopList();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		if (item.getItemId() == R.id.menu_settings) {
			ret = true;
		} else {
			ret = super.onOptionsItemSelected(item);
		}
		return ret;
	}
	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout list = (LinearLayout) findViewById(R.id.list);
		list.removeAllViews();
		getBusStopList();
	}

	private void setupBtnAddAddress() {
		RelativeLayout btnAddAddress = (RelativeLayout) findViewById(R.id.btnAddAddress);
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
		final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bus_stop_item, null);

		TextView txtName = (TextView) view.findViewById(R.id.blrName);
		txtName.setText(blrAddress.getName());
		txtName.setTypeface(this.signikaSemibold);

		TextView txtAddress = (TextView) view.findViewById(R.id.blrAddress);
		txtAddress.setText(blrAddress.getAddress());
		txtAddress.setTypeface(this.signikaSemibold);

		ImageView imgMap = (ImageView) view.findViewById(R.id.blrImgMap);
		String imgPath = getFilesDir() + "/" + "blrAddress_" + blrAddress.getId() + ".png";
		if (BitmapFactory.decodeFile(imgPath) == null) {
			new Thread(new Runnable() {
				public void run() {
					imageService = new ImageService();
					imageService.createImageMap(blrAddress, getApplicationContext());
				}
			}).start();
		} else {
			imgMap.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		}

		LinearLayout btnShowDetails = (LinearLayout) view.findViewById(R.id.btnShowDetails);
		btnShowDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),ShowAddress.class);
				intent.putExtra("BLR_ADDRESS_ID", blrAddress.getId());
				startActivity(intent);
			}
		});

		LinearLayout btnStartTracking = (LinearLayout) view.findViewById(R.id.btnStartActivity);
		btnStartTracking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(util.isGpsAvaliable()){
					Intent intent = new Intent(getApplicationContext(),TabTrackingActivity.class);
					intent.putExtra("BLR_ADDRESS_ID", blrAddress.getId());
					startActivity(intent);
				}else{
					buildAlertMessageNoGps();
				}
			}
		});

		return view;
	}
	
	private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to enable GPS?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                     startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
    }
	
	private void insertItemsTeste() {
		addressDAO = new AddressDAO(getApplicationContext());
		List<BlrAddress> list = addressDAO.getAllAddress();

		if (list.isEmpty()) {
		
			BlrAddress blrAddress = new BlrAddress();
			blrAddress.setName("Parents home");
			blrAddress.setAddress("Avenida Tropical, 2790, Tropical,32070-380, Contagem, MG - Brasil");
			blrAddress.setLat(-19.925606d);
			blrAddress.setLng(-44.123307d);
			blrAddress.setRingtone("Claro");
			blrAddress.setBuffer(300);
			addressDAO.save(blrAddress);
			
			blrAddress = new BlrAddress();
			blrAddress.setName("Home Sweet Home");
			blrAddress.setAddress("Rua Antonio da Silva, 71, Ing�, 32604-492, Betim, MG, Brasil");
			blrAddress.setLat(-19.858617d);
			blrAddress.setLng(-44.199308d);
			blrAddress.setRingtone("Pump It");
			blrAddress.setBuffer(300);
			addressDAO.save(blrAddress);
			
			blrAddress = new BlrAddress();
			blrAddress.setName("Puc S�o Gabriel");
			blrAddress.setAddress("R. Walter Ianni, 46-92 - Provid�ncia, Belo Horizonte - MG, 31980-110, Brazil");
			blrAddress.setLat(-19.950717d);
			blrAddress.setLng(-43.918408d);
			blrAddress.setRingtone("Claro");
			blrAddress.setBuffer(300);
			blrAddress.setStatus(true);
			addressDAO.save(blrAddress);
		
		}
	}
	
	private void setupFontFace() {
		this.signikaSemibold = Typeface.createFromAsset(getAssets(),"fonts/Signika-Semibold.ttf");
		TextView textView =  (TextView) findViewById(R.id.txtTitleMain);
		textView.setTypeface(this.signikaSemibold);
	}
}
