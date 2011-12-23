/*
* Copyright (c) 2011 Makoto Ishida
* Please see the file MIT-LICENSE.txt for copying permission.
*/

package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;
import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;

/**
 * @author Mike
 *
 */
public class Board {
	
	public static final int COLS = 8;
	public static final int ROWS = 8;
	
	private RectF rect = new RectF();
	
	private Cell cells[][] = new Cell[ROWS][COLS];
	private Cell.E_STATUS turn;
	private Player player1;
	private Player player2;
	
	public Board(){
		this.rect.left = 0f;
		this.rect.top = 0f;
	
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j] = new Cell(this, new Point(j, i));
			}
		}

		//初期の配置をセット
		cells[ROWS/2 -1][COLS/2 -1].setStatus(Cell.E_STATUS.White);
		cells[ROWS/2 -1][COLS/2].setStatus(Cell.E_STATUS.Black);
		cells[ROWS/2][COLS/2 -1].setStatus(Cell.E_STATUS.Black);
		cells[ROWS/2][COLS/2].setStatus(Cell.E_STATUS.White);

		turn = Cell.E_STATUS.Black;
		ArrayList<Cell> changedCells = new ArrayList<Cell>();
		setAllReversibleCells(changedCells);
	}
	
	public void setRectF(RectF rect) {
		this.rect = rect;
	}
	public RectF getRectF(){
		return this.rect;
	}

	public void setSize(int w, int h){
		int sz = w < h ? w: h;						//正方形になる様に小さいほうに合わせる。
		sz = (int)(sz * 0.980f);
		sz =  (int)(sz / Board.COLS) * Board.COLS;		//列数で割り切れない場合は余りを捨てる。
		int border = (w < h ? (w - sz) : (h - sz) ) / 2;
		this.rect.left = border;
		this.rect.top = border;
		this.rect.right = this.rect.left + sz;
		this.rect.bottom = this.rect.top + sz;
		
		float cellW = this.getCellWidth();
		float cellH = this.getCellHeight();

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j].setLeft(j * cellW + this.rect.left);
				cells[i][j].setTop(i * cellH + this.rect.top);
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
	
	public Cell getCell(Point point){
		return cells[point.y][point.x];
	}
	
	public float getCellWidth(){
		return this.rect.width() / (float)COLS;
	}

	public float getCellHeight(){
		return this.rect.height() / (float)ROWS;
	}
	
	public ArrayList<Cell> changeCell(Point point, Cell.E_STATUS newStatus) {
		Cell cell = cells[point.y][point.x];
		ArrayList<Cell> list = cell.getReversibleCells();

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
	
	public boolean isFinished(){
		return (this.turn == E_STATUS.None);
	}
	
	public void setFinished(){
		this.turn = E_STATUS.None;
	}
	
	public int changeTurn(List<Cell> changedCells){
		if (this.turn == E_STATUS.Black){
			this.turn = E_STATUS.White;
		} else {
			this.turn = E_STATUS.Black;
		}

		return setAllReversibleCells(changedCells);
	}
	
	private int setAllReversibleCells(List<Cell> changedCells){
		int n = 0;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				Cell cell = cells[i][j];
				
				//再計算の前に前回マークされていた部分を変更リストに追加。
				if (changedCells != null && cell.getReversibleCells().size() > 0){
					changedCells.add(cell);
				}
				
				//裏返されるセルのリストを再計算。
				cell.setReversibleCells(this.turn);

				//再計算後に今回マークされた部分を変更リストに追加。
				if (cell.getReversibleCells().size() > 0){
					n++;
					if (changedCells != null){
						changedCells.add(cell);	
					}
				}
			}
		}
		return n;
	}
	
	public int countCells(Cell.E_STATUS status){
		int n = 0;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (cells[i][j].getStatus() == status){
					n++;
				}
			}
		}
		return n;
	}
	
	public int countBlankCells(){
		return countCells(E_STATUS.None);
	}
	
	//コマを置く事が出来るセルのリストを返す。
	public ArrayList<Cell> getAvailableCells(){
		ArrayList<Cell> available_cells = new ArrayList<Cell>();
		for (int i = 0; i< Board.ROWS; i++ ){
			for (int j =0; j < Board.COLS; j++){
				cells[i][j].setEval(0);
				if (cells[i][j].getReversibleCells().size() > 0){
					available_cells.add(cells[i][j]);
				}
			}
		}
		return available_cells;
	}
	
	public int getAvailableCellCount(boolean recalculate){
		int n = 0;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				Cell cell = cells[i][j];
				if (recalculate){
					//裏返されるセルのリストを再計算。
					cell.setReversibleCells(this.turn);
				}
				if (cell.getReversibleCells().size() > 0){
					n++;
				}
			}
		}
		return n;
	}

	public Player getWinner(){
		E_STATUS w = getWinnerStatus();
		if (w == E_STATUS.None){
			return null;
		} else if (w == E_STATUS.Black){
			return this.getPlayer1();
		} else {
			return this.getPlayer2();
		}
	}
	
	public Cell.E_STATUS getWinnerStatus(){
		E_STATUS winner = E_STATUS.None;
		int cntB = countCells(E_STATUS.Black);
		int cntW = countCells(E_STATUS.White);
		if (cntB > cntW){
			winner = E_STATUS.Black;
		} else if (cntB < cntW){
			winner = E_STATUS.White;
		}
		return winner;
	}
	
	public String getTurnDisplay(){
		return Cell.statusToDisplay(this.turn);
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
		Utils.d("getStateString:" + s);
		return s;
	}


	
	/**
	 * 文字列から状態を復元する。
	 * @param s
	 */
	public void loadFromStateString(String s){
		if (TextUtils.isEmpty(s)) return;
		
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

		setAllReversibleCells(null);
	}

	/***
	 * 盤面の新しいクローンを作成して返す。
	 */
	public Board clone(){
		Board new_board = new Board();
		
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				new_board.cells[i][j].setStatus(this.cells[i][j].getStatus());
				new_board.cells[i][j].copyReversibleCells(this.cells[i][j].getReversibleCells());
			}
		}
		new_board.turn = this.turn;
//		new_board.setAllReversibleCells(null);

		return new_board;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Player getCurrentPlayer(){
		Player p = null;
		if (turn == E_STATUS.Black){
			p = player1;
		} else if (turn == E_STATUS.White){
			p = player2;
		}
		return p;
	}

	public void write(String header, String footer){
		Utils.d(header);
		for (int i = 0; i < ROWS; i++) {
			Utils.d(String.format("%s %s %s %s %s %s %s %s", 
					cells[i][0].getStatus().toString().substring(0, 1),
					cells[i][1].getStatus().toString().substring(0, 1),
					cells[i][2].getStatus().toString().substring(0, 1),
					cells[i][3].getStatus().toString().substring(0, 1),
					cells[i][4].getStatus().toString().substring(0, 1),
					cells[i][5].getStatus().toString().substring(0, 1),
					cells[i][6].getStatus().toString().substring(0, 1),
					cells[i][7].getStatus().toString().substring(0, 1)
					));
		}
		Utils.d(footer);
	}

}
