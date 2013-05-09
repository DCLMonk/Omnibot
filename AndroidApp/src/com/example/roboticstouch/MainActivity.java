package com.example.roboticstouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roboticstouch.control.BluetoothControl;

public class MainActivity extends Activity {
	public static final int IP_SET = 0;
	public static final int CUSTOM_IP = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.menu);

		Button fancy = (Button)findViewById(R.id.fancy);
		Button simple = (Button)findViewById(R.id.simple);

		fancy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TouchActivity.class);
				startActivity(intent);
			}
		});
		simple.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, com.example.roboticstouch.simple.TouchActivity.class);
				startActivity(intent);
			}
		});

		Button irl = (Button)findViewById(R.id.irl);
		Button tempest = (Button)findViewById(R.id.tempest);
		Button custom = (Button)findViewById(R.id.custom);

		irl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothControl.host = "130.111.219.73";
				Toast.makeText(getBaseContext(), "IP Set " + BluetoothControl.host + ":" + BluetoothControl.port, Toast.LENGTH_LONG).show();
				showDialog(IP_SET);
			}
		});
		tempest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothControl.host = "141.114.193.77";
				Toast.makeText(getBaseContext(), "IP Set " + BluetoothControl.host + ":" + BluetoothControl.port, Toast.LENGTH_LONG).show();
				showDialog(IP_SET);
			}
		});
		custom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(CUSTOM_IP);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case IP_SET:
			return new AlertDialog.Builder(this)
	        .setMessage("IP Set")
	        .setPositiveButton("Ok", null).create();
		case CUSTOM_IP:
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    // Get the layout inflater
		    LayoutInflater inflater = this.getLayoutInflater();
		    final View v = inflater.inflate(R.layout.custom_ip, null);
		    builder.setView(v)
		    // Add action buttons
		           .setPositiveButton("Set IP", new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   EditText host = (EditText)v.findViewById(R.id.host);
		            	   EditText port = (EditText)v.findViewById(R.id.port);
		            	   
		            	   BluetoothControl.host = host.getText() + "";
		            	   if (port.getText().length() > 0) {
		            		   try {
		            			   BluetoothControl.port = Integer.parseInt(port.getText() + "");
		            		   } catch (Exception e) {
		            			   BluetoothControl.port = 8023;
		            		   }
		            	   }
		            	   Toast.makeText(MainActivity.this, "IP Set " + BluetoothControl.host + ":" + BluetoothControl.port, Toast.LENGTH_LONG).show();
		               }
		           })
		           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   dismissDialog(CUSTOM_IP);
		               }
		           });
		    return builder.create();
		}
		return super.onCreateDialog(id);
	}
}
