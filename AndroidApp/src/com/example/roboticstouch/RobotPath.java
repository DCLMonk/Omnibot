package com.example.roboticstouch;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

import com.example.roboticstouch.FingureTracker.Location;
import com.example.roboticstouch.control.RobotControl;

public class RobotPath extends Thread {
	private static final float mins = (float) (1 / 200.0);
	
	private class RLocation {
		public float x, y, rot;
		
		public RLocation(float x, float y, float rot) {
			this.x = x;
			this.y = y;
			this.rot = rot;
		}
		
		public void makeRel(RLocation l) {
			x -= l.x;
			y -= l.y;
			rot -= l.rot;
			while (rot > Math.PI) rot -= 2*Math.PI;
			while (rot < -Math.PI) rot += 2*Math.PI;
		}

		public void makeRel(float angle) {
			float sinTh = (float) Math.sin(angle);
			float cosTh = (float) Math.cos(angle);
			float nx = cosTh * x - sinTh * y;
			float ny = sinTh * x + cosTh * y;
			x = nx;
			y = ny;
		}
	}

	private ArrayList<RLocation> rlocs;
	private RobotControl controller;

	public RobotPath(List<Pair<Location, Location>> locs, RobotControl controller) {
		this.controller = controller;
		rlocs = new ArrayList<RLocation>();
		for (int i = 0; i < locs.size(); ++i) {
			Location l1 = locs.get(i).first;
			Location l2 = locs.get(i).second;
			float dx = -(l2.x - l1.x);
			float dy = l2.y - l1.y;
			float r = (float) Math.atan2(dy, dx);
			
			rlocs.add(new RLocation(-(l1.x + l2.x)/2, (l1.y + l2.y) / 2, r));
		}
		for (int i = rlocs.size() - 1; i > 0; --i) {
			rlocs.get(i).makeRel(rlocs.get(i - 1));
		}
		rlocs.remove(0);
		float angle = 0;
		for (int i = 0; i < rlocs.size(); ++i) {
			rlocs.get(i).makeRel(angle);
			angle -= rlocs.get(i).rot;
		}
	}
	
	@Override
	public void run() {
		for (int i = 0; i < rlocs.size(); ++i) {
			int delay = goTo(rlocs.get(i));
			try {
				sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		controller.setDirection(0, 0);
	}
	
	public int goTo(RLocation rloc) {
		//relative new x and y
		float x = rloc.x / 15;
		float y = rloc.y / 15;
		//relative rotation in radians
		float rot = rloc.rot;
		Log.e("ROBOT PATH", "L: " + x + " " + y + " Rot: " + rot);

		//finalant variables
		//Robot radius
		final float robot_rad = 4.75f;
		//Wheel radius;
		final float wheel_rad = 2.0f;
		//Maximum Robot Speed
		final float max_speed = 6.28f;

		float radius,d,fulld;

		// linear distance to next point
		d = (float) Math.sqrt(x*x + y*y);

		// radius of circle that the arc lies on
		radius = (float) Math.sqrt(d*d/(2*(1-Math.cos(rot))));

		// length of the arc
		fulld = radius*Math.abs(rot);

		//start_rot = rot/2;
		double final_angle = Math.atan2(y,x) + rot/2;

		// calculate the time to next state
		float time = fulld/max_speed;
		Log.e("ROBOT PATH", "FA: " + final_angle + " FullD:" + fulld + " D: " + d + " Rad: " + radius);
		double rob_rot = rot * robot_rad / (/*2 * */wheel_rad) / time;
		Log.e("ROBOT PATH", "Rotation: " + rob_rot);

		double tps1 = (Math.sin(final_angle)*max_speed * 1/2 + rob_rot) * (180/(0.9*Math.PI));
		double tps2 = (Math.sin(final_angle+RobotControl.off2)*max_speed * 1/2 + rob_rot) * (180/(0.9*Math.PI));
		double tps3 = (Math.sin(final_angle+RobotControl.off3)*max_speed * 1/2 + rob_rot) * (180/(0.9*Math.PI));
		double scaleDown = 1;
		if (tps1 > 250 * scaleDown) scaleDown = tps1 / 250f;
		if (tps2 > 250 * scaleDown) scaleDown = tps2 / 250f;
		if (tps3 > 250 * scaleDown) scaleDown = tps3 / 250f;
		if (tps1 < -250 * scaleDown) scaleDown = tps1 / -250f;
		if (tps2 < -250 * scaleDown) scaleDown = tps2 / -250f;
		if (tps3 < -250 * scaleDown) scaleDown = tps3 / -250f;
		if (scaleDown > 1) {
			Log.e("ROBOT PATH", "Scaledown: " + scaleDown);
			tps1 /= scaleDown;
			tps2 /= scaleDown;
			tps3 /= scaleDown;
			time *= scaleDown;
		}
		if (Math.abs(tps1) < mins) {
			if (tps1 > 0) {
				tps1 = mins;
			} else {
				tps1 = -mins;
			}
		}
		if (Math.abs(tps2) < mins) {
			if (tps2 > 0) {
				tps2 = mins;
			} else {
				tps2 = -mins;
			}
		}
		if (Math.abs(tps3) < mins) {
			if (tps3 > 0) {
				tps3 = mins;
			} else {
				tps3 = -mins;
			}
		}
		short delay1 = (short) (10000/tps1);
		short delay2 = (short) (10000/tps2);
		short delay3 = (short) (10000/tps3);
		
		controller.setMotorDelays(delay1, delay2, delay3);
		
		Log.e("ROBOT PATH", "Time: " + time + " Delays: " + delay1 + " " + delay2 + " " + delay3);
		
		return (int)(1000 * time);
	}

}
