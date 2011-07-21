package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerIntermediate extends ComputerPlayer{

	private static final int[][] weight 
			= { { 30,-12,  0, -1, -1,  0,-12, 30 }, 
				{-12,-15, -3, -3, -3, -3,-15,-12 },
				{  0, -3,  0, -1, -1,  0, -3,  0 },
				{ -1, -3, -1, -1, -1, -1, -3, -1 },
				{ -1, -3, -1, -1, -1, -1, -3, -1 },
				{  0, -3,  0, -1, -1,  0, -3,  0 },
				{-12,-15, -3, -3, -3, -3,-15,-12 },
				{ 30,-12,  0, -1, -1,  0,-12, 30 }
			   };
	
	public ComputerPlayerIntermediate(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	protected Point think() {
		Point pos = new Point(-1, -1);
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			setStopped(true);
		}
		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		Cell[][] cells = mBoard.getCells();
		
		ArrayList<Cell> available_cells = new ArrayList<Cell>();
		for (int i = 0; i< Board.ROWS; i++ ){
			for (int j =0; j < Board.COLS; j++){
				if (cells[i][j].getReversibleCells().size() > 0){
					available_cells.add(cells[i][j]);
				}
			}
		}
		
		Collections.sort(available_cells, new MyComparator());

		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		if (available_cells.size() > 0){
			Cell chosenCell = available_cells.get(0);
			pos = chosenCell.getPoint();
		}
		
		return pos;
	}

	public class MyComparator implements Comparator<Cell> {  
		@Override
		public int compare(Cell cell1, Cell cell2) {
			//0：等しい。1：より大きい。-1：より小さい
			int weight1 = weight[cell1.getPoint().y][cell1.getPoint().x];
			int weight2 = weight[cell2.getPoint().y][cell2.getPoint().x];
			int n = Integer.signum(weight2 - weight1);		//降順にソートする。
			return n;      
		}  
	}  

}
