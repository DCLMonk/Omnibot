package com.example.roboticstouch;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class TouchSurfaceHolder extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private CanvasRenderer renderer;

	public TouchSurfaceHolder(Context context, TouchController touchController) {
		super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

		renderer = new CanvasRenderer(null, touchController);
		renderer.start();
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
