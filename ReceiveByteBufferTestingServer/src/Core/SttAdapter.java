package Core;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.swmem.voicetoview.data.Model;

import Server.Server;

public class SttAdapter extends Thread {
	private Model model;
	private static int fs=16000; 

	//Google API v2
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2";
	//public static final String KEY = "AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw"; // master key
	
	//public static final String KEY = "AIzaSyAcalCzUvPmmJ7CZBFOEWx2Z1ZSn4Vs1gg";
	//public static final String KEY = "AIzaSyCnl6MRydhw_5fLXIdASxkLJzcJh5iX0M4";
	public static final String KEY = "AIzaSyDVg6hJDJXIWvz-CdylnoCHNN4PaLx3aoc"; // my key
	public static final String URL = "https://www.google.com/speech-api/v2/recognize?output=json&lang=ko_kr&key=" + KEY + "&client=chromium&maxresults=6&pfilter=2";
	
	public SttAdapter(Model model) {
		this.model = model;
	}

	@Override
	public void run() {
		System.out.println("STT요청시작 메시지번호 : "+ model.getMessageNum());
		// InputStream in = null;
		int resCode = -1;

		// int http_status;
		try {
			HttpsURLConnection con;

			URL obj = new URL(URL);
			con = (HttpsURLConnection) obj.openConnection();
			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-Type", "audio/l16; rate=" + fs);
			// Send post request
			con.setDoOutput(true);


			OutputStream out=null;

			out= con.getOutputStream();
			//8000프레임으로 임의 조작후 전송
//			byte harfBuffers[] = new byte[model.getBuffers().length/2];
//			for(int i=0,j=1;i<(model.getBuffers().length/2)-1;i+=2,j+=2){
//				harfBuffers[i]=model.getBuffers()[i*2];
//				harfBuffers[j]=model.getBuffers()[j*2-1];
//			}
			
			
			out.write(model.getBuffers()); 
			out.flush();

			resCode = con.getResponseCode();

			if (resCode / 100 != 2) {
				System.out.println("POST bad io");
			}
			out.close();
			
			if (resCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				String stringReadLine;
				in.readLine();
				stringReadLine=in.readLine();
				// Ignore the first response, it's always empty
				//				System.out.println(stringReadLine);
				String textResult=null;;
				double confidence=0.0;

				if(stringReadLine==null){
					textResult="X";
				}else{
					try {
						JSONArray jsonArr= (new JSONObject(stringReadLine)).getJSONArray("result"); //아무말안했을때 널포인터 이따가 예외처리
						jsonArr = (JSONArray) ((JSONObject) jsonArr.get(0)).get("alternative");
						textResult = (String) ((JSONObject) jsonArr.get(0)).get("transcript");
						confidence =  (double) ((JSONObject) jsonArr.get(0)).get("confidence");
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				}
				in.close();
				//con.disconnect();
				model.setTextResult(textResult);
				model.setConfidence(confidence);
				Server.sendMessageToServer(model,1);
			}else{
				System.out.println("STT HTTP Connection 실패!!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.setTextResult("X");
			Server.sendMessageToServer(this.model,1);
		}
	}
}