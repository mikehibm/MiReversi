package com.example.mireversi.model;

import android.graphics.Rect;
import android.graphics.RectF;

public class Cell {
	
	public enum E_STATUS{
		None,
		Black,
		White
	}
	
	private RectF rect = new RectF();
	private E_STATUS status = E_STATUS.None;
	
	
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

}
