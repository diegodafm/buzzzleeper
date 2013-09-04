package br.com.dafm.android.buzzzleeper.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class ImageService {
	public static void downloadFile(String imageURL, String fileName)
			throws IOException {
		URL url = new URL("http://imgsapp.mg.superesportes.com.br/portlet/98/20100416170033755582o.png");
		File file = new File(fileName);
		long startTime = System.currentTimeMillis();
		Log.d("DownloadFile", "Begin Download URL: " + url + " Filename: " + fileName);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		//set up some things on the connection
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true); 
		//and connect!
		urlConnection.connect();
		
		
		InputStream is = urlConnection.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while ((current = bis.read()) != -1)
			baf.append((byte) current);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(baf.toByteArray());
		fos.close();
		Log.d("DownloadFile",
				"File was downloaded in: "
						+ ((System.currentTimeMillis() - startTime) / 1000)
						+ "s");
	}
}
