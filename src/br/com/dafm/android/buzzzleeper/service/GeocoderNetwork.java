package br.com.dafm.android.buzzzleeper.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
				stringAddress.append(result.getAddressLine(i)).append(", ");
			}
		}
		return stringAddress.toString();
	}

	public Bitmap getImage(String lat, String lon, String zoom, String size) {
		try {
			StringBuilder url = new StringBuilder(300);
			url.append("http://maps.google.com/maps/api/staticmap?");
			url.append("center=").append(lat).append(",").append(lon);
			url.append("&zoom=").append(zoom);
			url.append("&size=").append(size).append("x").append(size);
			url.append("&markers=size:tiny%7Ccolor:blue%7C").append(lat)
					.append(",").append(lon);
			url.append("&sensor=false");

			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					url.toString()).getContent());
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();

			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = 5;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			Bitmap outerBm = Bitmap.createBitmap(rect.width(), rect.height(),
					Bitmap.Config.ARGB_8888);
			Canvas outerCanvas = new Canvas(outerBm);
			Paint shadowPaint = new Paint();
			shadowPaint.setShadowLayer(12, 12, 12, 0xFF555555);
			shadowPaint.setColor(0xFF555555);
			outerCanvas.drawBitmap(output, 0, 0, shadowPaint);

			return output;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
