package com.example.mireversi.model;

import android.graphics.Point;

import com.example.mireversi.model.Cell.E_STATUS;

public class HumanPlayer extends Player {

	public HumanPlayer(E_STATUS turn, String name, Board board){
		super(turn, name, board);
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}

	@Override
	public void startThinking(IPlayerCallback callback) {
		callback.onEndThinking(null);
	}

	@Override
	public void stopThinking() {
		//Do nothing.
	}



}
