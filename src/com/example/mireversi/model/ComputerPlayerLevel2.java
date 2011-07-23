package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerLevel2 extends ComputerPlayer{

	public ComputerPlayerLevel2(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	protected Point think() {
		Point pos = null;
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			setStopped(true);
		}
		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		//コマを置く事が出来るセルのリストを得る。
		ArrayList<Cell> available_cells = mBoard.getAvailableCells();
		if (available_cells.size() == 0){
			return pos;
		}
		
		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		for (int i = 0; i < available_cells.size(); i++) {
			Cell cur = available_cells.get(i);
			//評価値をクリア
			cur.setEval(0);		
			
			//1手先用の盤面を作成。
			Board newBoard = mBoard.clone();
			try {
				//1手打つ。
				newBoard.changeCell(cur.getPoint(), newBoard.getTurn());
				
				//新しい盤面の局面の評価値を得る。
				cur.setEval(getWeightTotal(newBoard));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//評価値の高いものから降順にソート。
		Collections.sort(available_cells, new EvaluationComparator());

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
		
		//最終候補が複数ある場合はそのなかからランダムに選ぶ。
		Random rnd = new Random();
		int n = rnd.nextInt(max_cells.size());
		Cell chosenCell = max_cells.get(n);
		pos = chosenCell.getPoint();
		
		return pos;
	}
	

}
