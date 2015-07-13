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

	synchronized public static void init(int mode) throws UnknownHostException, IOException {
		if (socket == null || !socket.isConnected() || socket.isClosed()) {
			socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			Log.d("Connection Init", "initConnection()");
			if(mode == Constants.DISCONNECT) {
				header[0] = Constants.KIND_END;
			}
			Connection.oos.writeObject(Connection.header);
			Connection.oos.flush();
		}
	}

	synchronized public static void close() {
		try {
			Log.d("Connection Close", "closeConnection()");
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
