package com.example.roboticstouch;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;



public class FingureTracker extends Thread {
	public static final float THRESH = 25;
	
	private CanvasRenderer renderer;
	private long delay;
	private TouchController touchcontroller;
	
	private boolean isupdating;
	
	private List<Pair<Location, Location>> locs;

	private RobotPath path;
	
	public class Location {
		public float x, y;
		
		public Location(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public double dist(Location first) {
			float dx = first.x - x;
			float dy = first.y - y;
			return Math.sqrt(dx * dx + dy * dy);
		}
	}

	public FingureTracker(CanvasRenderer renderer, TouchController touchcontroller, float samplerate) {
		this.renderer = renderer;
		this.touchcontroller = touchcontroller;
		this.delay = (long)(1000 / samplerate);
		locs = new ArrayList<Pair<Location, Location>>();
	}
	
	@Override
	public void run() {
		long lasttime;
		while (renderer.mRun) {
			lasttime = System.currentTimeMillis();
			
			updatelocations();
			
			long diff = delay - (System.currentTimeMillis() - lasttime);
			try {
				if (diff > 0) {
					sleep(delay);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updatelocations() {
		int numactive = 0;
		Location l1 = null;
		Location l2 = null;
		Point point;
		for (int i = 0; i < TouchController.MAX_POINTS; ++i) {
			point = touchcontroller.getPoint(i);
			if (point.isSet()) {
				++numactive;
				if (l1 == null) {
					l1 = new Location(point.getX(), point.getY());
				} else {
					l2 = new Location(point.getX(), point.getY());
				}
			}
		}
		synchronized (locs) {
			if (isupdating) {
				if (numactive != 2) {
					isupdating = false;
					path = new RobotPath(locs, TouchActivity.robotControl);
					path.start();
				} else {
					if (Math.max(l1.dist(locs.get(locs.size() - 1).first), l2.dist(locs.get(locs.size() - 1).second)) > THRESH) {
						locs.add(new Pair<Location, Location>(l1, l2));
					}
				}
			} else {
				if (numactive == 2) {
					locs.clear();
					locs.add(new Pair<Location, Location>(l1, l2));
					isupdating = true;
				}
			}
		}
	}

	public void drawLines(Canvas canvas) {
		synchronized (locs) {
			for (int i = 1; i < locs.size(); i++) {
				drawline(canvas, locs.get(i - 1).first, locs.get(i).first);
				drawline(canvas, locs.get(i - 1).second, locs.get(i).second);
			}
		}
	}

	private void drawline(Canvas canvas, Location first, Location second) {
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		p.setStrokeWidth(3);
		canvas.drawLine(first.x, first.y, second.x, second.y, p);
	}
}
