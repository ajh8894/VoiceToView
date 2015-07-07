package com.swmem.voicetoview.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.swmem.voicetoview.data.Constants;

import android.util.Log;

public class SpeechRecognitionV1 implements Callable<String> {
	private byte[] pcm;
	private long PAIR;
	
	public SpeechRecognitionV1(byte[] pcm) {
		this.pcm = pcm;
	}
	
	@Override
	public String call() throws Exception {
		PAIR = Constants.MIN + (long) (Math.random() * ((Constants.MAX - Constants.MIN) + 1L));
		// InputStream in = null;
		int resCode = -1;
		
		// int http_status;
		try {
			URL url = new URL(Constants.ROOT + Constants.UP_P1 + PAIR + Constants.UP_P2 + Constants.V1_API_KEY);
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
				out.flush();
				Log.d("ParseStarter", "IO fin on data");

				resCode = httpConn.getResponseCode();

				Log.d("ParseStarter", "POST OK resp " + httpConn.getResponseMessage().getBytes().toString());

				if (resCode / 100 != 2) {
					Log.d("ParseStarter", "POST bad io ");
				}

			} catch (IOException e) {
				Log.d("ParseStarter", "FATAL " + e);
			}

			if (resCode == HttpURLConnection.HTTP_OK) {
				Log.d("ParseStarter", "OK RESP to POST return scanner ");
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Response Code : " + resCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String stringReadLine;
				
				while ((stringReadLine = in.readLine()) != null) {
					// Ignore the first response, it's always empty
					
					if (stringReadLine.length() > 20) {
						Log.d("JSON result: ", stringReadLine);
						try {
							JSONObject jsonObj = new JSONObject(stringReadLine);
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
					}
				}
				in.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
