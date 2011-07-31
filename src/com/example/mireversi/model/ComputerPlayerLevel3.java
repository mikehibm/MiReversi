package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;
import com.example.mireversi.model.ComputerPlayer.EvaluationComparator;

public class ComputerPlayerLevel3 extends ComputerPlayer{

	private Random mRnd;

	//場所毎の重み付け(序盤)
	protected static final int[][] weight_table1 
		= { { 60,-15,  0, -1, -1,  0,-15, 60 }, 
			{-15,-20, -3, -3, -3, -3,-20,-15 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{ -1, -3, -1, -1, -1, -1, -3, -1 },
			{  0, -3,  0, -1, -1,  0, -3,  0 },
			{-15,-20, -3, -3, -3, -3,-20,-15 },
			{ 60,-15,  0, -1, -1,  0,-15, 60 }
	   	};

//	//場所毎の重み付け(中盤)
//	protected static final int[][] weight_table2
//		= { { 80,  2, 12,  9,  9, 12,  2, 80 }, 
//			{  2,  0, 10,  2,  2, 10,  0,  2 },
//			{ 12, 10,  5,  3,  3,  5, 10, 12 },
//			{  9,  2,  3,  3,  3,  3,  2,  9 },
//			{  9,  2,  3,  3,  3,  3,  2,  9 },
//			{ 12, 10,  5,  3,  3,  5, 10, 12 },
//			{  2,  0, 10,  2,  2, 10,  0,  2 },
//			{ 80,  2, 12,  9,  9, 12,  2, 80 }
//	   	};

	//場所毎の重み付け(終盤)
	protected static final int[][] weight_table3 
		= { { 50, 10, 10, 10, 10, 10, 10, 50 }, 
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 10,  3,  3,  3,  3,  3,  3, 10 },
			{ 50, 10, 10, 10, 10, 10, 10, 50 }
	   	};

	public ComputerPlayerLevel3(E_STATUS turn, String name, Board board){
		super(turn, name, board);
		mRnd = new Random();
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
		
////DEBUG
//Utils.d("Available cells:\n");
//for (int i = 0; i < available_cells.size(); i++) {
//	Cell cur = available_cells.get(i);
//	Utils.d(String.format("%d x,y=%d,%d   weight=%d", i, cur.getCol(), cur.getRow(),  cur.getEval()));
//}
 
		int blanks = mBoard.countBlankCells();
		int depth = 1;						//2手先まで読む
		if (blanks <= 20) depth = 2;		//3手先まで読む
		if (blanks <= 10) depth = 3;		//5手先まで読む
		int available_size = available_cells.size();
		
		for (int i = 0; i < available_size; i++) {
			Cell cur = available_cells.get(i);
			
			this.setCurrentCell(cur);
			super.onProgress(i * 100 / available_size);					//進捗を画面に表示
			
			//depth手先まで打った後の局面の評価値のうち最も高い値を得る。
			getWeightByMiniMax(mBoard, cur, depth, false);

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

		ArrayList<Cell> max_cells = new ArrayList<Cell>();
		max_cells.add(available_cells.get(0));		//ソート後先頭に来たものを最終候補リストに追加。
		int max_eval = available_cells.get(0).getEval();
		int max_avail = available_cells.get(0).getNextAvaiableCnt();
		
		//２番目以降の位置にあるもので先頭と同じ評価値を持つものを最終候補リストに追加。
		for (int i = 1; i < available_cells.size(); i++) {
			Cell current = available_cells.get(i);
			if (max_eval == current.getEval() && max_avail == current.getNextAvaiableCnt()){
				max_cells.add(current);
			} else {
				break; 
			}
		}
		
////DEBUG
//Utils.d("Max cells:\n");
//for (int i = 0; i < max_cells.size(); i++) {
//	Cell cur = max_cells.get(i);
//	Utils.d(String.format("%d x,y=%d,%d  val=%d, available_cnt=%d", i, cur.getCol(), cur.getRow(),  cur.getEval(), cur.getNextAvaiableCnt()));
//}

		//最終候補が複数ある場合はそのなかからランダムに選ぶ。
		int n = mRnd.nextInt(max_cells.size());
		Cell chosenCell = max_cells.get(n);
		pos = chosenCell.getPoint();
		
//DEBUG
Utils.d(String.format("Chosen cell=%d: %d,%d   Eval=%d", n, chosenCell.getCol(), chosenCell.getRow(), chosenCell.getEval() ));

		return pos;
	}
	

	private int[][] getWeightTable(Board board){
		int blank_cells = board.countBlankCells();
		int[][] src_tbl;
		
		src_tbl = weight_table1;
		if (blank_cells <= 10) {
			src_tbl = weight_table3;
		}

		int[][] new_tbl = new int[Board.ROWS][Board.COLS];
		for (int i =0; i < Board.ROWS; i++){
			for (int j=0; j < Board.COLS; j++){
				new_tbl[i][j] = src_tbl[i][j];
			}
		}
		
//		//左上からの確定石の重みを重くする。
//		addWeightForStableCells(board, new_tbl, new Point(0, 0), 1, 0, -1);
//		addWeightForStableCells(board, new_tbl, new Point(0, 0), 0, 1, -1);
//		addWeightForStableCells(board, new_tbl, new Point(0, 0), 1, 1, 2);
//
//		//右上からの確定石の重みを重くする。
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), -1, 0, 7);
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), 0, 1, 7);
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, 0), -1, 1, 2);
//
//		//左下からの確定石の重みを重くする。
//		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 1, 0, 7);
//		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 0, -1, 7);
//		addWeightForStableCells(board, new_tbl, new Point(0, Board.ROWS-1), 1, -1, 2);
//
//		//右下からの確定石の重みを重くする。
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), -1, 0, 7);
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), 0, -1, 7);
//		addWeightForStableCells(board, new_tbl, new Point(Board.COLS-1, Board.ROWS-1), -1, -1, 2);
		
