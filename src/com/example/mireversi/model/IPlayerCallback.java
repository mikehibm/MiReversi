package com.example.mireversi.model;

import android.graphics.Point;

public abstract interface IPlayerCallback {
	
	public void onEndThinking(Point pos);
}
