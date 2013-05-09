package com.example.roboticstouch.control;

import java.io.IOException;

import android.util.Log;

public class RobotControl extends Thread {
	public static final float off2 = (float) (240 * Math.PI / 180);
	public static final float off3 = (float) (120 * Math.PI / 180);
	private static final float mins = (float) (1 / 200.0);
	
	public BluetoothControl bluetooth;
	private boolean sendRequest;
	private byte[] send;
	
	public RobotControl(BluetoothControl bluetooth) {
		this.bluetooth = bluetooth;
		start();
	}
	
	@Override
	public void run() {
		int i = 0;
		while (!isConnected() && (++i < 100)) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.e("CONTROL THREAD", "READY TO SEND");
		while (isConnected()) {
			if (sendRequest) {
				try {
					bluetooth.getOutputStream().write(send);
					bluetooth.getOutputStream().flush();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("ROBOT", "Transmit Failed");
				}
				sendRequest = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.e("CONTROL THREAD", "SHUTTING DOWN");
	}
	
	public void setDirection(float angle, float mag) {
		float speed1 = (float) (Math.sin(angle) * mag);
		float speed2 = (float) (Math.sin(angle + off2) * mag);
		float speed3 = (float) (Math.sin(angle + off3) * mag);

		if (Math.abs(speed1) < mins) {
			if (speed1 > 0) {
				speed1 = mins;
			} else {
				speed1 = -mins;
			}
		}
		if (Math.abs(speed2) < mins) {
			if (speed2 > 0) {
				speed2 = mins;
			} else {
				speed2 = -mins;
			}
		}
		if (Math.abs(speed3) < mins) {
			if (speed3 > 0) {
				speed3 = mins;
			} else {
				speed3 = -mins;
			}
		}
		setMotorSpeeds(speed1, speed2, speed3);
	}
	
	public void setMotorSpeeds(float speed1, float speed2, float speed3) {
		short s1 = (short)(40 / speed1 + .5f);
		short s2 = (short)(40 / speed2 + .5f);
		short s3 = (short)(40 / speed3 + .5f);
		setMotorDelays(s1, s2, s3);
	}
	
	public void setMotorDelays(short s1, short s2, short s3) {
		byte[] send = new byte[6];
		send[1] = (byte)((s1 >> 8) & 0xff);
		send[0] = (byte)((s1) & 0xff);
		send[3] = (byte)((s2 >> 8) & 0xff);
		send[2] = (byte)((s2) & 0xff);
		send[5] = (byte)((s3 >> 8) & 0xff);
		send[4] = (byte)((s3) & 0xff);
		
		if (!sendRequest) {
			this.send = send;
			sendRequest = true;
		}
	}

	public boolean isConnected() {
		return bluetooth.isConnected();
	}
}
