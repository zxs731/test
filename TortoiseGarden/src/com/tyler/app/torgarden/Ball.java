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
public  class Ball extends Circle
{
	public	int color1;
	public	int color2;
	public double vx = 0, vy = 0;

	public boolean dragging = false;
	public ArrayList<FootTrace> FootTraces=null;

	public Ball(double x, double y, double radius)
	{
		super(x, y, radius);
		traceList = new ArrayList<Ball>();
		this.FootTraces = new ArrayList<FootTrace>();
	}
	protected boolean isInBall(float x, float y)
	{
		return radius > Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
	}
	private int increase=-1;
	private float scale=1;
	public void change()
	{

		if (skin != null)
		{
			int skinWidth=skin.getWidth();
			if (scale >= 1)
				increase = -1;
			else if (scale <= 0.8)
			{
				increase = 1;
			}
			//	this.radius += increase * 1;
			scale += (float)increase / 50;
			/*
			 int scaleWidth=(int) (scale * skinWidth);   
			 if (scaleWidth <= 0)
			 {
			 radius = 3;
			 }
			 else
			 */
			{

				Matrix matrix = new Matrix();  
				matrix.postScale(scale, scale);  
				rotatedSkin = Bitmap.createBitmap(skin, 0, 0, skinWidth , skinWidth, matrix, true);   
				this.radius = rotatedSkin.getWidth() / 2;
			}
		}

	}
	public ArrayList<Ball> traceList;
	public float rotateAngle;
	public void moveToNext()
	{
		ArrayList<Ball> needClear=new ArrayList<Ball>();
		Ball next=null;
		float dis=0;
		Ball pre=null;//myPlane;
		Ball pre2=null;
		Ball myPlane=this;
		double l=Math.sqrt((myPlane.vx * myPlane.vx) + (myPlane.vy * myPlane.vy));// * 30 / 1000 ;
		float startx=myPlane.x;
		float starty=myPlane.y;
		//int i=0;
		while (traceList.size() > 0)//Ball b:traceList)
		{
			Ball b=traceList.get(0);
			if (pre == null)
				pre = myPlane;

			dis += Math.sqrt((pre.x - b.x) * (pre.x - b.x) + (pre.y - b.y) * (pre.y - b.y));
			if (dis < l)
			{
				pre2 = pre;
				pre = b;
				traceList.remove(0);

			}
			else
			{
				//	float nx=(float)((b.x-myPlane.x)*l/dis+myPlane.x);
				//	float ny=(float)((b.y-myPlane.y)*l/dis+myPlane.y);

				next = b;
				break;
			}
		}
		/*
		 for (int i=0;i<traceList.size();i++)//Ball b:traceList)
		 {
		 //Ball b=traceList.get(i);
		 if (traceList.indexOf(b) > 0)
		 {
		 pre = traceList.get(traceList.indexOf(b) - 1);
		 }
		 dis += Math.sqrt((pre.x - b.x) * (pre.x - b.x) + (pre.y - b.y) * (pre.y - b.y));
		 if (dis<Math.sqrt((myPlane.vx * myPlane.vx) + (myPlane.vy * myPlane.vy)) * 30 / 1000 )
		 {
		 needClear.add(b);
		 }
		 else
		 {
		 next = b;
		 break;
		 }
		 }
		 */

		if (next != null)
		{
			//	this.x = next.x;
			//	this.y = next.y;
			float rad=(float)Math.atan2((-1) * (pre.x - next.x), pre.y - next.y);//.atan( (-1)*(pre.x-next.x)/(pre.y-next.y));
			rotateAngle = radToDegree(rad);
			this.rotatedSkin = rotateBitmap(this.skin, rotateAngle);
			/*
			 //reset x,y vx,vy
			 double lastpart=Math.sqrt((next.x-pre.x)*(next.x-pre.x)+(next.y-pre.y)*(next.y-pre.y));
			 double adddis=lastpart-( dis-l);
			 this.x=(float)( Math.sin(rad)*adddis)+pre.x;
			 this.y=(float)( Math.cos(rad)*adddis)+pre.y;
			 this.vx=l*Math.sin(rad);
			 this.vy=l*Math.cos(rad);
			 */

			float nx=(float)((next.x - startx) * l / dis + startx);
			float ny=(float)((next.y - starty) * l / dis + starty);
			this.x = nx;
			this.y = ny;
			float k=(float)Math.sqrt((l * l) / ((nx - startx) * (nx - startx) + (ny - starty) * (ny - starty)));
			this.vx = (nx - startx) * k;
			this.vy = (ny - starty) * k;


		}
		else if (dis < l && pre != null)
		{
			//没有超过最小距离，则按最后一个轨迹点的方向前进

			float nx=(float)((pre.x - startx) * l / dis + startx);
			float ny=(float)((pre.y - starty) * l / dis + starty);
			this.x = nx;
			this.y = ny;
			float k=(float)Math.sqrt((l * l) / ((nx - startx) * (nx - startx) + (ny - starty) * (ny - starty)));
			this.vx = (nx - startx) * k;
			this.vy = (ny - starty) * k;

			/*
			 float rad=(float)Math.atan2((-1) * (pre2.x - pre.x), pre2.y - pre.y);//.atan( (-1)*(pre.x-next.x)/(pre.y-next.y));
			 rotateAngle = radToDegree(rad);
			 this.rotatedSkin = rotateBitmap(this.skin, rotateAngle);
			 //reset x,y vx,vy
			 //	double lastpart=Math.sqrt((next.x-pre.x)*(next.x-pre.x)+(next.y-pre.y)*(next.y-pre.y));
			 double adddis=l-dis;
			 this.x=(float)( Math.sin(rad)*adddis)+pre.x;
			 this.y=(float)( Math.cos(rad)*adddis)+pre.y;
			 this.vx=l*Math.sin(rad);
			 this.vy=l*Math.cos(rad);
			 */
		}


	}
	private Bitmap rotateBitmap(Bitmap bmp, float angle)
	{
		Matrix matrix=new Matrix();
		matrix.setRotate(angle);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
	}
	public float radToDegree(float rad)
	{

		return (float) (180 * rad / Math.PI);
	}
	public void AddNewTrace(float x, float y)
	{
		Ball b=new Ball(x, y, 5);
		b.color1 = 0;
		b.color2 = 1;
		traceList.add(b);
	}
	public Bitmap skin;
	private Bitmap rotatedSkin;
	public void DrawSkin(Canvas canvas, Paint paint)
	{
		if (rotatedSkin == null)
			rotatedSkin = skin;
		if (rotatedSkin != null)
			canvas.drawBitmap(rotatedSkin, this.x - rotatedSkin.getWidth() / 2
							  , this.y - rotatedSkin.getHeight() / 2, paint);
		/*			
		 if (b.radius >= 15 && b.radius <= 21)
		 canvas.drawBitmap(e20bmp, b.x - e20bmp.getWidth() / 2, b.y - e20bmp.getHeight() / 2, paint);
		 else if (b.radius > 21 && b.radius <= 29)
		 canvas.drawBitmap(e30bmp, b.x - e30bmp.getWidth() / 2, b.y - e30bmp.getHeight() / 2, paint);
		 else 
		 canvas.drawBitmap(e40bmp, b.x - e40bmp.getWidth() / 2, b.y - e40bmp.getHeight() / 2, paint);
		 */			
	}
	public void RotateSkin(float angle)
	{
		this.rotatedSkin = rotateBitmap(this.skin, angle);
	}
	
	/*
	 private class Button1 extends ClickBall
	 {
	 public Button1(double x, double y, int radius)
	 {
	 super(x, y, radius);
	 }
	 public void perform(float eventx, float eventy)
	 {
	 if (isOn && _pool != null)
	 {
	 Ball selBall=null;
	 for (Ball b:_pool)
	 {
	 if (b.isInBall(eventx, eventy))
	 {
	 selBall = b;
	 break;
	 }
	 }
	 if (selBall != null)
	 {
	 selBall.x = eventx;
	 selBall.y = eventy;
	 }
	 }
	 }}
	 */
}
