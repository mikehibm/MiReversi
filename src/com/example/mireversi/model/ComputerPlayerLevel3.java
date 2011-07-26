package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;
import com.example.mireversi.model.ComputerPlayer.EvaluationComparator;

public class ComputerPlayerLevel3 extends ComputerPlayer{

	//場所毎の重み付け(序盤)
	protected static final int[][] weight_table1 
		= { { 40,-12,  0, -1, -1,  0,-12, 40 }, 
			{-12,-15, -3, -3, -3, -3,-15,-12 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{-12,-15, -3, -3, -3, -3,-15,-12 },
			{ 40,-12,  0, -1, -1,  0,-12, 40 }
	   	};

	//場所毎の重み付け(中盤)
	protected static final int[][] weight_table2
		= { { 30,-10,  0,  0,  0,  0,-10, 30 }, 
			{-10,-15,  3,  0,  0,  3,-15,-10 },
			{  0,  3,  0,  1,  1,  0,  3,  0 },
			{ -1,  0,  1,  1,  1,  1,  0, -1 },
			{ -1,  0,  1,  1,  1,  1,  0, -1 },
			{  0,  3,  0,  1,  1,  0,  3,  0 },
			{-10,-15,  3,  0,  0,  3,-15,-10 },
			{ 30,-10,  0,  0,  0,  0,-10, 30 }
	   	};

	//場所毎の重み付け(終盤)
	protected static final int[][] weight_table3 
		= { { 30,  5,  5,  5,  5,  5,  5, 30 }, 
			{  5,  3,  3,  3,  3,  3,  5,  5 },
			{  5,  3,  3,  3,  3,  3,  3,  5 },
			{  5,  3,  3,  3,  3,  3,  3,  5 },
			{  5,  3,  3,  3,  3,  3,  3,  5 },
			{  5,  3,  3,  3,  3,  3,  3,  5 },
			{  5,  3,  3,  3,  3,  3,  3,  5 },
			{ 30,  5,  5,  5,  5,  5,  5, 30 }
	   	};

	public ComputerPlayerLevel3(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	protected Point think() {
		Point pos = null;
		
		//コマを置く事が出来るセルのリストを得る。
		ArrayList<Cell> available_cells = mBoard.getAvailableCells();

		//可能な手が0個の場合はパス。
		if (available_cells.size() == 0){
			return null;
		}
		//可能な手が一つしか無い場合はそれを選ぶ。
		if (available_cells.size() == 1){
			return available_cells.get(0).getPoint();
		}
		//場所の重みの重いものから降順にソート。
		Collections.sort(available_cells, new WeightComparator(getWeightTable(mBoard)));
////DEBUG
//Utils.d("Available cells:\n");
//for (int i = 0; i < available_cells.size(); i++) {
//	Cell cur = available_cells.get(i);
//	Utils.d(String.format("%d x,y=%d,%d   weight=%d", i, cur.getCol(), cur.getRow(),  cur.getEval()));
//}
 
		int depth = 1; //(60 - blanks) / 20;
		
		for (int i = 0; i < available_cells.size(); i++) {
			Cell cur = available_cells.get(i);
			
//			if (i < 5){
				//depth手先まで打った後の局面の評価値のうち最も高い値を得る。
				getWeightByMiniMax(mBoard, cur, depth, false);
//			} else {
//				cur.setEval(Integer.MIN_VALUE);
//				cur.setNextAvaiableCnt(0);
//			}

			if (isStopped()) return null;					//中断フラグが立っていたら抜ける。
		}
		
		//評価値の高いものから降順にソート。
		Collections.sort(available_cells, new EvaluationComparator());

//DEBUG
Utils.d(String.format("Depth=%d:  Sorted available cells:\n", depth));
for (int i = 0; i < available_cells.size(); i++) {
	Cell cur = available_cells.get(i);
	Utils.d(String.format("%d x,y=%d,%d   val=%d,available_cnt=%d", i, cur.getCol(), cur.getRow(),  cur.getEval(), cur.getNextAvaiableCnt()));
}

		int max_eval = available_cells.get(0).getEval();
		
		ArrayList<Cell> max_cells = new ArrayList<Cell>();
		max_cells.add(available_cells.get(0));		//ソート後先頭に来たものを最終候補リストに追加。
		
		//２番目以降の位置にあるもので先頭と同じ評価値を持つものを最終候補リストに追加。
		for (int i = 1; i < available_cells.size(); i++) {
			Cell current = available_cells.get(i);
			if (max_eval == current.getEval()){
				max_cells.add(current);
			} else {
				break; 
			}
		}
		
//DEBUG
Utils.d("Max cells:\n");
for (int i = 0; i < max_cells.size(); i++) {
	Cell cur = max_cells.get(i);
	Utils.d(String.format("%d x,y=%d,%d  val=%d, available_cnt=%d", i, cur.getCol(), cur.getRow(),  cur.getEval(), cur.getNextAvaiableCnt()));
}

//		//最終候補が複数ある場合はそのなかからランダムに選ぶ。
//		Random rnd = new Random();
//		int n = rnd.nextInt(max_cells.size());
//		Cell chosenCell = max_cells.get(n);
		Cell chosenCell = max_cells.get(0);
		pos = chosenCell.getPoint();
		
////DEBUG
//Utils.d(String.format("Chosen cell=%d: %d,%d   Eval=%d", n, chosenCell.getCol(), chosenCell.getRow(), chosenCell.getEval() ));

		return pos;
	}
	

	private int[][] getWeightTable(Board board){
		int blank_cells = board.countBlankCells();
		int[][] src_tbl;
		src_tbl = weight_table1;
//		if (blank_cells >= 40) {
//			src_tbl = weight_table1;
//		} else if (blank_cells >= 10){
//			src_tbl = weight_table2;
//		} else {
//			src_tbl = weight_table3;
//		}
		int[][] new_tbl = new int[Board.ROWS][Board.COLS];
		for (int i =0; i < Board.ROWS; i++){
			for (int j=0; j < Board.COLS; j++){
				new_tbl[i][j] = src_tbl[i][j];
			}
		}
		
		//左上からの確定石の重みを重くする。
		addWeightForStableCells(board, new_tbl, new Point(0, 0), 1, 0, -1);
		addWeightForStableCells(board, new_tbl, new Point(0, 0), 0, 1, -1);
		addWeightForStableCells(board, new_tbl, new Point(0, 0), 1, 1, 2);

		//右上からの確定石の重みを重くする。
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), -1, 0, -1);
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), 0, 1, -1);
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), -1, 1, 2);

		//左下からの確定石の重みを重くする。
		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 1, 0, -1);
		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 0, -1, -1);
		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 1, -1, 2);

		//右下からの確定石の重みを重くする。
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), -1, 0, -1);
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), 0, -1, -1);
		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), -1, -1, 2);

		return new_tbl;
	}
	
	private void addWeightForStableCells(Board board, int[][] tbl, Point point, int dx,int dy, int limit){
		Cell[][] cells = board.getCells();
		if (cells[point.y][point.x].getStatus() != board.getTurn()) return;

		point.offset(dx, dy);
		if (limit > 0) limit--;

		if (point.x < 0 || point.y < 0 
			|| point.x >= Board.COLS || point.y >= Board.ROWS 
			|| limit == 0){
			return;
		} else {
			tbl[point.y][point.x] = Math.abs(tbl[point.y][point.x]) * 2;
			addWeightForStableCells(board, tbl, point, dx, dy, limit);
		}
	}
	
	public void getWeightByMiniMax(Board prev_board, Cell cur, int depth, boolean passed){
		
		if (isStopped()) return;					//中断フラグが立っていたら抜ける。

		//前の盤面をクローンして1手先用の盤面を作成。
		Board new_board = prev_board.clone();
		
		if (!passed){
			//1手打って局面を進める。
			new_board.changeCell(cur.getPoint(), new_board.getTurn());
		}
		
		if (depth == 0){
			int val = getWeightTotal(new_board, getWeightTable(new_board));
			if (mTurn != new_board.getTurn()) val *= -1;
			cur.setEval(val);

new_board.write(String.format("--- cur=%d,%d  val=%d, Turn=%s", 
		cur.getCol(), cur.getRow(), val, new_board.getTurnDisplay()), 
   "---");

			int next_available_cnt = new_board.changeTurn(null);
			
			if (mTurn != new_board.getTurn()) next_available_cnt *= -1;
			cur.setNextAvaiableCnt(next_available_cnt);

			return;
		}

//DEBUG
Utils.d(String.format("Depth=%d: Turn=%s cur=%d,%d", depth, new_board.getTurnDisplay(), cur.getCol(), cur.getRow() ));
		int next_available_cnt = new_board.changeTurn(null);

		if (next_available_cnt == 0){
			getWeightByMiniMax(new_board, cur, depth-1, true);
			return;
		}

		//コマを置く事が出来るセルのリストを得る。
		ArrayList<Cell> available_cells = new_board.getAvailableCells();

//		//場所の重みの重いものから降順にソート。
//		Collections.sort(available_cells, new WeightComparator(getWeightTable(new_board)));

		Cell max_cell = available_cells.get(0);
		for (int i = 0; i < available_cells.size(); i++) {
			Cell cell = available_cells.get(i);

			//depth手先の局面での最も高い評価値を得る。
			getWeightByMiniMax(new_board, cell, depth-1, false);
			
			if (cell.getEval() > max_cell.getEval()) { 
				max_cell = cell; 
			} else if (cell.getEval() == max_cell.getEval()) {
				if (cell.getNextAvaiableCnt() > max_cell.getNextAvaiableCnt()){
					max_cell = cell; 
				} else if (cell.getNextAvaiableCnt() < max_cell.getNextAvaiableCnt()){
					max_cell = cell; 
				}
			}
		}

//DEBUG
Utils.d(String.format("Depth=%d: Turn=%s max=%d,%d  val=%d", depth, new_board.getTurnDisplay(), max_cell.getCol(), max_cell.getRow(), max_cell.getEval() ));
		cur.setEval(max_cell.getEval());
		cur.setNextAvaiableCnt(max_cell.getNextAvaiableCnt());
	}

}
