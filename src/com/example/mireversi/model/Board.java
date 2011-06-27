package com.example.mireversi.model;

public class Board {
	
	public static final int COLS = 8;
	public static final int ROWS = 8;
	
	private int width;
	private int height;
	
	private Panel panels[][] = new Panel[ROWS][COLS];
	
	public Board(){
	
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				panels[i][j] = new Panel();
			}
		}
		
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
