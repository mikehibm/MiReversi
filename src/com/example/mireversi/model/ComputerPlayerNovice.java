package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Point;

import com.example.mireversi.Utils;
import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerNovice extends ComputerPlayer {


	public ComputerPlayerNovice(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	protected Point think() {
		Point pos = new Point(-1, -1);
		
		try {
			Utils.d(this.getName() + ": Thread.sleep started.");
			Thread.sleep(200);
		} catch (InterruptedException e) {
			setStopped(true);
		}
		Utils.d(this.getName() + ": Thread.sleep ended.");

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

		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		if (available_cells.size() > 0){
			Random rnd = new Random();
			int n = rnd.nextInt(available_cells.size());
			Cell chosenCell = available_cells.get(n);
			
			pos = new Point(chosenCell.getX(), chosenCell.getY());
		}
		
		return pos;
	}

}
