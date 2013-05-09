package com.example.roboticstouch;



public class Point {
	public float x, y;
	public boolean isTouching;
	
	public Point() {
		super();
	}
	
	public void setAmount(float x, float y) {
		this.x = x;
		this.y = y;
		isTouching = true;
	}
	
	public boolean isSet() {
		return isTouching;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void reset() {
		x = 0;
		y = 0;
		isTouching = false;
	}

}
