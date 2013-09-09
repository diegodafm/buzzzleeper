package br.com.dafm.android.buzzzleeper.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.util.Log;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

public class ImageService {
	GeocoderNetwork geocoderNetwork;

	public static void downloadFile(String imageURL, String fileName) {

		try {
			File destFile = new File(fileName);
			URL u = new URL(imageURL);
			URLConnection conn = u.openConnection();
			int contentLength = conn.getContentLength();
			DataInputStream stream = new DataInputStream(u.openStream());
			byte[] buffer = new byte[contentLength];
			stream.readFully(buffer);
			stream.close();
			DataOutputStream fos = new DataOutputStream(new FileOutputStream(destFile));
			fos.write(buffer);
			fos.flush();
			fos.close();

		} catch (FileNotFoundException e) {
			Log.v("ERROR_DOWNLOADFILE", e.getMessage());
			return;
		} catch (IOException e) {
			Log.v("ERROR_DOWNLOADFILE", e.getMessage());
			return;
		}
	}

	public void createImageMap(BlrAddress blrAddress, Context context) {
		geocoderNetwork = new GeocoderNetwork();

		String imgUrl = geocoderNetwork.getImageUrl(blrAddress.getLat()
				.toString(), blrAddress.getLng().toString(), "13", "120", "80");
		String imgPath = context.getFilesDir() + "/" + "blrAddress_"
				+ blrAddress.getId() + ".png";
		downloadFile(imgUrl, imgPath);
	}
}
