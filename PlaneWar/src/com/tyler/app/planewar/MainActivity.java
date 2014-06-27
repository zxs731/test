package com.tyler.app.planewar;

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



public class MainActivity extends Activity
{

	BallPhysics physics;
	MainActivity mainActivity;
	Date starttime;
	int score;
	String statusmsg="";
	Vibrator vibrator=null ;
	Bitmap e20bmp;
	Bitmap e30bmp;
	Bitmap e40bmp;
	Bitmap p56bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE); // ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        AppHelper.fullScreen(this);
        AppHelper.orientation(this, Orientation.PORTRAIT);
		DisplayMetrics dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		UtilityHelper.SCREEN_HEIGHT = dm.heightPixels; // 获取具体的屏幕分辨率数??
		UtilityHelper.SCREEN_WIDTH = dm.widthPixels;


        setContentView(R.layout.activity_android_ball_physics);

		physics = new BallPhysics(this);
        LayoutParams full = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        addContentView(physics, full);
        mainActivity = this;

	//	BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inPreferredConfig = Config.ALPHA_8;
		e20bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy20);  
		e30bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy30);
//		e30bmp=e30bmp.copy
//		(Config.ARGB_8888, true);
	
//	e30bmp.eraseColor(Color.BLACK);
		e40bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy40);
		p56bmp = BitmapFactory.decodeResource(getResources(), R.drawable.p56);
    //    p56bmp.eraseColor(Color.BLACK);


        //new thread
        new Thread(new Runnable(){

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					AppHelper.enableSensor(mainActivity, physics, SensorType.ACCELEROMETER, SensorRate.GAME);
					vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);  
				}


			}).start();



    }

    @Override
    public void onAttachedToWindow()
	{
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
	@Override
	public void onStop()
	{
		super.onStop();
		if (physics != null)
	    	physics.stop();

	}
	@Override
	public void onPause()
	{
		super.onPause();
		if (physics != null)
		//	super.onPause();
			physics.pause();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		if (physics != null)
			physics.resume();
	}

    public class BallPhysics extends View implements ITicker, SensorEventListener
    {
        private ArrayList<Ball> pool;
		private ArrayList<Ball> enemyPool;
		private ArrayList<Ball> myBulletPool;
		private ArrayList<Ball> eBulletPool;
		private MyPlane myPlane;
        private int maxBalls = 400;
		private int maxBallsswitch=40;
		private boolean startremove=false;
		ArrayList<Ball> needremove=new ArrayList<Ball>();

        private Paint paint;
        private int colorId;
        private int[] colors;

        private double collisionDamping = .5;
        public double damping = .8;
        public double spin = 1;
        public double spinRadian = spin * Math.PI / 180;

        private biga.Point gravity = new biga.Point(0, 1);
		//record for gravity
		private float gx;
		private float gy;
		private float gz;
		private int totalArea;
		private ArrayList<ClickBall> functionBalls;
		private int life=100;
		private int tscore=0;
		Timer timer;


        public BallPhysics(Context context)
        {
            super(context);
            init(context);

        }
		public void pause()
		{
			if (timer != null)
				timer.pause();
		}
		public void resume()
		{
			if (timer != null)
				timer.start();
		}
		public void stop()
		{

			if (timer != null)
				timer.stop();
		}
        private void init(Context context)
        {
            setFocusable(true);

            paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);
            rgbColor(0xFFCC00);

            colorId = 0;
            colors = new int[] {
//            		0x003EBA, 0x5D7CBA, 0x8B9BBA, 0x29447B, 0x14223D, 0x455D8B, 0x7986A1, 
//                    0xFF9000, 0xFFC77F, 0xFFE3BF, 0xA97838, 0x543C1C, 0xBF955F, 0xDDC5A6 
				0xFF0000,0xFF8000,0xFFD700,0x006600,0x00FF00,0x6B8E23,0x00FFFF,
				0x66B2FF,0x0000FF,0x7F00FF,0xFF00FF,0xCC0066,0xFF9999,0x808080
            };
			statusmsg = getResources().getString(
				R.string.timekeeping);
            pool = new ArrayList<Ball>();
		    needremove = new ArrayList<Ball>();
			myBulletPool = new ArrayList<Ball>();
			eBulletPool = new ArrayList<Ball>();
			enemyPool = new ArrayList<Ball>();
			//    initalFuncBalls();
			InitialMyPlane();
			initialEnemy();

            timer = new Timer(30, this);
            timer.start();

        }
		private void InitialMyPlane()
		{
			myPlane = new MyPlane(UtilityHelper.SCREEN_WIDTH / 2
								  , UtilityHelper.SCREEN_HEIGHT * 3 / 4
								  , 60);
			myPlane.color1 = 0;
			myPlane.color2 = 1;
			//pool.add(myPlane);
		}
		private void initialEnemy()
		{
			for (int i=0;i < 10;i++)
			{
				Ball eb=new Ball(Math.random() * UtilityHelper.SCREEN_WIDTH
								 , 0, 15 + Math.random() * 20);
				eb.color1 = 6;
				eb.color2 = 4;
				eb.vy = Math.random() * 10;
				eb.vx = 0;
				enemyPool.add(eb);
			}
		}

		private void initalFuncBalls()
		{
			functionBalls = new ArrayList<ClickBall>();
			ClickBall clickBall=new Button1(125, 90, 35);
			clickBall.color1 = Color.GRAY;//(colorId ) % colors.length;
			clickBall.color2 = Color.LTGRAY;//(colorId + 1 ) % colors.length;
			clickBall.setBallsPool(pool);
			functionBalls.add(clickBall);
		}
		private void drawFuncBalls(Canvas canvas)
		{
			//彩球顺序
			for (ClickBall b:functionBalls)
			{
				//rgbColor(colors[b.color1]);
				paint.setColor(b.color1);
                b.draw(canvas, paint);
				paint.setColor(b.color2);
				canvas.drawCircle(b.x, b.y, (float)(b.radius * Constants.oneByGoldenRatio), paint);
				paint.setColor(Color.WHITE);
				paint.setTextSize(18); 
				canvas.drawText("Mode2", b.x - 95, b.y, paint);
				if (b.isOn)
					canvas.drawText("On", b.x, b.y, paint);
				else
					canvas.drawText("Off", b.x, b.y, paint);

            }
		}
        @Override
        public void tick()
        {

            update();
            invalidate();

        }
		private void drawvballs(Canvas canvas)
		{
			//彩球顺序
			for (int ii=0;ii < 20;ii++)
			{

				int y=ii * 20 + 40;
				Ball nb=new Ball(20, y	, 10);
				rgbColor(colors[(colorId + 1 + ii) % colors.length]);
                nb.draw(canvas, paint);
				rgbColor(colors[(colorId + 2 + ii) % colors.length]);
				canvas.drawCircle(nb.x, nb.y, (float)(nb.radius * Constants.oneByGoldenRatio), paint);

            }
		}
		private int getBallArea(Ball ball)
		{
			return (int)(ball.radius * ball.radius * 3.14159);
		}
		private void DrawBall(Ball myPlane, Canvas canvas)
		{
			rgbColor(colors[myPlane.color1]);
			myPlane.draw(canvas, paint);
			//	rgbColor(colors[myPlane.color2]);
			//canvas.drawCircle(myPlane.x, myPlane.y, (float)(myPlane.radius * Constants.oneByGoldenRatio), paint);
		}
		private void DrawEnemy(Ball myPlane, Canvas canvas)
		{
	//		rgbColor(colors[myPlane.color1]);
	//		myPlane.draw(canvas, paint);
			//	rgbColor(colors[myPlane.color2]);
			//canvas.drawCircle(myPlane.x, myPlane.y, (float)(myPlane.radius * Constants.oneByGoldenRatio), paint);
		}
		private void DrawMyplane(Ball myPlane, Canvas canvas)
		{
	//		rgbColor(colors[myPlane.color1]);


	//		myPlane.draw(canvas, paint);
			//	rgbColor(colors[myPlane.color2]);

			//	canvas.drawCircle(myPlane.x, myPlane.y, (float)(myPlane.radius * Constants.oneByGoldenRatio), paint);
			/*
			 int rwidth=6;
			 int rheight=90;	
			 float rx=myPlane.x - rwidth / 2;
			 float ry=myPlane.y - rheight / 2;
			 canvas.drawRect(rx, ry, rx + rwidth, ry + rheight, paint);	
			 */
		}
        @Override
        protected void onDraw(Canvas canvas)
        {
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			for (Ball b:enemyPool)
			{
				//DrawBall(b, canvas);
				if (b.radius >= 15 && b.radius <= 21)
					canvas.drawBitmap(e20bmp, b.x - e20bmp.getWidth()/2, b.y - e20bmp.getHeight()/2, paint);
				else if (b.radius > 21 && b.radius <= 29)
					canvas.drawBitmap(e30bmp, b.x - e30bmp.getWidth()/2, b.y - e30bmp.getHeight()/2, paint);
				else 
					canvas.drawBitmap(e40bmp, b.x - e40bmp.getWidth()/2, b.y - e40bmp.getHeight()/2, paint);
				DrawEnemy(b,canvas);
			}
			paint.setStyle(Paint.Style.FILL);
			for (Ball eb:eBulletPool)
			{
				DrawBall(eb, canvas);
			}
			for (Ball bu:myBulletPool)
			{
				DrawBall(bu, canvas);
			}
			paint.setStyle(Paint.Style.STROKE);
			//DrawMyplane(myPlane, canvas);
			canvas.drawBitmap(p56bmp, myPlane.x - p56bmp.getWidth()/2, myPlane.y - p56bmp.getHeight()/2, paint);
			DrawMyplane(myPlane,canvas);
			String gmsg="Life={1}, Score={2}, E={3}, EB={4}, MB={5}";
			gmsg = gmsg.replace("{1}", "" + life)
				.replace("{2}", "" + tscore)
				.replace("{3}", "" + enemyPool.size())
				.replace("{4}", "" + eBulletPool.size())
				.replace("{5}", "" + myBulletPool.size());
			paint.setColor(Color.WHITE);
			paint.setTextSize(30); 
			canvas.drawText(gmsg, 5, 25, paint);	

			/*
			 //  int id = colorId;
			 int ballsarea=0;
			 totalArea = getHeight() * getWidth();
			 for (Ball b : pool)
			 {
			 ballsarea += getBallArea(b);
			 rgbColor(colors[b.color1]);

			 b.draw(canvas, paint);
			 paint.setShader(null);

			 //change to circle color
			 rgbColor(colors[b.color2]);

			 float bcx=b.x;
			 float bcy=b.y;
			 //draw circle
			 canvas.drawCircle(b.x, b.y, (float)(b.radius * Constants.oneByGoldenRatio), paint);

			 //canvas.drawRect(new RectF(b.x-c, b.y-c, b.x+c, b.y+(b.radius-c)), paint);
			 paint.setShader(null);
			 //draw circle end
			 paint.setColor(Color.BLACK);
			 //canvas.drawText(b.color1+","+b.color2, b.x, b.y, paint);
			 }
			 */
			/*
			 if (colorId > 0)
			 {
			 //draw score
			 java.util.Date now = new java.util.Date(System.currentTimeMillis());
			 float timeUsing = ((float) (now.getTime() - starttime
			 .getTime()) / 1000);
			 timeUsing = (float) (Math.round(timeUsing * 10)) / 10;
			 String msg = statusmsg. replace("{0}",
			 timeUsing + "")
			 .replace("{1}", score + "")
			 .replace("{2}", startremove + "")
			 .replace("{3}", pool.size() + "")
			 //	.replace("{4}",maxBallsswitch+"");
			 .replace("{4}", ballsarea * 100 / totalArea + "%");
			 //paint.setTextAlign(Align.CENTER);
			 paint.setTextSize(18); 
			 paint.setColor(Color.WHITE);
			 canvas.drawText(msg, 35, 15, paint);
			 //彩球顺序
			 for (int ii=0;ii < 20;ii++)
			 {

			 int x=ii * 20 + 40;
			 Ball nb=new Ball(x, 40, 10);
			 rgbColor(colors[(colorId + 1 + ii) % colors.length]);
			 nb.draw(canvas, paint);
			 rgbColor(colors[(colorId + 2 + ii) % colors.length]);
			 canvas.drawCircle(nb.x, nb.y, (float)(nb.radius * Constants.oneByGoldenRatio), paint);

			 }
			 //垂直顺序
			 drawvballs(canvas);
			 }
			 */
			//draw comments x;y
			/*
			 for(Ball b:pool)
			 {
			 //radial line
			 paint.setColor(Color.WHITE);
			 canvas.drawLine(b.x,b.y,b.x+b.radius,b.y-b.radius,paint);
			 canvas.drawLine(b.x+b.radius,b.y-b.radius,b.x+b.radius+65,b.y-b.radius,paint);
			 canvas.drawText(""+(int)b.x+";"+(int)b.y, b.x+b.radius+5, b.y-b.radius-5, paint);
			 }
			 */

			/*
			 //x.y.z gravity value print
			 String gmsg="x={1}, y={2}, z={3}";
			 gmsg=gmsg.replace("{1}",""+gx)
			 .replace("{2}",""+gy)
			 .replace("{3}",""+gz);
			 paint.setColor(Color.WHITE);
			 paint.setTextSize(20); 
			 canvas.drawText(gmsg, 5, 45, paint);
			 */
			/*
			 biga.Point p0=new biga.Point();
			 p0.x = 200;
			 p0.y = 350;
			 biga.Point py=new biga.Point();
			 biga.Point px=new biga.Point();
			 biga.Point pz=new biga.Point();
			 py.x = p0.x;
			 py.y = p0.y + gy * 10 * (-1);
			 px.x = p0.x + gx * 10;
			 px.y = p0.y;
			 float part=(float)(gz * 10 / Math.sqrt(2));
			 pz.x = p0.x + part * (-1);
			 pz.y = p0.y + part;
			 paint.setStrokeWidth(5);
			 canvas.drawLine(p0.x, p0.y, py.x, py.y, paint);
			 canvas.drawLine(p0.x, p0.y, px.x, px.y, paint);
			 canvas.drawLine(p0.x, p0.y, pz.x, pz.y, paint);
			 paint.setStrokeWidth(0);
			 canvas.drawText("y: " + gy + " (m/s2)", py.x + 20, py.y, paint);
			 canvas.drawText("x: " + gx + " (m/s2)", px.x, px.y - 20, paint);
			 canvas.drawText("z: " + gz + " (m/s2)", pz.x + 20, pz.y + 20, paint);

			 drawFuncBalls(canvas);
			 */
		}



        private void update()
        {
			//	boolean issame;
			//	if (startremove)
			//    	needremove.clear();
			myPlaneUpdate();
			myBulletUpdate();
			enemyUpdate();
			eBulletUpdate();
		}
		private void eBulletUpdate()
		{
			boolean isHit=false;

			ArrayList<Ball> clear=new ArrayList<Ball>();	
			for (Ball a:eBulletPool)
			{
				if (a.y > UtilityHelper.SCREEN_HEIGHT
					|| a.x < 0 || a.x > UtilityHelper.SCREEN_WIDTH
					|| a.y < 0)
				{
					clear.add(a);
					continue;
				}
				if (coarseCollision(a, myPlane))
				{
					isHit = true;
					life -= 1;
					clear.add(a);
					continue;
				}
				a.x += a.vx;
				a.y += a.vy;
			}
			if (clear.size() > 0)
			{
				clear(eBulletPool, clear);
			}
			if (isHit)
				vibrate();
		}
		private void enemyUpdate()
		{
			ArrayList<Ball> clear=new ArrayList<Ball>();
			boolean crash=false;
			

			for (Ball a:enemyPool)
			{
				//bounds check
				if (a.y > getHeight())
				{
					a.y = 0;
					a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;

					continue;
				}	
				//撞毁
				if (coarseCollision(a, myPlane))
				{
				//	clear.add(a);
				crash=true;
					a.y = 0;
					a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
					a.vy=Math.random()*10;
					life -= 5;
					continue;
				}
				//敌人移动
				a.x += a.vx;
				a.y += a.vy;
				//发射
				if (Math.random() * 100 > 97)
				{
					Ball ebu=new Ball(a.x, a.y, 8);
					ebu.color1 = 6;
					ebu.color2 = 5;
			        if (myPlane.y < a.y)
					    ebu.vy = (-1) * (a.vy + 20);
					else
						ebu.vy = a.vy + 20;
					ebu.vx =	(myPlane.x - ebu.x) / ((myPlane.y - ebu.y) / ebu.vy);
					eBulletPool.add(ebu);
				}


			}
		//	if (clear.size() > 0)
		    if(crash)
			{
			//	clear(enemyPool, clear);
				//震动
				vibrate();
			}
		}
		private void vibrate()
		{
			//new thread
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						if (vibrator != null)
							vibrator.vibrate(100);
					}
				}).start();
		}
		private void clear(ArrayList<Ball> container, ArrayList<Ball> clear)
		{
			for (Ball b:clear)
			{
				container.remove(b);
			}
		}
		private void myBulletUpdate()
		{
			ArrayList<Ball> needRemoveBalls=new ArrayList<Ball>();
			ArrayList<Ball> needRemoveEnemy=new ArrayList<Ball>();
			for (Ball a:myBulletPool)
			{
				//bounds check
				if (a.x > getWidth() || a.y > getHeight() || a.x < 0 || a.y < 0)
				{
					needRemoveBalls.add(a);
					continue;
				}	
				//击中
				boolean hit=false;
				for (Ball eb:enemyPool)
				{
					if (Math.sqrt((eb.x - a.x) * (eb.x - a.x) + (eb.y - a.y) * (eb.y - a.y)) <= a.radius + eb.radius)
					{
						hit = true;
						needRemoveBalls.add(a);
						//	needRemoveEnemy.add(eb);
						eb.y = 0;
						eb.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
                       eb.vy=Math.random()*10;
					   tscore += 5;
						continue;
					}

				}
				if (hit)
				{
					continue;
				}
				a.x += a.vx;
				a.y -= a.vy;
			}
			for (Ball b:needRemoveBalls)
			{
				myBulletPool.remove(b);
			}
			/*
			 for(Ball b:needRemoveEnemy)
			 {
			 enemyPool.remove(b);
			 }
			 */
		}
		private void myPlaneUpdate()
		{
            double fx = 0, fy = 0, dax, day, dx, dy, dist,  maxdist, dmax, mag1;
			Ball a=myPlane;
			// friction
			a.vx *= damping;
			a.vy *= damping;

			// gravity
			a.vx += gravity.x;
			a.vy += gravity.y;

			//bounds check
			if (a.x < a.radius) a.x = a.radius;
			if (a.y < a.radius) a.y = a.radius;
			if (a.x > getWidth() - a.radius) a.x = getWidth() - a.radius;
			if (a.y > getHeight() - a.radius)a.y = getHeight() - a.radius;


			dax = (a.x + a.vx);
			day = (a.y + a.vy);
			fx = fy = 0;

			for (Ball b : eBulletPool)
			{
				//issame = false;
				if (a != b && coarseCollision(a, b))
				{


					dx = dax - b.x;
					dy = day - b.y;

					dist = Math.sqrt(dx * dx + dy * dy);
					maxdist = a.radius + b.radius;

					dmax = (maxdist - dist);

					if (dmax > 0)
					{
						//if they are same then dispear themself
						if (startremove)
						{
							//issame = (a.color1 == b.color1 && a.color2 == b.color2);
							//		issame = (a.color1 == b.color1) ;//&& a.color2 == b.color2);


							needremove.add(b);
							//	needremove.add(a);
							//	if (vibrator != null)
							//		vibrator.vibrate(50);
							break;

						}
						mag1 = dmax * collisionDamping / maxdist;
						fx += dx * mag1;
						fy += dy * mag1;
					}

				}
			}

			a.vx += fx;
			a.vy += fy;

			a.x += a.vx;
			a.y += a.vy;


			if (startremove)
				dispear(needremove, eBulletPool);
        }
		private void dispear(ArrayList<Ball> balls, ArrayList<Ball> pool)
		{
			for (Ball b : balls)
			{
				score--;//increase score
				pool.remove(b);

				//	maxBalls++;
			}
			if (balls.size() > 0)
			{
				//new thread
				new Thread(new Runnable(){
						@Override
						public void run()
						{
							if (vibrator != null)
								vibrator.vibrate(100);
						}
					}).start();
			}
		}
		private boolean processFunArea(float x, float y)
		{

			boolean retflag=false;
			for (ClickBall b:functionBalls)
			{
				if (b.radius >	Math.sqrt((x - b.x) * (x - b.x) + (y - b.y) * (y - b.y)))
				{
					b.Click();
					retflag = true;
				}
			}
			return retflag;
		}
		private boolean ProcessMyPlane(float x, float y)
		{

			boolean retflag=false;

			for (ClickBall b:functionBalls)
			{
				if (b.isOn)
				{
					b.perform(x, y);
				  	retflag = true;
				}
			}

			return retflag;
		}

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
			int touchcount= event.getPointerCount();
			for (int i=0;i < touchcount;i++)
			{
				//处理功能区
				float x=event.getX(i);
				float y=event.getY(i);
				/*
				 if (processFunArea(x, y))
				 {
				 return true;
				 }
				 //处理模式
				 if (processMode2(x, y))
				 return true;
				 */
				myPlane.touchMove(x, y);
				Ball bu=new Ball(myPlane.x, myPlane.y, 10);
				bu.vx = myPlane.vx;
				bu.vy = 40;
				myBulletPool.add(bu);
			}

            return true;

        }

        public boolean coarseCollision(Ball a, Ball b)
        {
            return b.x - b.radius < a.x + a.radius && b.x + b.radius >= a.x - a.radius && b.y - b.radius < a.y + a.radius && b.y + b.radius >= a.y - a.radius;
        }

        private void rgbColor(int RGB)
        {
            paint.setARGB(0xFF, (RGB >> 16 & 0xFF), (RGB >> 8 & 0xFF), (RGB & 0xFF));
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1)
		{}

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            //record gravity
			gx = event.values[0];
			gy = event.values[1];
			gz = event.values[2];
			final float alpha = 0.2f;//0.5f;

			gravity.x = alpha * gravity.x + (1 - alpha) * -event.values[0];
			gravity.y = alpha * gravity.y + (1 - alpha) * event.values[1];

			float max = .25f;
			GeomUtils.clamp(gravity.x, -max, max);
			GeomUtils.clamp(gravity.y, -max, max);

        }

        private class Ball extends Circle
        {
			public	int color1;
			public	int color2;
            public double vx = 0, vy = 0;

            public boolean dragging = false;

            public Ball(double x, double y, double radius)
            {
                super(x, y, radius);
            }
			protected boolean isInBall(float x, float y)
			{
				return radius > Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
			}
        }
		private class MyPlane extends Ball
		{
			public MyPlane(double x, double y, double radius)
			{
				super(x, y, radius);
			}
			public void touchMove(float ex, float ey)
			{
				if (super.isInBall(ex, ey))
				{

					super.x = ex;
					super.y = ey;

				}
				else
				{
					vx += (ex - x) / 30;
					vy += (ey - y) / 30;
				}
			}
		}
		private abstract class ClickBall extends Ball implements IPerform
		{
			protected boolean isOn;
			protected ArrayList<Ball> _pool=null;
			public ClickBall(double x, double y, double radius)
			{
				super(x, y, radius);
				isOn = false;
			}
			public abstract void perform(float eventx, float eventy);
			public void setBallsPool(ArrayList<Ball> pool)
			{
				_pool = pool;
			}
			public void Click()
			{
				isOn = !isOn;
				int tc=color1;
				color1 = color2;
				color2 = tc;
			}
		}
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
			}
		}

    }
	interface IPerform
	{
		void perform(float eventx, float eventy);
	}
}
