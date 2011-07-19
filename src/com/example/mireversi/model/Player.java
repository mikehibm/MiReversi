package com.example.mireversi.model;

import com.example.mireversi.model.Cell.E_STATUS;

public abstract class Player {
	
	protected E_STATUS mTurn;
	protected String mName;
	protected Board mBoard;
	
	public Player(E_STATUS turn, String name, Board board){
		setTurn(turn);
		setName(name);
		mBoard = board;
	}
	
	public abstract boolean isHuman();

	public void setTurn(E_STATUS mTurn) {
		this.mTurn = mTurn;
	}

	public E_STATUS getTurn() {
		return mTurn;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getName() {
		return mName;
	}
	
	public abstract void startThinking(IPlayerCallback callback);
	public abstract void stopThinking();
}



