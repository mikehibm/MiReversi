package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.graphics.RectF;

public class Cell {
	
	public enum E_STATUS{
		None,
		Black,
		White
	}
	
	private static final String TAG = "Cell";

	private Board mBoard;
	private RectF rect = new RectF();
	private E_STATUS status = E_STATUS.None;
	private int x;
	private int y;
	
	public Cell(Board board, int x, int y){
		this.mBoard = board;
		this.x = x;
		this.y = y;
	}
	
	
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
	
	public E_STATUS getOpponentStatus(E_STATUS turn){
		if (turn == E_STATUS.Black){
			return E_STATUS.White;
		} else if (turn == E_STATUS.White){
			return E_STATUS.Black;
		} else {
			return E_STATUS.None;
		}
	}

	public ArrayList<Cell> getReversibleCells(E_STATUS turn){
		ArrayList<Cell> list = null;
		ArrayList<Cell> listTotal = new ArrayList<Cell>();
		
		if (this.getStatus() != E_STATUS.None){
			return listTotal;
		}

		int n = 0;
		for (int i=-1; i<=1; i++){
			for (int j=-1; j<=1; j++){
				if (i != 0 || j != 0){
					list = new ArrayList<Cell>();
					getCellsInLine(j, i, turn, list);
					n = list.size();
					if (n > 0){
						Cell cell = getCell(j * (n+1), i * (n+1));
						if (cell != null){
							if (cell.getStatus() == getOpponentStatus(turn)){
								listTotal.addAll(list);
							}
						}
					}
				}
			}
		}
		
		return listTotal;
	}
	
	private void getCellsInLine(int dx, int dy, E_STATUS turn, ArrayList<Cell> list){
		Cell cell = getCell(dx, dy);
		if (cell == null) return;
		
		if (cell.getStatus() == turn){
			list.add(cell);
			cell.getCellsInLine(dx, dy, turn, list);
		}
	}
	
//	private int countLine(int dx, int dy, E_STATUS turn){
//		Cell cell = getCell(dx, dy);
//		if (cell == null) return 0;
//		
//		if (cell.getStatus() == turn){
//			return cell.countLine(dx, dy, turn) + 1;
//		}
//
//		return 0;
//	}
	
	private Cell getCell(int offx, int offy){
		int px = this.x + offx;
		int py = this.y + offy;
		
		if (px < 0 || px >= Board.COLS){
			return null;
		}
		if (py < 0 || py >= Board.ROWS){
			return null;
		}
		
		Cell[][] cells = mBoard.getCells();
		return cells[py][px];
	}
}
