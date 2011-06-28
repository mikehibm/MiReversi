package com.example.mireversi;

import com.example.mireversi.model.*;
import com.example.mireversi.model.Cell.E_STATUS;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;



public class MainActivity extends Activity {

	private Board mBoard = new Board();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new CustomSurfaceView(this));

	}

	class CustomSurfaceView extends SurfaceView 
	implements SurfaceHolder.Callback {

		private int mScrWidth;
		private int mScrHeight;


		public CustomSurfaceView(Context context) {
			super(context);

			setFocusable(true);
			getHolder().addCallback(this);
		}

		@Override
		public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
			// SurfaceViewが変化（画面の大きさ，ピクセルフォーマット）した時のイベントの処理を記述
			mScrWidth = width;
			mScrHeight = height;

			mBoard.setSize(width, height);

			doDraw(holder);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// SurfaceViewが作成された時の処理（初期画面の描画等）を記述
			//doDraw(holder);
		}


		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// SurfaceViewが廃棄された時の処理を記述
		}

		private void doDraw(SurfaceHolder holder) {
			Canvas canvas = holder.lockCanvas();
			onDraw(canvas);
			holder.unlockCanvasAndPost(canvas);
		}


		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);

			showPanel(canvas);

		}

		private void showPanel(Canvas canvas){
			int bw = mBoard.getWidth();
			int bh = mBoard.getHeight();
			float cw = mBoard.getCellWidth();
			float ch = mBoard.getCellHeidht();

			if (mBoard.getWidth() <=0 ) return;

			Paint paint = new Paint();
			paint.setColor(Color.rgb(0, 180, 0));

			canvas.drawRect( 0, 0, bw, bh, paint);

			//罫線の色
			paint.setColor(Color.rgb(40, 40, 40));
			
			//縦線
			for (int i = 0; i < Board.COLS; i++) {
				canvas.drawLine(cw * (i+1), 0, cw * (i+1), bh, paint);
			}
			//横線
			for (int i = 0; i < Board.ROWS; i++) {
				canvas.drawLine(0, ch * (i+1), bw, ch * (i+1), paint);
			}

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
						Toast.makeText(this.getContext(), e.getMessage(), 1000).show();
					}

					doDraw(getHolder());
				}
				break;
			default:
				return true;
			}
			
			return true;
		}

	}

}