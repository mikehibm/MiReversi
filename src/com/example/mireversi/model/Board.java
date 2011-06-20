package com.example.mireversi.model;

public class Board {
	
	public static final int COLS = 8;
	public static final int ROWS = 8;
	
	private int width;
	private int height;
	
	private Panel panels[][];
	
	public Board(){
		
		
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}

	
}
