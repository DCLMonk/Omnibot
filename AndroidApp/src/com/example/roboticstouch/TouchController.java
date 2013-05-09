package com.example.roboticstouch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Most of this class is based of what was learned from replica island source.
 *
 */
public class TouchController {
	public static final int MAX_POINTS = 5;
	private boolean isMultitouch;
	private Point[] points;
	
	public TouchController(Context context) {
		supportsMultitouch(context);
		
		points = new Point[MAX_POINTS];
		for (int i = 0; i < MAX_POINTS; i++) {
			points[i] = new Point();
		}
	}

	public void updateTouch(MotionEvent event) {
		if (isMultitouch) {
		    final int pointerCount = event.getPointerCount();
		    for (int i = 0; i < pointerCount; i++) {
	            final int action = event.getAction();
	            final int actual = action & MotionEvent.ACTION_MASK;
	            final int id = event.getPointerId(i);
	            if (actual == MotionEvent.ACTION_POINTER_UP || 
                            actual == MotionEvent.ACTION_UP || 
                            actual == MotionEvent.ACTION_CANCEL) {
	            	unTouch(id);
	            } else {
	            	touch(id, event.getX(i), event.getY(i));
	            }
		    }
		} else {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				unTouch(0);
			} else {
				touch(0, event.getRawX(), event.getRawY());
			}
		}
	}
	
	public Point getPoint(int i) {
		return points[i];
	}

    private void touch(int i, float x, float y) {
		points[i].setAmount(x, y);
	}

	private void unTouch(int i) {
		points[i].reset();
	}

	public boolean supportsMultitouch(Context context) {
		if (context == null) {
			Log.e("TouchController", "Null Context!");
		}
        PackageManager packageManager = context.getPackageManager();
        isMultitouch = packageManager.hasSystemFeature("android.hardware.touchscreen.multitouch");

        return isMultitouch;
    }
}
