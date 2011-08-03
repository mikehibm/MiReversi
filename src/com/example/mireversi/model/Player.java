package com.example.mireversi.model;

import android.content.Context;
import com.example.mireversi.Pref;
import com.example.mireversi.model.Cell.E_STATUS;

public abstract class Player {
	
	protected E_STATUS mTurn;
	protected String mName;
	protected Board mBoard;

	private int mProgress;
	private Cell mCurrentCell;
	
	
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
	
	private static final Player getPlayer(String name, Board board, E_STATUS turn, String value){
		int int_value = Integer.valueOf(value);
		Player player;
		switch (int_value){
//		case 0: 
//			player = new ComputerPlayerLevel0(turn, name, board);
//			break;
		case 1: 
			player = new ComputerPlayerLevel1(turn, name, board);
			break;
		case 2:
			player = new ComputerPlayerLevel2(turn, name, board);
			break;
		case 3:
			player = new ComputerPlayerLevel3(turn, name, board);
			break;
		default:
			player = new HumanPlayer(turn, name,board);
		}
		return player;
	}
	
	public static final Player getPlayer1(Context con, Board board, E_STATUS turn){
		String name = Pref.getPlayer1Name(con);
		String value = Pref.getPlayer1(con);
		return getPlayer(name, board, turn, value);
	}

	public static final Player getPlayer2(Context con, Board board, E_STATUS turn){
		String name = Pref.getPlayer2Name(con);
		String value = Pref.getPlayer2(con);
		return getPlayer(name, board, turn, value);
	}

	public void setProgress(int mProgress) {
		this.mProgress = mProgress;
	}

	public int getProgress() {
		return mProgress;
	}

	public void setCurrentCell(Cell mCurrentCell) {
		this.mCurrentCell = mCurrentCell;
	}

	public Cell getCurrentCell() {
		return mCurrentCell;
	}

}



