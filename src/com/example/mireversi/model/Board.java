package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.graphics.RectF;
import android.util.Log;
import com.example.mireversi.exceptions.*;
import com.example.mireversi.model.Cell.E_STATUS;

/**
 * @author mike
 *
 */
public class Board {
	
	private static final String TAG = "Board";
	public static final int COLS = 8;
	public static final int ROWS = 8;
	
	private RectF rect = new RectF();
	
	private Cell cells[][] = new Cell[ROWS][COLS];
	private Cell.E_STATUS turn;
	
	public Board(){
		this.rect.left = 0f;
		this.rect.top = 0f;
	
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j] = new Cell(this, j, i);
			}
		}

		//初期の配置をセット
		cells[ROWS/2 -1][COLS/2 -1].setStatus(Cell.E_STATUS.Black);
		cells[ROWS/2 -1][COLS/2].setStatus(Cell.E_STATUS.White);
		cells[ROWS/2][COLS/2 -1].setStatus(Cell.E_STATUS.White);
		cells[ROWS/2][COLS/2].setStatus(Cell.E_STATUS.Black);

		turn = Cell.E_STATUS.Black;
	}
	
	public void setRectF(RectF rect) {
		this.rect = rect;
	}
	public RectF getRectF(){
		return this.rect;
	}

	public void setSize(int w, int h){
		int sz = w < h ? w: h;						//正方形になる様に小さいほうに合わせる。
		
		this.rect.right = this.rect.left + (int)(sz / Board.COLS) * Board.COLS;		//列数で割り切れない場合は余りを捨てる。
		this.rect.bottom = this.rect.top + (int)(sz / Board.ROWS) * Board.ROWS;		//行数で割り切れない場合は余りを捨てる。
		
		float cellW = this.getCellWidth();
		float cellH = this.getCellHeidht();

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j].setLeft(j * cellW);
				cells[i][j].setTop(i * cellH);
				cells[i][j].setWidth(cellW);
				cells[i][j].setHeight(cellH);
			}
		}
	}
	
	public Cell[][] getCells(){
		return cells;
	}
	public void setCells(Cell[][] cells){
		this.cells = cells;
	}
	
	public float getCellWidth(){
		return this.rect.width() / (float)COLS;
	}

	public float getCellHeidht(){
		return this.rect.height() / (float)ROWS;
	}
	
	public ArrayList<Cell> changeCell(int r, int c, Cell.E_STATUS newStatus) throws Exception{
		Cell cell = cells[r][c];
		
		Cell.E_STATUS oppStatus = cell.getOpponentStatus(newStatus);
		
		ArrayList<Cell> list = cell.getReversibleCells(oppStatus);
		if (list.size() == 0){
			throw new InvalidMoveException();
		}

		ArrayList<Cell> changedCells = new ArrayList<Cell>();

		for (Cell cell2 : list) {
			cell2.setStatus(newStatus);
			changedCells.add(cell2);
		}

		cell.setStatus(newStatus);
		changedCells.add(cell);

		return changedCells;
	}
	
	public Cell.E_STATUS getTurn(){
		return this.turn;
	}
	
	public void changeTurn(){
		if (this.turn == E_STATUS.Black){
			this.turn = E_STATUS.White;
		} else {
			this.turn = E_STATUS.Black;
		}
	}
	
	/**
	 * 状態を文字列にシリアライズする。
	 */
	public String getStateString(){
		StringBuilder str = new StringBuilder();
	
		str.append(Cell.statusToString(this.turn) + ":");
		
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				str.append(cells[i][j].getStatusString());
			}
		}
		
		String s = str.toString();
		Log.d(TAG, "getStateString:" + s);
		return s;
	}


	
	/**
	 * 文字列から状態を復元する。
	 * @param s
	 */
	public void loadFromStateString(String s){
		
		this.turn = Cell.stringToStatus(s.substring(0, 1));
		
		String s2;
		int start = 2;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				s2 = s.substring(start, start+1);
				cells[i][j].setStatusString(s2);
				start++;
			}
		}
	}
}
