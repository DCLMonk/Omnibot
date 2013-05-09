package com.example.roboticstouch;

import com.example.roboticstouch.control.BluetoothControl;
import com.example.roboticstouch.control.RobotControl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class TouchActivity extends Activity {
	public TouchController touchController;

	private TouchSurfaceHolder surface;

	private BluetoothControl blueToothControl;

	public static RobotControl robotControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_touch);
		
		touchController = new TouchController(this);
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		blueToothControl = new BluetoothControl(adapter);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				blueToothControl.connect();
			}
		})).start();

		robotControl = new RobotControl(blueToothControl);
		surface = new TouchSurfaceHolder(this, touchController);
		FrameLayout preview = (FrameLayout) findViewById(R.id.frame);

		preview.addView(surface);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!blueToothControl.isConnected()) {
			(new Thread(new Runnable() {

				@Override
				public void run() {
					blueToothControl.connect();
				}
			})).start();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		surface.getRenderer().mRun = false;
		(new Thread(new Runnable() {
			@Override
			public void run() {
				if (blueToothControl.isConnected()) {
					blueToothControl.disconnect();
					blueToothControl = null;
				}
			}
		})).start();
		surface.getRenderer().mRun = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_touch, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchController.updateTouch(event);

		return true;
	}

}