package temp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import android.provider.MediaStore.Files;
import android.util.Log;

public class Test extends Thread {
	String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2",
			strUrl = "https://www.google.com/speech-api/v2/recognize?output=json&lang=ko_kr&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&client=chromium&maxresults=6&pfilter=2";
	byte[] pcm;
	public Test(byte[] pcm) {
		this.pcm = pcm;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			sendPost();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Send post to google
	 */
	private void sendPost() throws Exception {
		byte[] mextrad = pcm;
		int resCode = -1;
		OutputStream out = null;
		// int http_status;
		URL url = new URL(strUrl);
		URLConnection urlConn = url.openConnection();
		HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
		try {


			if (!(urlConn instanceof HttpsURLConnection)) {
				throw new IOException("URL is not an Https URL");
			}

			
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setChunkedStreamingMode(0);
			httpConn.setRequestProperty("User-Agent", USER_AGENT);
			httpConn.setRequestProperty("Content-Type", "audio/l16; rate=16000");
			httpConn.connect();

			try {
				// this opens a connection, then sends POST & headers.
				out = httpConn.getOutputStream();
				// Note : if the audio is more than 15 seconds
				// dont write it to UrlConnInputStream all in one block as this
				// sample does.
				// Rather, segment the byteArray and on intermittently, sleeping
				// thread
				// supply bytes to the urlConn Stream at a rate that approaches
				// the bitrate ( =30K per sec. in this instance ).
				Log.d("ParseStarter", "IO beg on data");
				out.write(mextrad); // one big block supplied instantly to the
									// underlying chunker wont work for duration
									// > 15 s.
				Log.d("ParseStarter", "IO fin on data");
				// do you need the trailer?
				// NOW you can look at the status.
				resCode = httpConn.getResponseCode();

				Log.d("ParseStarter", "POST OK resp "
						+ httpConn.getResponseMessage().getBytes().toString());

				if (resCode / 100 != 2) {
					Log.d("ParseStarter", "POST bad io ");
				}

			} catch (IOException e) {
				Log.d("ParseStarter", "FATAL " + e);

			}
		    System.out.println("\nSending 'POST' request to URL : " + url);
		    System.out.println("Response Code : " + resCode);

		    BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		    String inputLine;
		    StringBuffer response = new StringBuffer();

		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }
		    in.close();

		    // print result
		    System.out.println(response.toString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
/*	    URL obj = new URL(url);
	    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

	    // add reuqest header
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.setRequestProperty("Content-Type", "audio/l16; rate=16000");
	    con.setRequestProperty("AcceptEncoding", "gzip,deflate,sdch");

	    // Send post request
	    
	    con.setDoOutput(true);
	    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	    wr.write(Files.readAllBytes(Paths.get("C:\\tmp\\test_sounds\\1_16000.wav")));
	    wr.flush();
	    wr.close();

	    int responseCode = con.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Response Code : " + responseCode);

	    BufferedReader in = new BufferedReader(new InputStreamReader(
	            con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();

	    // print result
	    System.out.println(response.toString());*/

	}
}
