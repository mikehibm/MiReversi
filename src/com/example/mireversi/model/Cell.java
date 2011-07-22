package com.example.mireversi.model;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class Cell {
	
	public enum E_STATUS{
		None,
		Black,
		White
	}
	
	private Board mBoard;
	private E_STATUS status = E_STATUS.None;
	private RectF rect = new RectF();
	private Point point = new Point();
	
	//このセルにコマを置くと裏返しの対象になるセルのリスト
	ArrayList<Cell> mReversibleCells = new ArrayList<Cell>();
	
	//このセルにコマを置いた場合の局面の評価値
	private int mEval;
	
	
	public Cell(Board board, Point point){
		this.mBoard = board;
		this.point = point;
	}
	
	
	public static String statusToDisplay(E_STATUS st){
		String s = "None";
		if (st == E_STATUS.Black){
			s = "Black";
		} else if (st == E_STATUS.White){
			s = "White";
		}
		return s;
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
	
	public Point getPoint(){
		return this.point;
	}
	
	public int getRow(){
		return this.point.y;
	}
	
	public int getCol(){
		return this.point.x;
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
	
	public void setEval(int mEval) {
		this.mEval = mEval;
	}


	public int getEval() {
		return mEval;
	}


	public boolean isBlank(){
		return (this.status == E_STATUS.None);
	}

	public void setStatusString(String status_str) {
		this.status = stringToStatus(status_str);
	}
	public String getStatusString() {
		return statusToString(this.status);
	}
	
	public static E_STATUS getOpponentStatus(E_STATUS turn){
		if (turn == E_STATUS.Black){
			return E_STATUS.White;
		} else if (turn == E_STATUS.White){
			return E_STATUS.Black;
		} else {
			return E_STATUS.None;
		}
	}

	public ArrayList<Cell> getReversibleCells(){
		return mReversibleCells;
	}
	
	public void setReversibleCells(E_STATUS current){
		mReversibleCells.clear();
		
		if (this.getStatus() != E_STATUS.None){
			return;
		}

		E_STATUS opponent = getOpponentStatus(current);

		for (int i=-1; i<=1; i++){
			for (int j=-1; j<=1; j++){
				if (i != 0 || j != 0){
					ArrayList<Cell> list = new ArrayList<Cell>();
					int n = getCellsInLine(j, i, opponent, list);
					if (n > 0){
						Cell cell = getNextCell(j * (n+1), i * (n+1));
						if (cell != null){
							if (cell.getStatus() == current){
								mReversibleCells.addAll(list);
							}
						}
					}
				}
			}
		}
	}
	
	private int getCellsInLine(int dx, int dy, E_STATUS turn, ArrayList<Cell> list){
		Cell cell = getNextCell(dx, dy);
		if (cell != null){
			if (cell.getStatus() == turn){
				list.add(cell);
				cell.getCellsInLine(dx, dy, turn, list);
			}
		}
		return list.size();
	}
	
	private Cell getNextCell(int offx, int offy){
		int px = this.point.x + offx;
		int py = this.point.y + offy;
		
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
