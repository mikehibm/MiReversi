package com.example.mireversi.model;

import android.graphics.Rect;
import android.graphics.RectF;

public class Cell {
	
	public enum E_STATUS{
		None,
		Black,
		White
	}
	
	private static final String TAG = "Cell";

	private RectF rect = new RectF();
	private E_STATUS status = E_STATUS.None;
	
	
	public static String statusToString(E_STATUS st){
		String s = "N";
		if (st == E_STATUS.Black){
			s = "B";
		} else if (st == E_STATUS.White){
			s = "W";
		}
		return s;
	}
	
	public static E_STATUS stringToStatus(String s){
		E_STATUS st  = E_STATUS.None;
		if (s.equals("B")){
			st = E_STATUS.Black;
		} else if (s.equals("W")){
			st = E_STATUS.White;
		}
		return st;
	}
	
	public void setRectF(RectF rect) {
		this.rect = rect;
	}
	public RectF getRectF(){
		return this.rect;
	}
	
	public void setWidth(float w) {
		this.rect.right = this.rect.left + w;
	}
	public float getWidth() {
		return this.rect.width();
	}
	public void setHeight(float h) {
		this.rect.bottom = this.rect.top + h;
	}
	public float getHeight() {
		return this.rect.height();
	}
	public void setTop(float top) {
		this.rect.top = top;
	}
	public float getTop() {
		return this.rect.top;
	}
	public void setLeft(float left) {
		this.rect.left = left;
	}
	public float getLeft() {
		return this.rect.left;
	}
	
	public Rect getRect(){
		Rect r = new Rect();
		this.rect.round(r);
		return r;
	}
	
	public float getCx(){
		return this.getRectF().centerX();
		//return this.left + this.width/2.0f;
	}
	
	public float getCy(){
		return this.getRectF().centerY();
		//return this.top + this.height/2.0f;
	}
	
	public void setStatus(E_STATUS status) {
		this.status = status;
	}
	public E_STATUS getStatus() {
		return status;
	}

	public void setStatusString(String status_str) {
		this.status = stringToStatus(status_str);
	}
	public String getStatusString() {
		return statusToString(this.status);
	}

}
