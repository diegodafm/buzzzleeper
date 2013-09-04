package br.com.dafm.android.buzzzleeper.service;

import java.io.IOException;
import java.util.List;

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

	public String getImageUrl(String lat, String lon, String zoom, String size) {
		StringBuilder url = new StringBuilder(300);
		url.append("http://maps.google.com/maps/api/staticmap?");
		url.append("center=").append(lat).append(",").append(lon);
		url.append("&zoom=").append(zoom);
		url.append("&size=").append(size).append("x").append(size);
		url.append("&markers=size:tiny%7Ccolor:blue%7C").append(lat)
				.append(",").append(lon);
		url.append("&sensor=false");

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
