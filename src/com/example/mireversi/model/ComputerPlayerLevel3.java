package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;
import com.example.mireversi.model.ComputerPlayer.EvaluationComparator;

public class ComputerPlayerLevel3 extends ComputerPlayer{

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
		Collections.sort(available_cells, new WeightComparator());
//DEBUG
Utils.d("Available cells:\n");
for (int i = 0; i < available_cells.size(); i++) {
	Cell cur = available_cells.get(i);
	Utils.d(String.format("%d x,y=%d,%d   weight=%d", i, cur.getCol(), cur.getRow(),  cur.getEval()));
}

		if (isStopped()) return null;					//中断フラグが立っていたら抜ける。
		
		int depth = 0; //(60 - blanks) / 20;
		
		for (int i = 0; i < available_cells.size(); i++) {
			Cell cur = available_cells.get(i);
			
			if (i < 5){
				//depth手先まで打った後の局面の評価値のうち最も高い値を得る。
				getWeightByMiniMax(mBoard, cur, depth, false);
			} else {
				cur.setEval(Integer.MIN_VALUE);
				cur.setNextAvaiableCnt(0);
			}

			if (isStopped()) return null;					//中断フラグが立っていたら抜ける。
		}
		
		//評価値の高いものから降順にソート。
		Collections.sort(available_cells, new EvaluationComparator());

//DEBUG
Utils.d(String.format("Depth=%d:  Sorted available cells:\n", depth));
for (int i = 0; i < available_cells.size(); i++) {
	Cell cur = available_cells.get(i);
	Utils.d(String.format("%d x,y=%d,%d   val=%d", i, cur.getCol(), cur.getRow(),  cur.getEval()));
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
	Utils.d(String.format("%d x,y=%d,%d   weight=%d", i, cur.getCol(), cur.getRow(),  cur.getEval()));
}

		//最終候補が複数ある場合はそのなかからランダムに選ぶ。
		Random rnd = new Random();
		int n = rnd.nextInt(max_cells.size());
		Cell chosenCell = max_cells.get(n);
		pos = chosenCell.getPoint();
		
//DEBUG
Utils.d(String.format("Chosen cell=%d: %d,%d   Eval=%d", n, chosenCell.getCol(), chosenCell.getRow(), chosenCell.getEval() ));

		return pos;
	}
	

	public void getWeightByMiniMax(Board prev_board, Cell cur, int depth, boolean passed){
		
		if (isStopped()) return;					//中断フラグが立っていたら抜ける。

		//前の盤面をクローンして1手先用の盤面を作成。
		Board new_board = prev_board.clone();
		
		if (!passed){
			//1手打って局面を進める。
			new_board.changeCell(cur.getPoint(), new_board.getTurn());
		}

		int next_available_cnt = new_board.changeTurn(null);
		
		if (depth == 0){
			int val = getWeightTotal(new_board);
			cur.setEval(val);
			cur.setNextAvaiableCnt(next_available_cnt);
			return;
		}

		if (next_available_cnt == 0){
			getWeightByMiniMax(new_board, cur, depth-1, true);
		}

		//コマを置く事が出来るセルのリストを得る。
		ArrayList<Cell> available_cells = new_board.getAvailableCells();

		//場所の重みの重いものから降順にソート。
		Collections.sort(available_cells, new WeightComparator());

////DEBUG
//Utils.d(String.format("Depth=%d: Turn=%s: Available cells:\n", depth, new_board.getTurnDisplay()));

		Cell max_cell = available_cells.get(0);
		for (int i = 0; i < available_cells.size() && i < 7; i++) {
			Cell cell = available_cells.get(i);

			//depth手先の局面での最も高い評価値を得る。
			getWeightByMiniMax(new_board, cell, depth-1, false);
			
			if (cell.getEval() > max_cell.getEval()) { 
				max_cell = cell; 
			} else if (cell.getEval() == max_cell.getEval()) {
				if (cell.getNextAvaiableCnt() > max_cell.getNextAvaiableCnt() && mTurn == new_board.getTurn()){
					max_cell = cell; 
				} else if (cell.getNextAvaiableCnt() < max_cell.getNextAvaiableCnt() && mTurn == new_board.getTurn()){
					max_cell = cell; 
				}
			}
		}

		cur.setEval(max_cell.getEval());
		cur.setNextAvaiableCnt(max_cell.getNextAvaiableCnt());
	}

}
