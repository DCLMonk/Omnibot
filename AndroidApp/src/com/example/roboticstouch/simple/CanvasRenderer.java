package com.example.roboticstouch.simple;

import com.example.roboticstouch.control.RobotControl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class CanvasRenderer extends Thread {

	private SurfaceHolder holder;
	private Canvas canvas;

	public boolean mRun = true;
	private long delay;
	private TouchController touchController;
	private RobotControl control;
	private TextView tv;
	private TouchSurfaceHolder touchHolder;

	public CanvasRenderer(TouchSurfaceHolder touchSurfaceHolder, SurfaceHolder holder, TouchController touchController, RobotControl control) {
		super();
		this.holder = holder;
		this.delay = (long)(1000 / 60);
		this.touchController = touchController;
		this.control = control;
		this.touchHolder = touchSurfaceHolder;
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
		paint.setColor(Color.GREEN);
		canvas.drawCircle(size/2, size/2, size/2, paint);
		paint.setColor(Color.WHITE);
		canvas.drawRect(new Rect(0, size, size, size * 6 / 5), paint);
		paint.setColor(Color.RED);
		float x = -1;
		float y = -1;

		for (int i = 0; i < TouchController.MAX_POINTS; ++i) {
			Point point = touchController.getPoint(i);
			
			if (point.isSet()) {
				x = point.getX();
				y = point.getY();
				canvas.drawCircle((float)point.getX(), (float)point.getY(), 50, paint);
			}
		}
		if (x != -1) {
			if (y < size) {
				x -= size / 2;
				y -= size / 2;
				x *= -1; // maybe x
				final float dir = (float) (Math.atan2(y, x) + (Math.PI / 2));
				float dist = (float)Math.sqrt(x * x + y * y) / (size / 2);
				if (dist > 1) dist = 1;
				final float d = dist;
				touchHolder.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (tv != null) {
							tv.setText(control.isConnected() + " " + dir + " " + d);
						}
					}
				});
				control.setDirection(dir, dist);
			} else if (y < (1.2f * size)) {
				x -= size / 2;
				x /= size / 2;
				x *= -1; // maybe x
				if (Math.abs(x) < (50/20000f)) if (x > 0) x = (50/20000f); else x = -(50/20000f);
				short rot = (short) (50 / x);
				control.setMotorDelays(rot, rot, rot);
			}
		} else {
			touchHolder.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (tv != null) {
						tv.setText(control.isConnected() + " no touch");
					}
				}
			});
			control.setDirection(0, 0);
		}
	}

	public void setText(TextView tv) {
		this.tv = tv;
	}

	

}