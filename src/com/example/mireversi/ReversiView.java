package com.example.mireversi;

import java.util.ArrayList;
import java.util.List;

import com.example.mireversi.model.Board;
import com.example.mireversi.model.Cell;
import com.example.mireversi.model.IPlayerCallback;
import com.example.mireversi.model.Player;
import com.example.mireversi.model.Cell.E_STATUS;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ReversiView extends View implements IPlayerCallback, Runnable{

	private static final int VIEW_ID = 1000;

	private Board mBoard;
	
	private Paint mPaintScreenBg = new Paint();
	private Paint mPaintScreenBg2 = new Paint();
	private Paint mPaintBoardBg = new Paint();
	private Paint mPaintBoardBorder = new Paint();
	private Paint mPaintCellFgB = new Paint();
	private Paint mPaintCellFgW = new Paint();
	private Paint mPaintCellAvB = new Paint();
	private Paint mPaintCellAvW = new Paint();
	private Paint mPaintTextFg = new Paint();
	private Paint mPaintTurnRect = new Paint();
	private Paint mPaintWinnerRect = new Paint();
	private Paint mPaintCellCur = new Paint();
	
	private Bitmap mBitmapWhite;
	private Bitmap mBitmapBlack;
	private Bitmap mBitmapBoard;
	
	private int mWidth;
	private int mHeight;
	private static final float CELL_SIZE_FACTOR = 0.40f;
	private static final float CELL_SIZE_FACTOR_PRG = 0.30f;
	private boolean mPaused; 

	private Handler mHandler = new Handler();
	private List<Cell> mTurnningCells = null;
	private List<Cell> mChangedCells = null;
	private int mTurnningProgress = 0;
	private static final int TURNNING_FREQ = 5;    //frames to complete a turn.
	private static final int TURNING_TIME = 500;  //msec
	

	public ReversiView(Context context) {
		super(context);

		setId(VIEW_ID);
		setFocusable(true);
		
		mPaintScreenBg.setColor(getResources().getColor(R.color.screen_bg));
		mPaintScreenBg2.setColor(getResources().getColor(R.color.screen_bg2));
		mPaintBoardBg.setColor(getResources().getColor(R.color.board_bg));
		mPaintBoardBorder.setColor(getResources().getColor(R.color.board_border));
		mPaintCellFgB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellFgW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellAvB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellAvW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellCur.setColor(getResources().getColor(R.color.cell_fg_current));
		mPaintTextFg.setColor(getResources().getColor(R.color.text_fg));
		mPaintTurnRect.setColor(getResources().getColor(R.color.turn_rect));
		mPaintWinnerRect.setColor(getResources().getColor(R.color.winner_rect));

		//アンチエイリアスを指定。これをしないと縁がギザギザになる。
		mPaintCellFgB.setAntiAlias(true);
		mPaintCellFgW.setAntiAlias(true);
		mPaintCellAvB.setAntiAlias(true);
		mPaintCellAvW.setAntiAlias(true);
		mPaintCellCur.setAntiAlias(true);

		mPaintCellAvB.setAlpha(32);
		mPaintCellAvW.setAlpha(64);
		mPaintCellCur.setAlpha(128);
		
		mPaintTextFg.setAntiAlias(true);
		mPaintTextFg.setStyle(Style.FILL);
		
		//参考URL:
		// http://y-anz-m.blogspot.com/2010/02/android-multi-screen.html 
		// http://y-anz-m.blogspot.com/2010/05/androiddimension.html
		Resources res = getResources();  
		int fontSize = res.getDimensionPixelSize(R.dimen.font_size_status); 
		mPaintTextFg.setTextSize(fontSize);
		
		mPaintTurnRect.setAntiAlias(true);
		mPaintTurnRect.setAlpha(128);
		mPaintTurnRect.setStyle(Style.STROKE);
		mPaintTurnRect.setStrokeWidth(5f);

		mPaintWinnerRect.setAntiAlias(true);
		mPaintWinnerRect.setAlpha(192);
		mPaintWinnerRect.setStyle(Style.STROKE);
		mPaintWinnerRect.setStrokeWidth(5f);

		init(false);
	}
	
	public void init(boolean auto_start){
		mBoard = new Board();
		mPaused = false;
		
		mBoard.setPlayer1(Player.getPlayer1(getContext(), mBoard, E_STATUS.Black));
		mBoard.setPlayer2(Player.getPlayer2(getContext(), mBoard, E_STATUS.White));
		
		invalidate();
		
		Utils.d("init");
		if (auto_start){
			callPlayer();
		}
		
		GameActivity activity = (GameActivity)this.getContext();
		activity.hideWinner("Started!");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		this.mWidth = getWidth();
		this.mHeight = getHeight();
		mBoard.setSize(this.mWidth, this.mHeight);
		
		if (mBitmapBlack == null){
			loadBitmap();
		}
		
		drawBoard(canvas);
	}
	
	private void loadBitmap(){
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeight();
		int INSET = (int)(cw * CELL_SIZE_FACTOR * 0.3);
		Resources res = this.getContext().getResources();

		try {
			
			//ボードの背景
			Bitmap board = BitmapFactory.decodeResource(res, R.drawable.bg2_green2);
			mBitmapBoard = Bitmap.createScaledBitmap(board, (int)mBoard.getRectF().width(), (int)mBoard.getRectF().height(), true);
		} catch (Exception ex) {
			Utils.d(ex.getMessage());
		}

		try {
			//黒のコマ
			Bitmap black = BitmapFactory.decodeResource(res, R.drawable.b1);
			mBitmapBlack = Bitmap.createScaledBitmap(black, (int)cw-INSET*2, (int)ch-INSET*2, true);
		} catch (Exception ex) {
			Utils.d(ex.getMessage());
		}

		try {
			//白のコマ
			Bitmap white = BitmapFactory.decodeResource(res, R.drawable.w1);
			mBitmapWhite = Bitmap.createScaledBitmap(white, (int)cw-INSET*2, (int)ch-INSET*2, true);
		} catch (Exception ex) {
			Utils.d(ex.getMessage());
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTurnningCells != null || mTurnningProgress > 0) return true;		//裏返しの最中は何も出来ない。
		
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			int r = (int)(y / mBoard.getCellHeight());
			int c = (int)(x / mBoard.getCellWidth());
			if (r < Board.ROWS && c < Board.COLS && r >=0 && c >= 0){
				Player p = mBoard.getCurrentPlayer();
				if (p != null && p.isHuman()){
					move(new Point(c, r));
				}
			}
			break;
		default:
		}
		
		return true;
	}

	private void move(Point point){
		mChangedCells = null;
		mTurnningCells = null;
		mTurnningProgress = 0;
		
		Cell currentCell = mBoard.getCell(point);
		
		if (currentCell.getReversibleCells().size() == 0){
			String s = String.format("Invalid move. (r,c=%d,%d)", point.y, point.x);
			//Toast.makeText(this.getContext(), s, Toast.LENGTH_SHORT).show();
			Utils.d(s);
			return;
		} 
		
		mChangedCells = mBoard.changeCell(point, mBoard.getTurn());
		mTurnningCells = new ArrayList<Cell>(mChangedCells);
		mTurnningCells.remove(currentCell);
		
		int nextAvailableCellCount = mBoard.changeTurn(mChangedCells); 
		if (nextAvailableCellCount == 0){
			if (mBoard.countBlankCells() == 0){				//全部のセルが埋まった場合は終了
				finish();											
			} else {
				showSkippMessage();					//スキップ
				nextAvailableCellCount = mBoard.changeTurn(mChangedCells);
				if (nextAvailableCellCount == 0){	//どちらも打つ場所が無くなった場合は終了
					finish();							
				}
			}
		}
		
		callPlayer();
			
		invalidate(currentCell.getRect());

		//裏返し処理用タイマースレッドを開始
		startTurnning();
	}
	
	@Override
	public void onEndThinking(final Point pos) {
		if (pos == null) return;
		if (pos.y < 0 || pos.x < 0) return;
		if (mPaused) return;
		
		if (mTurnningCells != null || mTurnningProgress > 0){
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run(){
					move(pos);
				}
			}, TURNING_TIME);
		} else {
			move(pos);
		}
	}
	
	@Override
	public void onProgress() {
		invalidate(0, (int)mBoard.getRectF().bottom, mWidth, mHeight);
	}
	
	@Override
	public void onPointStarted(Point pos) {
		Cell cell = mBoard.getCell(pos);
		invalidate(cell.getRect());			//変更された領域のみを再描画
	}
	
	@Override
	public void onPointEnded(Point pos) {
		Cell cell = mBoard.getCell(pos);
		invalidate(cell.getRect());			//変更された領域のみを再描画
	}
	
	private void finish(){
		mBoard.setFinished();
//		showCountsToast();
		
		invalidate();			
	}
	
	public void showCountsToast(){
		Cell.E_STATUS winner = mBoard.getWinnerStatus();
		String msg = "Black: " + mBoard.countCells(Cell.E_STATUS.Black) + "\n"
			+ "White: " + mBoard.countCells(Cell.E_STATUS.White) + "\n\n";
		
		if (mBoard.isFinished()){
			if (winner != Cell.E_STATUS.None){
				msg += "Winner is: " + Cell.statusToDisplay(winner) + "!!";
			} else {
				msg += "Draw game! ";
			}
		} else {
			if (winner != Cell.E_STATUS.None){
				msg += Cell.statusToDisplay(winner) + " is winning...\n\n";
			}
			msg += mBoard.getTurnDisplay() + "'s turn.";
		}
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
		Utils.d(msg);
	}
	
	private void showSkippMessage(){
		String msg = Cell.statusToDisplay(mBoard.getTurn()) + " has been skipped.";
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
		Utils.d(msg);
	}
	
	private void drawBoard(Canvas canvas){

		if (mBoard.getRectF().width() <= 0f ) return;
		
		float bleft = mBoard.getRectF().left;
		float btop = mBoard.getRectF().top;
		float bw = mBoard.getRectF().width();
		float bh = mBoard.getRectF().height();
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeight();

		//ボードの背景 
		canvas.drawBitmap(mBitmapBoard, bleft, btop, null);

		//縦線
		for (int i = 0; i < Board.COLS; i++) {
			canvas.drawLine(cw * (i+1) + bleft, btop, cw * (i+1) + bleft, bh + btop, mPaintBoardBorder);
		}
		//横線
		for (int i = 0; i < Board.ROWS; i++) {
			canvas.drawLine(bleft, ch * (i+1) + btop, bw + bleft, ch * (i+1) + btop, mPaintBoardBorder);
		}

		//全てのCellを描画
		drawCells(canvas, cw);
		
		//手番の表示、現在の黒と白の数の表示
		drawStatus(canvas);
	}
	
	//全てのCellについてコマが配置されていれば描く
	private void drawCells(Canvas canvas, float cw){
		boolean show_hints = Pref.getShowHints(getContext());

		Cell[][] cells = mBoard.getCells();
		for (int i = 0; i < Board.ROWS; i++) {
			for (int j = 0; j < Board.COLS; j++) {
				Cell cell =cells[i][j]; 
				Cell.E_STATUS st = cell.getStatus();
				
				if (st == E_STATUS.None){
					if (show_hints) drawHints(cell, canvas, cw);
				} else {
					drawStone(cell, canvas, cw, st);
				}
			}
		}
	}
	
	private void drawStone(Cell cell, Canvas canvas, float cw, Cell.E_STATUS st){
//		float w = cw * CELL_SIZE_FACTOR;
//		float cx = cell.getCx();
//		float cy = cell.getCy();
//		Paint pt = st == E_STATUS.Black ? mPaintCellFgB : mPaintCellFgW;  
//		canvas.drawCircle(cx, cy, w, pt);

		final float INSET = (cell.getWidth() * CELL_SIZE_FACTOR * 0.3f);
		float inset_w;
		Bitmap bm;
		
		
		if (mTurnningProgress > 0 && mTurnningCells != null && mTurnningCells.contains(cell)){
			RectF dest =  new RectF(cell.getRectF());
			Rect src = new Rect(0, 0, (int)cell.getWidth(), (int)cell.getHeight());
			
			if (mTurnningProgress > TURNNING_FREQ/2){
				inset_w = (float)(cell.getWidth() * (TURNNING_FREQ - mTurnningProgress) / TURNNING_FREQ);
				if (inset_w < 0f) inset_w = 0f;
				dest.inset(inset_w , 0f);
				bm = (st == E_STATUS.Black) ? mBitmapBlack : mBitmapWhite;
			} else {
				inset_w = (float)(cell.getWidth() * mTurnningProgress / TURNNING_FREQ);
				if (inset_w < 0f) inset_w = 0f;
				dest.inset(inset_w, 0f);
				bm = (st == E_STATUS.Black) ? mBitmapWhite : mBitmapBlack;
			}
			dest.offset(INSET, INSET);
			canvas.drawBitmap(bm, src, dest, null);
			
		} else {
			bm = (st == E_STATUS.Black) ? mBitmapBlack : mBitmapWhite;
			canvas.drawBitmap(bm, cell.getLeft()+INSET, cell.getTop()+INSET, null);
		}
	
	}
	
	private void drawHints(Cell cell, Canvas canvas, float cw){
		if (cell.getReversibleCells().size() == 0) 
			return;
		
		//次に配置可能なセルであれば小さな丸を表示する
		float aw = cw * 0.1f;
		Paint pt = mBoard.getTurn() == Cell.E_STATUS.Black ? mPaintCellAvB : mPaintCellAvW;
		canvas.drawCircle(cell.getCx(), cell.getCy(), aw, pt);
	}

	private void drawStatus(Canvas canvas){
		Resources res = getResources();  
		float turn_rect_inset = res.getDimension(R.dimen.turn_rect_inset); 
		float turn_rect_round = res.getDimension(R.dimen.turn_rect_round); 
		float turn_circle_x = res.getDimension(R.dimen.turn_circle_x); 
		float turn_circle_y = res.getDimension(R.dimen.turn_circle_y); 
		float turn_text_x = res.getDimension(R.dimen.turn_text_x); 
		float turn_text_y = res.getDimension(R.dimen.turn_text_y); 
		float top = mBoard.getRectF().bottom + mBoard.getRectF().top;
		float center = mBoard.getRectF().width() / 2f;

		//ボード以外の余白部分の背景
//		Shader shader = new LinearGradient(mWidth/2, top + (mHeight - top) * 0.5f, mWidth/2, mHeight, mPaintScreenBg.getColor(), mPaintScreenBg2.getColor(), Shader.TileMode.CLAMP);  
		Shader shader = new RadialGradient(mWidth/2f, top * 0.9f, mWidth * 0.7f, mPaintScreenBg2.getColor(), mPaintScreenBg.getColor(), Shader.TileMode.CLAMP);  
		Paint  paint = new Paint( mPaintScreenBg);  
		paint.setShader(shader);  
		canvas.drawRect(0, top, mWidth, mHeight, paint);


		if (!mBoard.isFinished()){
			RectF rect;
			if (mBoard.getTurn() == E_STATUS.Black){
				rect = new RectF(turn_rect_inset, top + turn_rect_inset, center - turn_rect_inset, mHeight - turn_rect_inset);
			} else {
				rect = new RectF(center + turn_rect_inset, top + turn_rect_inset, mWidth - turn_rect_inset, mHeight - turn_rect_inset);
			}
			mPaintTurnRect.setStyle(Style.FILL);
			mPaintTurnRect.setAlpha(128);
			canvas.drawRoundRect(rect, turn_rect_round, turn_rect_round, mPaintTurnRect);	//背景
			mPaintTurnRect.setStyle(Style.STROKE);
			mPaintTurnRect.setAlpha(255);
			canvas.drawRoundRect(rect, turn_rect_round, turn_rect_round, mPaintTurnRect);	//枠
		}

		float circle_w = mBoard.getCellWidth() * CELL_SIZE_FACTOR;
		
		//黒の円
		canvas.drawCircle(turn_circle_x, top + turn_circle_y, circle_w, mPaintCellFgB);
		
		//白の円（外枠付）
		canvas.drawCircle(center + turn_circle_x, top + turn_circle_y, circle_w, mPaintCellFgB);
		canvas.drawCircle(center + turn_circle_x, top + turn_circle_y, circle_w * 0.94f, mPaintCellFgW);

		//各プレーヤーのコマ数を表示
		int fontSize = res.getDimensionPixelSize(R.dimen.font_size_status); 
		mPaintTextFg.setTextSize(fontSize);
		String s = String.valueOf(mBoard.countCells(E_STATUS.Black));
		canvas.drawText(s, turn_text_x, top + turn_text_y, mPaintTextFg);				//黒のコマ数
		s = String.valueOf(mBoard.countCells(E_STATUS.White));
		canvas.drawText(s, center + turn_text_x, top + turn_text_y, mPaintTextFg);		//白のコマ数

		//各プレーヤーの名前を表示
		int fontSizeName = res.getDimensionPixelSize(R.dimen.font_size_name); 
		mPaintTextFg.setTextSize(fontSizeName);
		canvas.drawText(mBoard.getPlayer1().getName(), turn_circle_x, top + turn_text_y*1.8f, mPaintTextFg);			//黒の名前
		canvas.drawText(mBoard.getPlayer2().getName(), center + turn_circle_x, top + turn_text_y*1.8f, mPaintTextFg);   //白の名前					

		
		//コンピュータの思考中の場合は進捗状況を表示。
		Player p = mBoard.getCurrentPlayer();
		if (p != null && !p.isHuman()){
//			mPaintTextFg.setTextSize(fontSize);
//			s = String.valueOf(mBoard.getCurrentPlayer().getProgress()) + "%"; 
//			canvas.drawText(s, rect.left + turn_circle_x, top + turn_text_y*2.5f, mPaintTextFg);	

			//現在思考中のセルを赤丸で表示。
			Cell cell = p.getCurrentCell();
			if (cell != null){
				canvas.drawCircle(cell.getCx(), cell.getCy(),  mBoard.getCellWidth() * CELL_SIZE_FACTOR_PRG, mPaintCellCur);
				invalidate(cell.getRect()); 
			}
		} 
		
		if (mBoard.isFinished()){
			mPaintTextFg.setTextSize(fontSize);
			drawWinner(canvas);
		}
		
	}
	
	private void drawWinner(Canvas canvas){
		String s;
		Player winner = mBoard.getWinner();
		if (winner != null){
			s = winner.getName() + " wins! ";
		} else {
			s = "Draw game! ";
		}

//		//盤面全体をグレーアウト
//		Paint paintBg = new Paint();
//		paintBg.setColor(Color.BLACK);
//		paintBg.setAlpha(128);
//		canvas.drawRect(mBoard.getRectF(), paintBg);
		
		GameActivity activity =  (GameActivity)this.getContext();
		activity.showWinner(s);
	}

	
	public String getState(){
		String s = mBoard.getStateString();
		Utils.d("getState: state=" + s);
		return s;
	}
	
	public void pause(){
		mPaused = true;
		
		Player p = mBoard.getCurrentPlayer();
		if (p != null && !p.isHuman()){
			//別スレッドで思考ルーチンが動いていれば中断する。
			p.stopThinking();
		}
	}
	
	public void resume(String state){
		Utils.d("onResume: state=" + state);

		mPaused = false;
		if (!TextUtils.isEmpty(state)){
			mBoard.loadFromStateString(state);

			mBoard.setPlayer1(Player.getPlayer1(getContext(), mBoard, E_STATUS.Black));
			mBoard.setPlayer2(Player.getPlayer2(getContext(), mBoard, E_STATUS.Black));
		}

		callPlayer();
	}
	
	private void callPlayer(){
		if (mPaused) return;
		
		Player p = mBoard.getCurrentPlayer();
		if (p != null && !p.isHuman()){
			p.startThinking(this);
		}
	}

	private void startTurnning(){
		new Thread(this).start();
	}
	
	//石を裏返すアニメーションを別スレッドで処理する
	@Override
	public void run() {
		
		if (mTurnningCells != null && mTurnningCells.size() >0){
			for (mTurnningProgress = 1; mTurnningProgress <= TURNNING_FREQ; mTurnningProgress++){
				//ハンドラにUIスレッド側で実行する処理を渡す。
				mHandler.post(new Runnable(){
					@Override
					public void run(){
						if (mTurnningCells != null){
							for (Cell cell : mTurnningCells) {
								invalidate(cell.getRect());			//変更された領域のみを再描画
							}
						}
					}
				});
				try {
					Thread.sleep(TURNING_TIME / TURNNING_FREQ);
				} catch (Exception e) {
					Utils.d(e.getMessage());
				}
			}
		}
		mTurnningCells = null;
		mTurnningProgress = 0;

//		try {
//			Thread.sleep(TURNING_TIME / TURNNING_FREQ);
//		} catch (Exception e) {
//			Utils.d(e.getMessage());
//		}

		mHandler.post(new Runnable(){
			@Override
			public void run(){
				if (mChangedCells != null){
					for (Cell cell : mChangedCells) {
						invalidate(cell.getRect());			//変更された領域のみを再描画
					}
				}
			
				//画面下部のステータス表示領域を再描画
				invalidate(0, (int)mBoard.getRectF().bottom, mWidth, mHeight);
			}
		});
	}

}
