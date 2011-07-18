package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Random;

import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerNovice extends ComputerPlayer {

	private static final String TAG = "MiReversi.ComputerPlayerNovice";

	public ComputerPlayerNovice(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	public void StartThinking(IPlayerCallback callback) {
		Cell[][] cells = mBoard.getCells();
		ArrayList<Cell> available_cells = new ArrayList<Cell>();
		
		for (int i = 0; i< Board.ROWS; i++ ){
			for (int j =0; j < Board.COLS; j++){
				if (cells[i][j].getReversibleCells().size() > 0){
					available_cells.add(cells[i][j]);
				}
			}
		}

		Random rnd = new Random();
		int n = rnd.nextInt(available_cells.size());
		Cell chosenCell = available_cells.get(n);
		
		callback.onEndThinking(chosenCell.getY(), chosenCell.getX());

	}

}
