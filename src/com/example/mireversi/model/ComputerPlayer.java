package com.example.mireversi.model;

import com.example.mireversi.model.Cell.E_STATUS;

public abstract class ComputerPlayer extends Player{
	
	public ComputerPlayer(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	public boolean isHuman() {
		return false;
	}

}
