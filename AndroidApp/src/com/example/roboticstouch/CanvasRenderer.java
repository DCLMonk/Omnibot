package com.example.roboticstouch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class CanvasRenderer extends Thread {

	private SurfaceHolder holder;
	private Canvas canvas;

	public boolean mRun = true;
	private long delay;
	private TouchController touchController;
	private FingureTracker fingureTracker;

	public CanvasRenderer(SurfaceHolder holder, TouchController touchController) {
		super();
		this.holder = holder;
		this.delay = (long)(1000 / 60);
		this.touchController = touchController;
		fingureTracker = new FingureTracker(this, touchController, 4);
		fingureTracker.start();
	}

	public void setHolder(SurfaceHolder holder) {
		this.holder = holder;
	}
	
	@Override
	public void run() {
		long lastTime;
		while (mRun) {
			lastTime = System.currentTimeMillis();
			if (holder != null) {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					drawFrame();
					holder.unlockCanvasAndPost(canvas);
				}
			}
			
			long diff = delay - (System.currentTimeMillis() - lastTime);
			try {
				if (diff > 0) {
					sleep(delay);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void drawFrame() {
		int size = Math.min(canvas.getWidth(), canvas.getHeight());
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
		paint.setColor(Color.RED);
		for (int i = 0; i < TouchController.MAX_POINTS; ++i) {
			Point point = touchController.getPoint(i);
			
			if (point.isSet()) {
				canvas.drawCircle((float)point.getX(), (float)point.getY(), 50, paint);
			}
		}
		fingureTracker.drawLines(canvas);
	}

	

}