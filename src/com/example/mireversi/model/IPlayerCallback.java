/*
* Copyright (c) 2011 Makoto Ishida
* Please see the file MIT-LICENSE.txt for copying permission.
*/

package com.example.mireversi.model;

import android.graphics.Point;

public abstract interface IPlayerCallback {
	
	public void onEndThinking(Point pos);
	public void onProgress();
	public void onPointStarted(Point pos);
	public void onPointEnded(Point pos);
}
