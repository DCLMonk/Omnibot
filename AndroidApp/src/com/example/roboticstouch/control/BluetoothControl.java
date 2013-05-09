package com.example.roboticstouch.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothControl {
	public final static boolean useBluetooth = false;
	public static String host = "130.111.219.73";
	public static int port = 8023;

	private BluetoothAdapter adapter;
	private BufferedReader bufferedReader;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice device;
	private PrintStream out;
	
	private boolean connected;
	private Socket socket;

	public BluetoothControl(BluetoothAdapter adapter) {
		this.adapter = adapter;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void connect() {
		if (useBluetooth) {
			try {
				// hard coded the MAC of our bluetooth chip
				device = adapter.getRemoteDevice("00:19:5D:EE:08:05");
	
				// breaks out functions that are no longer broken out for our use
				// specifically connect
				Method m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
	
				// connect to the board
				bluetoothSocket.connect();

				out = new PrintStream(bluetoothSocket.getOutputStream());
				connected = true;
			} catch (Exception e) {
				e.printStackTrace();
				connected = false;
			}
			Log.e("BLUETOOTH", "Connected Bluetooth");
		} else {
			
			try {
				socket = new Socket(host, port);
				
				out = new PrintStream(socket.getOutputStream());
				Log.e("BLUETOOTH", "Connected Socket");
				connected = true;
			} catch (UnknownHostException e) {
				connected = false;
				e.printStackTrace();
			} catch (IOException e) {
				connected = false;
				e.printStackTrace();
			}
		}

	}

	public void disconnect() {
		connected = false;
		if (useBluetooth) {
			Log.e("BLUETOOTH", "Disconnecting Bluetooth");
	
			try {
				// close everything when done
				// closes bluetooth socket and input stream
	//			try {
	//				bluetoothSocket.getInputStream().close();
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
				try {
					bluetoothSocket.getOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.e("BLUETOOTH", "InputStream Closed Bluetooth");
	//			try {
	//				in.close();
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
				
				
				out.close();
	
				Log.e("BLUETOOTH", "Input Stopped Bluetooth");
				bluetoothSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("BLUETOOTH", "Disconnected Bluetooth");
		} else {
			Log.e("BLUETOOTH", "Disconnecting Socket");

			try {
				try {
					socket.getOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				out.close();

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("BLUETOOTH", "Disconnected Socket");
		}
	}

	public BufferedReader getInputStream() {
		return bufferedReader;
	}

	public PrintStream getOutputStream() {
		return out;
	}

	public BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}
}
