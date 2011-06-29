package com.example.mireversi;

import com.example.mireversi.model.Board;
import com.example.mireversi.model.Cell;
import com.example.mireversi.model.Cell.E_STATUS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ReversiView extends View {

	private Board mBoard = new Board();

	public ReversiView(Context context) {
		super(context);
		
		setFocusable(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mBoard.setSize(getWidth(), getHeight());
		
		drawBoard(canvas);
	}

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			int r = (int)(y / mBoard.getCellHeidht());
			int c = (int)(x / mBoard.getCellWidth());
			if (r < Board.ROWS && c < Board.COLS){
				try {
					mBoard.changeCell(r, c, mBoard.getTurn());
					mBoard.changeTurn();
				} catch (Exception e) {
					//Toast.makeText(this.getContext(), e.getMessage(), 300).show();
				}

				invalidate();			//画面を再描画
			}
			break;
		default:
			return true;
		}
		
		return true;
	}
	
	
	private void drawBoard(Canvas canvas){
		int bw = mBoard.getWidth();
		int bh = mBoard.getHeight();
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeidht();

		if (mBoard.getWidth() <=0 ) return;

		Paint paint = new Paint();					//本当はここでnewするのはパフォーマンス上良くない。後で直そう。
		paint.setColor(Color.rgb(0, 180, 0));

		canvas.drawRect( 0, 0, bw, bh, paint);

		paint.setColor(Color.rgb(40, 40, 40));		//罫線の色
		
		//縦線
		for (int i = 0; i < Board.COLS; i++) {
			canvas.drawLine(cw * (i+1), 0, cw * (i+1), bh, paint);
		}
		//横線
		for (int i = 0; i < Board.ROWS; i++) {
			canvas.drawLine(0, ch * (i+1), bw, ch * (i+1), paint);
		}

		//円を描く前にアンチエイリアスを指定。これをしないと円がギザギザになる。
		paint.setAntiAlias(true);

		Cell[][] cells = mBoard.getCells();
		for (int i = 0; i < Board.ROWS; i++) {
			for (int j = 0; j < Board.COLS; j++) {
				Cell cell =cells[i][j]; 
				Cell.E_STATUS st = cell.getStatus();

				if (st == E_STATUS.Black){
					paint.setColor(Color.BLACK);
				} else if(st == E_STATUS.White){
					paint.setColor(Color.WHITE);
				}

				if (st != E_STATUS.None){
					canvas.drawCircle(cell.getCx(), cell.getCy(), (float) (cw * 0.46), paint);
				}
			}
		}

	}

}
