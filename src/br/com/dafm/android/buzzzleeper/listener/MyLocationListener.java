package br.com.dafm.android.buzzzleeper.listener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	
	private Context context;
	
	private IntentFilter filter;
	
	public MyLocationListener(Context context, IntentFilter filter) {
		super();
		this.context = context;
		this.filter = filter;
	}

	@Override
	public void onLocationChanged(Location loc) {
		Intent data = new Intent(this.filter.getAction(0));
		data.putExtra("location", loc);
		context.sendBroadcast(data);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}