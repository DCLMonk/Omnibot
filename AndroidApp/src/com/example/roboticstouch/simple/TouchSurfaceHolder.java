package com.example.roboticstouch.simple;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.roboticstouch.control.RobotControl;


public class TouchSurfaceHolder extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private CanvasRenderer renderer;
	private TouchActivity activity;

	public TouchSurfaceHolder(TouchActivity context, TouchController touchController, RobotControl control) {
		super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        this.activity = context;

		renderer = new CanvasRenderer(this, null, touchController, control);
		renderer.start();
	}
	
	public TouchActivity getActivity() {
		return activity;
	}
	
	public CanvasRenderer getRenderer() {
		return renderer;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
    	renderer.setHolder(holder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
    	renderer.setHolder(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
    	renderer.setHolder(null);
	}
	
}
