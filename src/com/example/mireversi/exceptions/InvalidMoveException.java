/*
* Copyright (c) 2011 Makoto Ishida
* Please see the file MIT-LICENSE.txt for copying permission.
*/

package com.example.mireversi.exceptions;

public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = 8874475463061014928L;
	
	String mMsg = "Invalid move.";
	
	public InvalidMoveException(){
		//
	}
	
	public InvalidMoveException(String msg){
		mMsg = msg;
	}
	
	@Override
	public String getMessage(){
		return mMsg;
	}
}
