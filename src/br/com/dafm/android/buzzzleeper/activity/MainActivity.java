package br.com.dafm.android.buzzzleeper.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

public class MainActivity extends Activity {

	private AddressDAO addressDAO;

	private List<BlrAddress> addresses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupBtnAddAddress();
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
			
			TextView text = new TextView(getApplicationContext());
			text.setText(blrAddress.getName());
			list.addView(text);
			
			TextView address = new TextView(getApplicationContext());
			address.setText(blrAddress.getAddress());
			list.addView(address);
		}
	}

}
