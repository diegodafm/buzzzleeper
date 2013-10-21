package br.com.dafm.android.buzzzleeper.listener;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
	
	private Context context;
	
	private List<IntentFilter> filters;
	
	public MyLocationListener(Context context, List<IntentFilter> filters) {
		super();
		this.context = context;
		this.filters = filters;
	}

	@Override
	public void onLocationChanged(Location loc) {
		if(!this.filters.isEmpty()){
			for (IntentFilter filter : this.filters) {				
				Intent data = new Intent(filter.getAction(0));
				data.putExtra("location", loc);
				context.sendBroadcast(data);			
			}
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.v("MYLOCATION", "onProviderDisabled");
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.v("MYLOCATION", "onProviderEnabled");
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.v("MYLOCATION", "onStatusChanged");
		// TODO Auto-generated method stub

	}

}