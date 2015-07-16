package com.swmem.voicetoview.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Connection {
	public static String[] header;
	public static Socket socket;
	public static ObjectOutputStream oos;
	public static ObjectInputStream ois;

	synchronized public static void connect(int type) throws UnknownHostException, IOException {
		if (socket == null || !socket.isConnected() || socket.isClosed()) {
			socket = new Socket(Constants.CONNECT_SERVER_IP, Constants.CONNECT_SERVER_PORT);
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			Log.d("Connection connect()", "initConnection()");
			if(type == Constants.DISCONNECT) {
				header[0] = Constants.KIND_END;
			} 
			Connection.oos.writeObject(Connection.header);
			Connection.oos.flush();
		}
	}

	synchronized public static void disconnect() {
		Log.d("Connection connect()", "disconnect()");
		try {
			if (ois != null) {
				ois.close();
				Log.d("ObjectInputStream", "close");
			}
			if (oos != null) {
				oos.close();
				Log.d("ObjectOutputStream", "close");
			}
			if (socket != null) {
				socket.close();
				Log.d("Socket", "close");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ois = null;
			oos = null;
			socket = null;
		}
	}
}
