package com.example.mireversi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MainActivity extends Activity {
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
	    	if (mScrWidth <=0 ) return;

	    	Paint paint = new Paint();
	    	paint.setColor(Color.rgb(0, 180, 0));
	    	
	    	canvas.drawRect( 0, 0, mScrWidth, mScrHeight, paint);

	    	paint.setColor(Color.BLACK);
	    	for (int i = 0; i < 8; i++) {
				canvas.drawLine( (mScrWidth / 8) * (i+1), 
								 0, 
								 (mScrWidth / 8) * (i+1), 
								 mScrHeight, 
								 paint);
			}
	    	for (int i = 0; i < 8; i++) {
				canvas.drawLine( 0,
								 (mScrHeight / 8) * (i+1),
								 mScrWidth,
								 (mScrHeight / 8) * (i+1),
								 paint);
			}
	    	
	    	paint.setColor(Color.BLACK);
	        canvas.drawCircle(mScrWidth/2 - (mScrWidth/8/2), mScrHeight/2 - (mScrHeight/8/2), (mScrWidth/2 / 8), paint);
	        canvas.drawCircle(mScrWidth/2 + (mScrWidth/8/2), mScrHeight/2 + (mScrHeight/8/2), (mScrWidth/2 / 8), paint);
	    	
	    	paint.setColor(Color.WHITE);
	        canvas.drawCircle(mScrWidth/2 - (mScrWidth/8/2), mScrHeight/2 + (mScrHeight/8/2), (mScrWidth/2 / 8), paint);
	        canvas.drawCircle(mScrWidth/2 + (mScrWidth/8/2), mScrHeight/2 - (mScrHeight/8/2), (mScrWidth/2 / 8), paint);
	    	
	    }
	    

//        public boolean onTouchEvent(MotionEvent event) {
//            float x = event.getX();
//            float y = event.getY();
//            switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x0 = x;
//                y0 = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                dx = x - x0;
//                dy = y - y0;
//                break;
//            case MotionEvent.ACTION_UP:
//                xOffset += (x - x0);
//                yOffset += (y - y0);
//                dx = 0;
//                dy = 0;
//                break;
//            default:
//                return true;
//            }
//            doDraw(getHolder());
//            return true;
//        }

	}
    
}