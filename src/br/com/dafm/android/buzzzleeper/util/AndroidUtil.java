package br.com.dafm.android.buzzzleeper.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import br.com.dafm.android.buzzzleeper.BuzzzleeperApplication;
import br.com.dafm.android.buzzzleeper.R;

import com.google.inject.Singleton;

@Singleton
public class AndroidUtil {
	
	private LocationManager locationManager;
	private ConnectivityManager connectivityManager;
	private BuzzzleeperApplication buzzzleeperApplication;
	private NotificationManager notificationManager;
	private Context context;
	
	public AndroidUtil (Context context){
		this.context = context;
	}

	public boolean isGpsAvaliable() {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public boolean isNetworkAvailable() {
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null &&  activeNetworkInfo.isConnected());
	}
		
	public Integer getBatteryLevel(){
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent battery = buzzzleeperApplication.getApplicationContext().registerReceiver(null, ifilter);
		
		int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = (level / (float)scale) * 100;
		
		return (int) batteryPct;
	}
	
	public void sendNotification(int id, Class<?> cls, String title, String message) {
		Intent notificationIntent = new Intent(buzzzleeperApplication, cls);
		notificationIntent.putExtra("ID",id);
		notificationIntent.putExtra("TITLE", title);
		notificationIntent.putExtra("MESSAGE", message);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent contentIntent = null;
		if(BroadcastReceiver.class.isAssignableFrom(cls)) {
			contentIntent = PendingIntent.getBroadcast(buzzzleeperApplication, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		} else if(Activity.class.isAssignableFrom(cls)) {
			contentIntent = PendingIntent.getActivity(buzzzleeperApplication, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		} else if(Service.class.isAssignableFrom(cls)) {
			contentIntent = PendingIntent.getService(buzzzleeperApplication, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		}

		Notification notification = new Notification(R.drawable.ic_zzz_notification, title, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(buzzzleeperApplication, title, message, contentIntent);
		notificationManager.notify(id, notification);
	}
	
	public void sendNotification(int id, String action, String title, String message) {
		Intent notificationIntent = new Intent(action);
		notificationIntent.putExtra("ID", id);
		notificationIntent.putExtra("TITLE", title);
		notificationIntent.putExtra("MESSAGE", message);

		PendingIntent contentIntent = PendingIntent.getActivity(buzzzleeperApplication, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification = new Notification(R.drawable.ic_zzz_notification, title, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(buzzzleeperApplication, title, message, contentIntent);
		notificationManager.notify(id, notification);
	}

	public boolean turnGPSOn() {
	    String provider = Settings.Secure.getString(buzzzleeperApplication.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        buzzzleeperApplication.sendBroadcast(poke);
	        return true;
	    }
	    return false;
	}
/*
	public boolean turnGPSOff() {
	    String provider = Settings.Secure.getString(buzzzleeperApplication.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(provider.contains("gps")) { //if gps is enabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        buzzzleeperApplication.sendBroadcast(poke);
	        return true;
	    }
	    return false;
	}
*/
}
