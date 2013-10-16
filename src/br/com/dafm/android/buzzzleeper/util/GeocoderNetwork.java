package br.com.dafm.android.buzzzleeper.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class GeocoderNetwork {
	
	public String getAddress(Double lat, Double lng, Integer maxResults,
			Context context) {
		Geocoder geocoder = new Geocoder(context);
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(lat, lng, maxResults);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Address result = null;
		StringBuilder stringAddress = new StringBuilder();
		if (addresses != null && addresses.size() > 0) {
			result = addresses.get(0);

			for (int i = 0; i < result.getMaxAddressLineIndex(); i++) {
				if(i>0){
					stringAddress.append(", ");
				}
				stringAddress.append(result.getAddressLine(i));
			}
		}
		return stringAddress.toString();
	}
	
	/*

	public String getAddress(Double lat, Double lng, Integer maxResults,
			Context context) {
		
		final Double latitude = lat;
		final Double longitude = lng;
		final String address = null;
		
		new Thread(new Runnable() {
			public void run() {
		
				HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true");
		        HttpClient client = new DefaultHttpClient();
		        HttpResponse response;
		        StringBuilder stringBuilder = new StringBuilder();
		        JSONObject jsonObject = new JSONObject();
		
		        try {
		            response = client.execute(httpGet);
		            HttpEntity entity = response.getEntity();
		            InputStream stream = entity.getContent();
		            int b;
		            while ((b = stream.read()) != -1) {
		                stringBuilder.append((char) b);
		            }
		            
		            jsonObject = new JSONObject(stringBuilder.toString());
		            String address2 = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
					
				} catch (ClientProtocolException e) {
					new RuntimeException();
					e.printStackTrace();
				} catch (IOException e) {
					new RuntimeException();
					e.printStackTrace();			
				} catch (JSONException e) {
					new RuntimeException();
					e.printStackTrace();
				}
			}
		}).run();

		return address;
	}

	*/
	public String getImageUrl(String lat, String lon, String zoom, String width,String height) {
		StringBuilder url = new StringBuilder(300);
		url.append("http://maps.googleapis.com/maps/api/staticmap?");
		url.append("center=").append(lat).append(",").append(lon);
		url.append("&zoom=").append(zoom);
		url.append("&size=").append(width).append("x").append(height);
		url.append("&markers=size:tiny%7Ccolor:blue%7C").append(lat)
				.append(",").append(lon);
		url.append("&sensor=true");
		url.append("&key=AIzaSyB55oMJ_Ku4q_oWXkMxCWBY6yv6jRlTgp8");

		return url.toString();
	}

	public Address findAddress(String address, Integer limitResults,
			Context context) {
		Geocoder geocoder = new Geocoder(context);
		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocationName(address, limitResults);

		} catch (IOException e) {
			e.printStackTrace();
		}

		Address result = null;
		if (addresses != null && addresses.size() > 0) {
			result = addresses.get(0);
		}
		return result;
	}

}
