package com.example.mireversi.model;

import android.graphics.Point;

import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerIntermediate extends ComputerPlayer{

	public ComputerPlayerIntermediate(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}

	@Override
	protected Point think() {
		return new Point(-1, -1);
	}


}
