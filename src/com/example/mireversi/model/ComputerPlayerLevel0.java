/*
* Copyright (c) 2011 Makoto Ishida
* Please see the file MIT-LICENSE.txt for copying permission.
*/

package com.example.mireversi.model;

import java.util.ArrayList;
import java.util.Random;
import android.graphics.Point;
import com.example.mireversi.model.Cell.E_STATUS;

public class ComputerPlayerLevel0 extends ComputerPlayer {

	private static int WAIT_MSEC = 10;
	private Random mRnd;
	
	public ComputerPlayerLevel0(E_STATUS turn, String name, Board board){
		super(turn, name, board);
		
		mRnd = new Random();
	}

	@Override
	protected Point think() {
		Point pos = null;
		
		try {
			Thread.sleep(WAIT_MSEC);
		} catch (InterruptedException e) {
			setStopped(true);
		}
		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		//コマを置く事が出来る場所のリストを得る。
		ArrayList<Cell> available_cells = mBoard.getAvailableCells();
		if (available_cells.size() == 0){
			return pos;
		}
		
		if (isStopped()) return pos;					//中断フラグが立っていたら抜ける。
		
		//置く事が出来る場所からランダムに選ぶ。
		int n = mRnd.nextInt(available_cells.size());
		Cell chosenCell = available_cells.get(n);
		pos = chosenCell.getPoint();

		return pos;
	}

}
