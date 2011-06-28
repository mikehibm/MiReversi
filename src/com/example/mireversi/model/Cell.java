package com.example.mireversi.model;

public class Cell {
	
	public enum E_STATUS{
		None,
		Black,
		White
	}
	
	private float width;
	private float height;
	private int top;
	private int left;
	private E_STATUS status = E_STATUS.None;
	
	
	public void setWidth(float width) {
		this.width = width;
	}
	public float getWidth() {
		return width;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getHeight() {
		return height;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getTop() {
		return top;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getLeft() {
		return left;
	}
	
	public float getCx(){
		return (float) ((float)this.left + (float)this.width/2.0);
	}
	
	public float getCy(){
		return (float) ((float)this.top + (float)this.height/2.0);
	}
	
	public void setStatus(E_STATUS status) {
		this.status = status;
	}
	public E_STATUS getStatus() {
		return status;
	}

}
