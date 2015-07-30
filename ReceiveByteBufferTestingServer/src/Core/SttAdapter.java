package Core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Server.Server;

import com.swmem.voicetoview.data.Model;

public class SttAdapter extends Thread {
	private long PAIR;
	private Model model;
	private static boolean version1=true;
	private static int fs=16000; 
	// Key obtained through Google Developer group
	public final String V1_API_KEY = "AIzaSyBgnC5fljMTmCFeilkgLsOKBvvnx6CBS0M";
	// URL for Google API
	public final String ROOT = "https://www.google.com/speech-api/full-duplex/v1/";
	public final String UP_P1 = "up?lang=ko_kr&lm=dictation&client=chromium&pair=";
	public final String UP_P2 = "&key=";
	
	//Google API v2
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
	public static final String URL = "https://www.google.com/speech-api/v2/recognize?output=json&lang=ko_kr&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&client=chromium&maxresults=6&pfilter=2";
	// Variables used to establish return code
	public final long MIN = 10000000;
	public final long MAX = 900000009999999L;

	public SttAdapter(Model model) {
		this.model = model;
	}

	@Override
	public void run() {
		System.out.println("STT요청시작 메시지번호 : "+model.getMessageNum());
		PAIR = MIN + (long) (Math.random() * ((MAX - MIN) + 1L));
		// InputStream in = null;
		int resCode = -1;

		// int http_status;
		try {
			HttpsURLConnection con;
			if(version1){
				URL url = new URL(ROOT + UP_P1 + PAIR + UP_P2 + V1_API_KEY);
				URLConnection urlConn = url.openConnection();

				if (!(urlConn instanceof HttpsURLConnection)) {
					throw new IOException("URL is not an Https URL");
				}

				con = (HttpsURLConnection) urlConn;
				con.setAllowUserInteraction(false);
				con.setInstanceFollowRedirects(true);
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setChunkedStreamingMode(0);
				con.setRequestProperty("Content-Type", "audio/l16; rate=" + fs);
				con.connect();
				
			}else{
				 URL obj = new URL(URL);
		         con = (HttpsURLConnection) obj.openConnection();
		         // add reuqest header
		         con.setRequestMethod("POST");
		         con.setRequestProperty("User-Agent", USER_AGENT);
		         con.setRequestProperty("Content-Type", "audio/l16; rate=" + fs);
		         // Send post request
		         con.setDoOutput(true);
			}
			try {
				OutputStream out = con.getOutputStream();
				//8000프레임으로 임의 조작후 전송
//				byte harfBuffers[] = new byte[model.getBuffers().length/2];
//				for(int i=0,j=1;i<(model.getBuffers().length/2)-1;i+=2,j+=2){
//					harfBuffers[i]=model.getBuffers()[i*2];
//					harfBuffers[j]=model.getBuffers()[j*2-1];
//				}
				out.write(model.getBuffers()); 
				out.flush();

				resCode = con.getResponseCode();

				if (resCode / 100 != 2) {
					System.out.println("POST bad io");
				}

			}catch (Exception e) {
			}

			if (resCode == HttpURLConnection.HTTP_OK) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				String stringReadLine;
				in.readLine();
				stringReadLine=in.readLine();
				// Ignore the first response, it's always empty
				//				System.out.println(stringReadLine);
				String textResult=null;;
				Double confidence=0.0;
				
				if(stringReadLine==null){
					textResult="X";
				}else{
					try {
						JSONArray jsonArr= (new JSONObject(stringReadLine)).getJSONArray("result"); //아무말안했을때 널포인터 이따가 예외처리
						jsonArr = (JSONArray) ((JSONObject) jsonArr.get(0)).get("alternative");
						textResult = (String) ((JSONObject) jsonArr.get(0)).get("transcript");
						confidence =  (Double) ((JSONObject) jsonArr.get(0)).get("confidence");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}
				}
				in.close();
				model.setTextResult(textResult);
				model.setConfidence(confidence);
				if(Server.test)	Server.sendTargetQueue(textResult,false);
				else Server.sendMessageToServer(model,false);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}