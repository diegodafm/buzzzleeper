package br.com.dafm.android.buzzzleeper.listener;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.activity.MainActivity;
import br.com.dafm.android.buzzzleeper.service.TrackingService;
import br.com.dafm.android.buzzzleeper.util.AndroidUtil;

public class MyLocationListener implements LocationListener {
	
	private Context context;
	
	private List<IntentFilter> filters;
	
	private AndroidUtil util;
	
	public MyLocationListener(Context context, List<IntentFilter> filters) {
		super();
		this.context = context;
		this.filters = filters;
		util = new AndroidUtil(this.context);
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
		context.stopService(new Intent(context, TrackingService.class));
		util.sendNotification(1, MainActivity.class,this.context.getString(R.string.gpsStopped), this.context.getString(R.string.msgAppStopedActivateGps));

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