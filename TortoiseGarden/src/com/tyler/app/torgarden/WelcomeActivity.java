package com.tyler.app.torgarden;

import android.os.Bundle;
import android.app.Activity;
import java.util.ArrayList;



import utils.AppHelper;
import utils.AppHelper.Orientation;
import utils.AppHelper.SensorRate;
import utils.AppHelper.SensorType;
import utils.ITicker;
import utils.Timer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import biga.shapes2D.Circle;
import biga.utils.Constants;
import biga.utils.GeomUtils;
import java.util.*;
import android.os.Vibrator;  
import android.util.DisplayMetrics;
import java.math.*;
import android.graphics.Bitmap; 
import android.graphics.Bitmap.Config; 
import android.graphics.BitmapFactory; 
import android.graphics.Matrix;
import android.content.Intent;

public class WelcomeActivity extends Activity
{
	WelcomeView welView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
		//	this.setContentView(R.layout.activity_android_ball_physics);
		welView=new WelcomeView(this);
		LayoutParams full = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//   physics.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
		addContentView(welView, full);
		// setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
	/*
		welView.setOnClickListener(new View.OnClickListener(){

				public void onClick(View p1)
				{
					go();
				}
			});
		welView.setOnLongClickListener(new View.OnLongClickListener(){

				public boolean onLongClick(View p1)
				{
				go();
					return true;
				}
				
				
			});*/
	}

	public void go()
	{

		Intent intent= new Intent(WelcomeActivity.this, MainActivity.class);
		//	intent.putExtra("cmd", 0);
		//	intent.putExtra("id",  Integer.parseInt(v.getTag().toString()));
		startActivity(intent);

	}
	/*
	@Override
	public void onResume(){
		if(welView!=null)
		welView.resume();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		go();
		return true;
	}
	*/
}
