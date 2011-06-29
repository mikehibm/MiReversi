package com.example.mireversi;

import com.example.mireversi.model.Board;
import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity{
	private Board mBoard = new Board();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new ReversiView(this));
		
	}
	
	


}
