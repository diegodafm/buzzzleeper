package br.com.dafm.android.buzzzleeper;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Diego Alisson on 8/16/13.
 */
public class AddAddress extends FragmentActivity  {
	
	private GoogleMap googleMap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title);
		setContentView(R.layout.add_address);
		
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap)).getMap();
        if (googleMap == null) {
            Toast.makeText(this, "Google Maps not available", 
                Toast.LENGTH_LONG).show();
        }
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