//Utils.d(String.format("--- Weight Table Turn=%s:", board.getTurnDisplay()));		
//for (int i =0; i < Board.ROWS; i++){
//	Utils.d(String.format("%3d %3d %3d %3d %3d %3d %3d %3d ",
//			new_tbl[i][0], new_tbl[i][1], new_tbl[i][2], new_tbl[i][3],
//			new_tbl[i][4], new_tbl[i][5], new_tbl[i][6], new_tbl[i][7]));
//}
//Utils.d("--- Weight Table end");		

		return new_tbl;
	}
	
//	private void addWeightForStableCells(Board board, int[][] tbl, Point point, int dx,int dy, int limit){
//		Cell[][] cells = board.getCells();
//		if (cells[point.y][point.x].getStatus() != board.getTurn()) return;
//		int wt = getWeight(point, tbl);
//
//		point.offset(dx, dy);
//		if (limit > 0) limit--;
//
//		if (point.x < 0 || point.y < 0 
//			|| point.x >= Board.COLS || point.y >= Board.ROWS 
//			|| limit == 0){
//			return;
//		} else {
//			tbl[point.y][point.x] = wt;
//			addWeightForStableCells(board, tbl, point, dx, dy, limit);
//		}
//	}
	
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
			cur.setEval(val);

//DEBUG
//new_board.write(String.format("--- cur=%d,%d  val=%d, Turn=%s", cur.getCol(), cur.getRow(), val, new_board.getTurnDisplay()),  "---");

			int next_available_cnt = new_board.changeTurn(null);
			cur.setNextAvaiableCnt(next_available_cnt);

			return;
		}

//DEBUG
//Utils.d(String.format("Depth=%d: Turn=%s cur=%d,%d", depth, new_board.getTurnDisplay(), cur.getCol(), cur.getRow() ));
		int next_available_cnt = new_board.changeTurn(null);

		if (next_available_cnt == 0){
			getWeightByMiniMax(new_board, cur, depth-1, true);
			return;
		}

		//コマを置く事が出来るセルのリストを得る。
		ArrayList<Cell> available_cells = new_board.getAvailableCells();

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
//Utils.d(String.format("Depth=%d: Turn=%s max=%d,%d  val=%d", depth, new_board.getTurnDisplay(), max_cell.getCol(), max_cell.getRow(), max_cell.getEval() ));
		cur.setEval(max_cell.getEval() * -1);
		cur.setNextAvaiableCnt(max_cell.getNextAvaiableCnt()*-1);
	}

}
