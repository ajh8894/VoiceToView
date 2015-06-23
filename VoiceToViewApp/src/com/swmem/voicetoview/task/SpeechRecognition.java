package com.swmem.voicetoview.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SpeechRecognition implements Callable<String> {
	private byte[] pcm;

	// Key obtained through Google Developer group
	private String api_key = "AIzaSyBgnC5fljMTmCFeilkgLsOKBvvnx6CBS0M";

	// URL for Google API
	private String root = "https://www.google.com/speech-api/full-duplex/v1/";
	private String up_p1 = "up?lang=ko_kr&lm=dictation&client=chromium&pair=";
	private String up_p2 = "&key=";
	
	// Variables used to establish return code
	private final long MIN = 10000000;
	private final long MAX = 900000009999999L;
	private long PAIR;
	
	public SpeechRecognition(byte[] pcm) {
		this.pcm = pcm;
	}
	
	
	@Override
	public String call() throws Exception {
		PAIR = MIN + (long) (Math.random() * ((MAX - MIN) + 1L));
		// InputStream in = null;
		int resCode = -1;
		
		// int http_status;
		try {
			URL url = new URL(root + up_p1 + PAIR + up_p2 + api_key);
			URLConnection urlConn = url.openConnection();

			if (!(urlConn instanceof HttpsURLConnection)) {
				throw new IOException("URL is not an Https URL");
			}

			HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setChunkedStreamingMode(0);
			httpConn.setRequestProperty("Content-Type", "audio/l16; rate=" + 16000);
			httpConn.connect();

			try {
				OutputStream out = httpConn.getOutputStream();

				Log.d("ParseStarter", "IO beg on data");
				out.write(pcm); 	
				Log.d("ParseStarter", "IO fin on data");

				resCode = httpConn.getResponseCode();

				Log.d("ParseStarter", "POST OK resp " + httpConn.getResponseMessage().getBytes().toString());

				if (resCode / 100 != 2) {
					Log.d("ParseStarter", "POST bad io ");
				}

			} catch (IOException e) {
				Log.d("ParseStarter", "FATAL " + e);
			}

			if (resCode == HttpsURLConnection.HTTP_OK) {
				Log.d("ParseStarter", "OK RESP to POST return scanner ");
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Response Code : " + resCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					//Ignore the first response, it's always empty
					Log.d("response", inputLine);
					try {
						JSONObject jsonObj = new JSONObject(inputLine);
						JSONObject result = (JSONObject) jsonObj.getJSONArray("result").get(0);
						JSONArray alternative = result.getJSONArray("alternative");
						JSONObject transcripts = (JSONObject) alternative.get(0);
						String transcript = transcripts.getString("transcript");
						
						/*Log.d("result", result.toString());
						Log.d("alternative", alternative.toString());
						Log.d("transcripts", transcripts.toString());
						Log.d("transcript", transcript);*/
						return transcript;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					response.append(inputLine);
				}
				in.close();
				System.out.println(response.toString());

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
