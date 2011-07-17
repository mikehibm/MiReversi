package com.example.mireversi.model;

import com.example.mireversi.model.Cell.E_STATUS;

public abstract class Player {
	
	private E_STATUS mTurn;
	private String mName;
	
	public Player(E_STATUS turn, String name){
		setTurn(turn);
		setName(name);
	}

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
}
