package Test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("CHANGE", action);
		
		//IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		//intentFilter.addAction("");
		//registerReceiver(networkStateReceiver, intentFilter);
		//unregisterReceiver(networkStateReceiver);
		
		if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			//Toast.makeText(context, "CHANGE", 10);
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
			
			System.out.println("---------------------------------------------------------------");
			System.out.println(activeNetwork.getType());
			System.out.println(activeNetwork.getTypeName());
			System.out.println(activeNetwork.getSubtype());
			System.out.println(activeNetwork.getSubtypeName());
			System.out.println("---------------------------------------------------------------");
		    NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

	        if(mobile != null) 
	        	Log.d("mobile", "Available: " + mobile.isAvailable() + " Connected: " + mobile.isConnected());
	        if(wifi != null)
	        	Log.d("wifi", "Available: " + wifi.isAvailable() + " Connected: " + wifi.isConnected());
		}
	}
}
