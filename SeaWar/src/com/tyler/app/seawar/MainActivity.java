package com.tyler.app.seawar;

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
	Bitmap e1240bmp;
	Bitmap e1340bmp;
	Bitmap e1440bmp;
	Bitmap p156bmp;
	private ArrayList<Bitmap> leftBMPList;
	private ArrayList<Bitmap> rightBMPList;

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
        AppHelper.orientation(this, Orientation.LANDSCAPE);
		DisplayMetrics dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		UtilityHelper.SCREEN_HEIGHT = dm.heightPixels; // 获取具体的屏幕分辨率数??
		UtilityHelper.SCREEN_WIDTH = dm.widthPixels;


        setContentView(R.layout.activity_android_ball_physics);
//bmps
		PrepareBMPResources();
		physics = new BallPhysics(this);
        LayoutParams full = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//	physics.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        addContentView(physics, full);
        mainActivity = this;




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
	private void PrepareBMPResources()
	{
		leftBMPList = new ArrayList<Bitmap>();
		rightBMPList = new ArrayList<Bitmap>();
		leftBMPList.add(processBMP(R.drawable.f1240));
		leftBMPList.add(processBMP(R.drawable.f1340));
		leftBMPList.add(processBMP(R.drawable.f1440));
		//leftBMPList.add(processBMP(R.drawable.f540));
		//leftBMPList.add(processBMP(R.drawable.f640));
		leftBMPList.add(processBMP(R.drawable.f740));
		leftBMPList.add(processBMP(R.drawable.f840));
		leftBMPList.add(processBMP(R.drawable.f940));
		leftBMPList.add(processBMP(R.drawable.f950));
		leftBMPList.add(processBMP(R.drawable.f960));
		leftBMPList.add(processBMP(R.drawable.f970));

		rightBMPList.add(processBMP(R.drawable.f240));
		rightBMPList.add(processBMP(R.drawable.f340));
		rightBMPList.add(processBMP(R.drawable.f440));
		//rightBMPList.add( processBMP(R.drawable.f1540));
		//rightBMPList.add( processBMP(R.drawable.f1640));
		rightBMPList.add(processBMP(R.drawable.f1740));
		rightBMPList.add(processBMP(R.drawable.f1840));
		rightBMPList.add(processBMP(R.drawable.f1940));
		rightBMPList.add(processBMP(R.drawable.f1950));
		rightBMPList.add(processBMP(R.drawable.f1960));
		rightBMPList.add(processBMP(R.drawable.f1970));
		/*
		 e20bmp = processBMP(R.drawable.f240);
		 e30bmp = processBMP(R.drawable.f340);
		 e40bmp = processBMP(R.drawable.f440);
		 e1240bmp = processBMP(R.drawable.f1240);
		 e1340bmp = processBMP(R.drawable.f1340);
		 e1440bmp = processBMP(R.drawable.f1440);
		 */
		p56bmp = processBMP(R.drawable.f140);
		p156bmp = processBMP(R.drawable.f1140);

	}
	private Bitmap processBMP(int resId)
	{
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);

		bmp = bmp.copy
		(Config.ARGB_8888, true);

		for (int i=0;i < bmp.getWidth();i++)
		{
			for (int j=0;j < bmp.getHeight();j++)
			{
				if (bmp.getPixel(i, j) == Color.BLACK)
			    	bmp.setPixel(i, j, Color.alpha(0));
			}
		}

		bmp.setHasAlpha(true);
		return bmp;
	}
	private Bitmap rotateBitmap(Bitmap bmp, float angle)
	{
		Matrix matrix=new Matrix();
		matrix.setRotate(angle);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
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
		private ArrayList<Ball> bubblePool;
		private ArrayList<Ball> relationShip;

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
		boolean autoshot=false;



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
			bubblePool = new ArrayList<Ball>();
			relationShip = new ArrayList<Ball>();
			//    initalFuncBalls();
			InitialMyPlane();
			initialEnemy();

            timer = new Timer(30, this);
            timer.start();

        }
		private void InitialMyPlane()
		{
			myPlane = new MyPlane(UtilityHelper.SCREEN_WIDTH / 2
								  , UtilityHelper.SCREEN_HEIGHT / 2
								  , 20);
			myPlane.color1 = 0;
			myPlane.color2 = 1;
			myPlane.skin = p56bmp;
			myPlane.radius = myPlane.skin.getWidth() / 2;
			//pool.add(myPlane);
		}


		private void initialEnemy()
		{
			for (int i=0;i < 10;i++)
			{
				//Ball eb=new Ball(Math.random() * UtilityHelper.SCREEN_WIDTH
				//					 , 0, 15 + Math.random() * 20);
				Ball eb=null;
				/*
				 if (Math.random() * 10 > 5)
				 {
				 eb = new Ball(0, Math.random() * UtilityHelper.SCREEN_HEIGHT
				 , 15 + Math.random() * 20);
				 eb.vx = Math.random() * 10;

				 }
				 else
				 {
				 eb = new Ball(UtilityHelper.SCREEN_WIDTH, Math.random() * UtilityHelper.SCREEN_HEIGHT
				 , 15 + Math.random() * 20);
				 eb.vx = (-1) * (Math.random() * 10);
				 }
				 */
				eb = new Ball(10, 10, 20);
				ResetEnemy(eb);
				eb.color1 = 6;
				eb.color2 = 4;
				eb.vy = 0;//Math.random() * 10;

				enemyPool.add(eb);
			}
		}

		private void ResetEnemy(Ball eb)
		{
			if (Math.random() * 10 > 5)
			{

				eb.x = 0;
				eb.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
				eb.skin = getRandom(false);
				eb.vx = Math.random() * 10;

			}
			else
			{

				eb.x = UtilityHelper.SCREEN_WIDTH;
				eb.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
				eb.skin = getRandom(true);
				eb.vx = (-1) * (Math.random() * 10);
			}
			eb.radius = (float)eb.skin.getWidth() / 2;
			eb.color1 = 6;
			eb.color2 = 4;
			eb.vy = 0;//Math.random() * 10;
		}
		private Bitmap getRandom(boolean direct)
		{
			Bitmap ret;
			int size=rightBMPList.size();
			int i=(int)(Math.random() * size);
			if (direct)
				ret = rightBMPList.get(i);
			else
				ret = leftBMPList.get(i);
			return changeSizeRadom(ret);
		}
		private Bitmap changeSizeRadom(Bitmap skin)
		{
			int skinWidth=skin.getWidth();
			float scale = (float)(Math.random() * 1.5 + 0.5);

			Matrix matrix = new Matrix();  
			matrix.postScale(scale, scale);  
			return Bitmap.createBitmap(skin, 0, 0, skinWidth , skinWidth, matrix, true);   
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
		private void DrawEnemy(Ball e, Canvas canvas)
		{
			/*
			 Ball b=myPlane;
			 if (b.vx < 0)
			 {

			 if (b.radius >= 15 && b.radius <= 21)
			 canvas.drawBitmap(e20bmp, b.x - e20bmp.getWidth() / 2, b.y - e20bmp.getHeight() / 2, paint);
			 else if (b.radius > 21 && b.radius <= 29)
			 canvas.drawBitmap(e30bmp, b.x - e30bmp.getWidth() / 2, b.y - e30bmp.getHeight() / 2, paint);
			 else 
			 canvas.drawBitmap(e40bmp, b.x - e40bmp.getWidth() / 2, b.y - e40bmp.getHeight() / 2, paint);

			 }
			 else
			 {

			 if (b.radius >= 15 && b.radius <= 21)
			 canvas.drawBitmap(e1240bmp, b.x - e1240bmp.getWidth() / 2, b.y - e1240bmp.getHeight() / 2, paint);
			 else if (b.radius > 21 && b.radius <= 29)
			 canvas.drawBitmap(e1340bmp, b.x - e1340bmp.getWidth() / 2, b.y - e1340bmp.getHeight() / 2, paint);
			 else 
			 canvas.drawBitmap(e1440bmp, b.x - e1440bmp.getWidth() / 2, b.y - e1440bmp.getHeight() / 2, paint);
			 }
			 */

			Bitmap bmp=e.skin;
			if (bmp != null)
				canvas.drawBitmap(bmp, e.x - bmp.getWidth() / 2, e.y - bmp.getHeight() / 2, paint);
			//		rgbColor(colors[myPlane.color1]);
			//		myPlane.draw(canvas, paint);
			//	rgbColor(colors[myPlane.color2]);
			//canvas.drawCircle(myPlane.x, myPlane.y, (float)(myPlane.radius * Constants.oneByGoldenRatio), paint);
		}
		private void DrawMyplane(Ball myPlane, Canvas canvas)
		{
			paint.setStyle(Paint.Style.STROKE);
			//DrawMyplane(myPlane, canvas);
			if (myPlane.vx > 0)
				canvas.drawBitmap(p56bmp, myPlane.x - p56bmp.getWidth() / 2, myPlane.y - p56bmp.getHeight() / 2, paint);
			else
				canvas.drawBitmap(p156bmp, myPlane.x - p156bmp.getWidth() / 2, myPlane.y - p156bmp.getHeight() / 2, paint);

		}
		private void DrawBubbles(Canvas canvas)
		{
			paint.setStrokeWidth(2);
			rgbColor(colors[6]);

			for (Ball b:bubblePool)
			{
				paint.setAlpha(b.alpha);	
				b.draw(canvas, paint);
			}
			paint.setAlpha(255);
		}
		private void drawRelationShip(Canvas canvas)
		{
			for (Ball r:relationShip)
			{
				if (r.radius >= myPlane.radius)
				{
					paint.setColor(Color.RED);

				}
				else
				{
					paint.setColor(Color.GREEN);
				}
				canvas.drawLine(myPlane.x, myPlane.y, r.x, r.y, paint);
			}
		}
        @Override
        protected void onDraw(Canvas canvas)
        {
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			for (Ball b:enemyPool)
			{
				//DrawBall(b, canvas);

				DrawEnemy(b, canvas);
			}
			DrawBubbles(canvas);
			drawRelationShip(canvas);
			paint.setStyle(Paint.Style.FILL);
			for (Ball eb:eBulletPool)
			{
				DrawBall(eb, canvas);
			}
			for (Ball bu:myBulletPool)
			{
				DrawBall(bu, canvas);
			}

			DrawMyplane(myPlane, canvas);
			String gmsg="Life={1}, Score={2}, E={3}, EB={4}, MB={5},vx={6},vy={7}";
			gmsg = gmsg.replace("{1}", "" + life)
				.replace("{2}", "" + score)
				.replace("{3}", "" + enemyPool.size())
				.replace("{4}", "" + eBulletPool.size())
				.replace("{5}", "" + myBulletPool.size())
				.replace("{6}", "" + (int)myPlane.vx)
				.replace("{7}", "" + (int)myPlane.vy);
			paint.setColor(Color.WHITE);
			paint.setTextSize(30); 
			canvas.drawText(gmsg, 5, 25, paint);	
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
			bubbleUpdate();
		}
		private void bubbleUpdate()
		{
			ArrayList<Ball > needRem=new ArrayList<Ball>();
			for (Ball b:bubblePool)
			{
				if (b.x < 0 || b.x > UtilityHelper.SCREEN_WIDTH
					|| b.y < 0 || b.y > UtilityHelper.SCREEN_HEIGHT)
				{
					needRem.add(b);
				}
				else
				{


					b.x += b.vx;
					b.y += b.vy;
				}
			}
			for (Ball b:needRem)
			{
				bubblePool.remove(b);
			}
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
			//	if (isHit)
			//		vibrate();
		}
		private void enemyUpdate()
		{
			ArrayList<Ball> clear=new ArrayList<Ball>();
			boolean crash=false;

			relationShip.clear();
			for (Ball a:enemyPool)
			{
				//bounds check
				if (a.x > getWidth() || a.x < 0)
				{
					/*
					 a.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
					 if (Math.random() * 10 > 5)
					 {
					 a.x = 0;
					 a.vx = Math.random() * 10;
					 }
					 else
					 {
					 a.x = UtilityHelper.SCREEN_WIDTH;
					 a.vx = (-1) * Math.random() * 10;
					 }
					 a.vy = 0;
					 */
					ResetEnemy(a);
					continue;
				}	
				//撞毁
				if (coarseCollision(a, myPlane))
				{
					if (myPlane.radius > a.radius)
					{


						//	clear.add(a);
						crash = true;
						/*
						 a.y =  (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
						 if (Math.random() * 10 > 5)
						 {
						 a.x = 0;
						 a.vx = Math.random() * 10;
						 }
						 else
						 {
						 a.x = UtilityHelper.SCREEN_WIDTH;
						 a.vx = (-1) * Math.random() * 10;
						 }
						 a.vy = 0;
						 */
						ResetEnemy(a);
						score += 10;
						life+=1;
						continue;
					}
					else
					{


						life -= 1;
					}
					
				}
				//逼近
				if (isNear(a, myPlane))
				{
					relationShip.add(a);
				}
				//敌人移动
				a.x += a.vx;
				a.y += a.vy;
				//发射
				/*
				 if (Math.random() * 100 > 97)
				 {
				 Ball ebu=new Ball(a.x, a.y, 5);
				 ebu.color1 = 6;
				 ebu.color2 = 5;
				 if (myPlane.y < a.y)
				 ebu.vy = (-1) * (a.vy + 20);
				 else
				 ebu.vy = a.vy + 20;
				 ebu.vx =	(myPlane.x - ebu.x) / ((myPlane.y - ebu.y) / ebu.vy);
				 eBulletPool.add(ebu);
				 }
				 */
				//raise bubbles
				if (Math.random() * 100 > 98)
				{
					Ball bb=new Ball(a.x, a.y, Math.random() * 6 + 2);
					bb.color1 = 6;
					bb.color2 = 5;
					bb.vx = 0;
					bb.vy = (-1) * (Math.random() * 8 + 3);
					bb.alpha = (int)(Math.random() * 150 + 50);
					bubblePool.add(bb);
				}

			}
			//	if (clear.size() > 0)
		    if (crash)
			{
				//	clear(enemyPool, clear);
				//震动
				//		vibrate();
			}
		}
		private boolean isNear(Ball e, Ball myFish)
		{
			int dis=	e.skin.getWidth() + myFish.skin.getWidth();
			return Math.sqrt((e.x - myFish.x) * (e.x - myFish.x) + (e.y - myFish.y) * (e.y - myFish.y)) <= dis;

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
						eb.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
						if (Math.random() * 10 > 5)
						{
							eb.x = 0;
							eb.vx = Math.random() * 10;
						}
						else
						{
							eb.x = UtilityHelper.SCREEN_WIDTH;
							eb.vx = (-1) * Math.random() * 10;
						}

						eb.vy = 0;// Math.random() * 10;
						tscore += 5;
						continue;
					}

				}
				if (hit)
				{
					continue;
				}
				a.x += a.vx;
				a.y += a.vy;
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
			a.vx += gravity.y;
			a.vy += (-1) * gravity.x;

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

			a.x += a.vx / 2;
			a.y += a.vy / 2;


			//	if (startremove)
			//		dispear(needremove, eBulletPool);
			/*
			 if (autoshot)
			 {
			 if (myPlane.vx != 0 || myPlane.vy != 0)
			 {
			 Ball bu=new Ball(myPlane.x, myPlane.y, 8);
			 bu.vx = myPlane.vx;
			 bu.vy = 40;
			 myBulletPool.add(bu);
			 }
			 }
			 */
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


        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
			int touchcount= event.getPointerCount();
			if (touchcount > 2)
				autoshot = !autoshot;

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
				/*
				 if (!autoshot)
				 {
				 if (!myPlane.isInBall(x, y))
				 {
				 Ball bu=new Ball(myPlane.x, myPlane.y, 5);

				 bu.vy=myPlane.vy;
				 if (x > myPlane.x)
				 bu.vx = 40;
				 else if (x < myPlane.x)
				 bu.vx = -40;
				 myBulletPool.add(bu);
				 }
				 }
				 */
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
			public int alpha=255;

			public Bitmap skin;
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
