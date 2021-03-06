package com.tyler.app.crabgarden;

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
	Bitmap myplaneBMPOri;

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
		//prepare bmps
		e20bmp = processBMP(R.drawable.crab20); 
		e30bmp = processBMP(R.drawable.crab30);
		e40bmp = processBMP(R.drawable.crab40);
		p56bmp = processBMP(R.drawable.p56);

		myplaneBMPOri = p56bmp;


        setContentView(R.layout.activity_android_ball_physics);

		physics = new BallPhysics(this);
        LayoutParams full = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        physics.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
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
		private ArrayList<Ball> traceBallPool;
		private ArrayList<Ball> bonousPool;
		private ArrayList<Ball> killersPool;
		private Hashtable<Integer,Ball> targets;
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
			targets = new Hashtable<Integer,Ball>();
			bonousPool = new ArrayList<Ball>();
			killersPool = new ArrayList<Ball>();
			//    initalFuncBalls();
			//	InitialMyPlane();
			initialBonus();
			initialEnemy();

            timer = new Timer(20, this);
            timer.start();

        }
		private void InitialMyPlane()
		{
			myPlane = new MyPlane(UtilityHelper.SCREEN_WIDTH / 2
								  , UtilityHelper.SCREEN_HEIGHT / 2
								  , 60);
			myPlane.color1 = 0;
			myPlane.color2 = 1;
			myPlane.vx = 20;
			myPlane.vy = 20;
			//pool.add(myPlane);
		}
		private void initialEnemy()
		{
			for (int i=0;i < 10;i++)
			{
				Ball eb=new Ball(Math.random() * UtilityHelper.SCREEN_WIDTH
								 , Math.random() * UtilityHelper.SCREEN_HEIGHT, 15 + Math.random() * 20);
				eb.color1 = 6;
				eb.color2 = 4;
				//	eb.vy = 10+ Math.random() * 30;
				//	eb.vx = 10+ Math.random() * 30;
				Ball a=eb;

				float rad=(float)Math.random() * 30;
				if (rad <= 10)
				{
					eb.radius = e20bmp.getWidth() / 2;
					eb.skin = e20bmp;
				}
				else if (rad > 10 && rad <= 20)
				{
					eb.radius = e30bmp.getWidth() / 2;
					eb.skin = e30bmp;
				}
				else 
				{
					eb.radius = e40bmp.getWidth() / 2;
					eb.skin = e40bmp;
				}
				rearrange(a);
				enemyPool.add(eb);
			}
		}
		private void initialBonus()
		{
			for (int i=0;i < 5;i++)
			{
				Ball b=new Ball(Math.random() * (UtilityHelper.SCREEN_WIDTH - 200) + 100
								, Math.random() * (UtilityHelper.SCREEN_HEIGHT - 200) + 100
								, Math.random() * 30 + 20);
			    b.color1 = 2;
				b.color2 = 3;
				//bmp skin
				Bitmap bmpSkin=scaleBMP(processBMP(R.drawable.unkown), 0.35f);
				switch (i)
				{
					case(0):
						bmpSkin = scaleBMP(processBMP(R.drawable.bonusbox), 0.25f);
						break;
					case(1):
						bmpSkin = scaleBMP(processBMP(R.drawable.bonus2), 0.25f);
						break;
					case(2):
						bmpSkin = scaleBMP(processBMP(R.drawable.bomb), 0.25f);
						break;
					case(3):
						bmpSkin = scaleBMP(processBMP(R.drawable.killer), 0.25f);
						break;

				}
				b.radius = bmpSkin.getWidth() / 2;
				b.skin = bmpSkin;
				bonousPool.add(b);
			}
		}
		private Bitmap scaleBMP(Bitmap bmp, float scale)
		{

			Matrix matrix = new Matrix();  
			matrix.postScale(scale, scale);  
			int chgWidth=(int)(bmp.getWidth());//*scale);

			Bitmap chgBmp = Bitmap.createBitmap(bmp, 0, 0, chgWidth, chgWidth, matrix, true);   
			return chgBmp;
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
		private void DrawTrace(Ball enemy, Canvas canvas)
		{
			/*
			 for (Ball tr:enemy.traceList)
			 {

			 paint.setColor(Color.RED);
			 //	paint.setAlpha(100);
			 tr.draw(canvas, paint);
			 //	paint.setAlpha(255);
			 }
			 */
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(10);
			paint.setColor(Color.BLUE);
			paint.setAlpha(100);
			for (int i=0;i < enemy.traceList.size() - 1;i++)
			{
				Ball b2=enemy.traceList.get(i + 1);
				Ball b1=enemy.traceList.get(i);

				canvas.drawLine(b1.x, b1.y, b2.x, b2.y, paint);
			}
			paint.setAlpha(255);
			paint.setStrokeWidth(5);
		}
		private void DrawEnemy(Ball myPlane, Canvas canvas)
		{
			Ball b=myPlane;
			//	DrawTrace(myPlane,canvas);
		//	int alpha=(int)Math.random() * 55 + 200;
		//	paint.setAlpha(alpha);
			b.DrawSkin(canvas, paint);

			//		rgbColor(colors[myPlane.color1]);
			//		myPlane.draw(canvas, paint);
			//	rgbColor(colors[myPlane.color2]);
			//canvas.drawCircle(myPlane.x, myPlane.y, (float)(myPlane.radius * Constants.oneByGoldenRatio), paint);
		}
		private void DrawBonus(Canvas canvas)
		{
			for (Ball b:bonousPool)
			{
				b.DrawSkin(canvas, paint);
				//	paint.setStyle(Paint.Style.STROKE);
				//	DrawBall(b,canvas);
				//DrawBall(ball,canvas);
				/*
				 rgbColor(colors[b.color1]);
				 b.draw(canvas, paint);
				 rgbColor(colors[b.color2]);
				 canvas.drawCircle(b.x, b.y, (float)(b.radius * Constants.oneByGoldenRatio), paint);
				 */
			}
		}
		private void DrawMyplane(Ball myPlane, Canvas canvas)
		{
			if (myPlane == null)
				return;
			paint.setStyle(Paint.Style.STROKE);
			for (Ball tr:myPlane.traceList)
			{
				tr.draw(canvas, paint);
			}
			//DrawMyplane(myPlane, canvas);
			canvas.drawBitmap(p56bmp, myPlane.x - p56bmp.getWidth() / 2, myPlane.y - p56bmp.getHeight() / 2, paint);
			rgbColor(colors[2]);
			paint.setStyle(Paint.Style.FILL);
			if (myPlane.vx > 0)
			{
				// canvas.drawRect(myPlane.x-p56bmp.getWidth()/2-15,myPlane.y,myPlane.x-p56bmp.getWidth()/2,myPlane.y+15,paint);
				canvas.drawCircle(myPlane.x - p56bmp.getWidth() / 2 * 3 / 4, myPlane.y + p56bmp.getHeight() / 2, (float)(0.05 * myPlane.vx) + 5, paint);
			}
			else
			{
				//	canvas.drawRect(myPlane.x+p56bmp.getWidth()/2,myPlane.y,myPlane.x+p56bmp.getWidth()/2+15,myPlane.y+15,paint);
				canvas.drawCircle(myPlane.x + p56bmp.getWidth() / 2 * 3 / 4, myPlane.y + p56bmp.getHeight() / 2, (float)(-0.05 * myPlane.vx) + 5, paint);
			}
			if (myPlane.vy < 0)
			{

				canvas.drawCircle(myPlane.x, myPlane.y + p56bmp.getHeight() / 2, (float)(-0.1 * myPlane.vy + 5), paint);
			}
			paint.setStyle(Paint.Style.STROKE);
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
			DrawBonus(canvas);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			//	paint.setStyle(Paint.Style.FILL);	
			for (Ball b:enemyPool)
			{
				DrawTrace(b, canvas);
			}

			for (Ball b:enemyPool)
			{
				//DrawBall(b, canvas);

				DrawEnemy(b, canvas);
			}
			paint.setAlpha(255);
			for (Ball eb:eBulletPool)
			{
				DrawBall(eb, canvas);
			}
			for (Ball bu:myBulletPool)
			{
				DrawBall(bu, canvas);
			}

			DrawMyplane(myPlane, canvas);
			String gmsg="Life={1}, Score={2}, E={3}, EB={4}, MB={5},VX={6},VY={7}";
			gmsg = gmsg.replace("{1}", "" + life)
				.replace("{2}", "" + score)
				.replace("{3}", "" + enemyPool.size())
				.replace("{4}", "" + eBulletPool.size())
				.replace("{5}", "" + myBulletPool.size())
				.replace("{6}", "" + 0) //enemyPool.get(0).vx)
				.replace("{7}", "" + 0);
			paint.setColor(Color.WHITE);
			paint.setTextSize(30); 
			canvas.drawText(gmsg, 5, 25, paint);	


		}



        private void update()
        {
			//	boolean issame;
			//	if (startremove)
			//    	needremove.clear();
			bonusUpdate();
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
				if (a.y > getHeight() || a.x > getWidth() || a.x < 0 || a.y < 0)
				{
					rearrange(a);
					/*
					 a.traceList.clear();
					 int scenario = (int)(Math.random()*12);
					 if(scenario<=3)
					 {
					 //从左边来
					 a.x=0;
					 a.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
					 a.vx=Math.random()*10+10;
					 a.vy=0;
					 a.RotateSkin(90);
					 //	restartships(0, (float)Math.random() * UtilityHelper.SCREEN_HEIGHT

					 }else if(scenario<=6)
					 {
					 //从上面来
					 a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
					 a.y=0;
					 a.vy=Math.random()*10+10;
					 a.vx=0;
					 a.RotateSkin(180);
					 }else if(scenario<=9){
					 //从下面来
					 a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
					 a.y=UtilityHelper.SCREEN_HEIGHT;
					 a.vy=(-1)*(Math.random()*10+10);
					 a.vx=0;
					 a.RotateSkin(0);
					 }else{
					 //从右面来
					 a.x =UtilityHelper.SCREEN_WIDTH;
					 a.y=(float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
					 a.vx=(-1)*(Math.random()*10+10);
					 a.vy=0;
					 a.RotateSkin(270);
					 }
					 */
					//	a.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
					//	a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;

					continue;
				}	
				/*
				 //撞毁
				 if (coarseCollision(a, myPlane))
				 {
				 //	clear.add(a);
				 crash = true;
				 a.y = 0;
				 a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
				 a.vy = Math.random() * 10;
				 life -= 5;
				 continue;
				 }
				 */
				if (a.traceList.size() > 0)
					a.moveToNext();
				else
				{
					a.x += a.vx;
					a.y += a.vy;
				}
				/*
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
				 */

			}
			//	if (clear.size() > 0)
		    if (crash)
			{
				//	clear(enemyPool, clear);
				//震动
				vibrate();
			}
		}
		private void rearrange(Ball a)
		{
			a.traceList.clear();
			int scenario = (int)(Math.random() * 12);
			if (scenario <= 3)
			{
				//从左边来
				a.x = 0;
				a.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
				a.vx = Math.random() * 6 + 5;
				a.vy = 0;
				a.RotateSkin(90);
				//	restartships(0, (float)Math.random() * UtilityHelper.SCREEN_HEIGHT

			}
			else if (scenario <= 6)
			{
				//从上面来
				a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
				a.y = 0;
				a.vy = Math.random() * 6 + 5;
				a.vx = 0;
				a.RotateSkin(180);
			}
			else if (scenario <= 9)
			{
				//从下面来
				a.x = (float)Math.random() * UtilityHelper.SCREEN_WIDTH;
				a.y = UtilityHelper.SCREEN_HEIGHT;
				a.vy = (-1) * (Math.random() * 6 + 5);
				a.vx = 0;
				a.RotateSkin(0);
			}
			else
			{
				//从右面来
				a.x = UtilityHelper.SCREEN_WIDTH;
				a.y = (float)Math.random() * UtilityHelper.SCREEN_HEIGHT;
				a.vx = (-1) * (Math.random() * 6 + 5);
				a.vy = 0;
				a.RotateSkin(270);
			}
		}
		private void bonusUpdate()
		{
			for (Ball b:bonousPool)
			{
				b.change();
				for (Ball ship:enemyPool)
				{
					if (coarseCollision(b, ship))
					{
						changeRadPlace(b);
						score += 10;
					}
				}
			}
		}
		private void changeRadPlace(Ball bonus)
		{
			bonus.x = (float)(Math.random() * (UtilityHelper.SCREEN_WIDTH - 200) + 100);
			bonus.y = (float)(Math.random() * (UtilityHelper.SCREEN_HEIGHT - 200) + 100);
			//	bonus.radius = 1;

		}
		private void restartships(Ball b, float left, float right, float vx, float vy)
		{
			b.x = left;
			b.y = right;
			b.vx = vx;
			b.vy = vy;
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
						eb.vy = Math.random() * 10;
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
			if (myPlane == null)
				return;
			//move according to the trace
			myPlane.moveToNext();
			/*
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
			if (touchcount > 2)
				autoshot = !autoshot;

			for (int i=0;i < touchcount;i++)
			{
				float x=event.getX(i);
				float y=event.getY(i);

				int action=event.getAction();
				switch (action)
				{
					case(MotionEvent.ACTION_DOWN):
						for (Ball b:enemyPool)
						{
							if (b.isInBall(x, y))
							{
								b.traceList.clear();
								if (targets.containsKey(i))
								{
									targets.remove(i);
								}
								targets.put(i, b);

								break;
							}
						}
						break;
					case(MotionEvent.ACTION_MOVE):
						Ball tar=targets.get(i);
						if (tar != null)
							tar.AddNewTrace(x, y);
						break;
					case(MotionEvent.ACTION_UP):
						if (targets.containsKey(i))
							targets.remove(i);
						break;
				}

				/*
				 if (processFunArea(x, y))
				 {
				 return true;
				 }
				 //处理模式
				 if (processMode2(x, y))
				 return true;
				 */
				//find which is in the target ship
				/*if (myPlane.isInBall(x, y))
				 {
				 targets.put(i, myPlane);
				 }*/
				//show the touch trace
				/*
				 Ball tar=myPlane;// targets.get(i);
				 if (tar != null)
				 {
				 ArrayList traceList=tar.traceList;

				 {
				 Ball b=new Ball(x, y, 5);
				 b.color1 = 0;
				 b.color2 = 1;
				 tar.traceList.add(b);
				 }
				 }
				 */
				/*
				 myPlane.touchMove(x, y);
				 if(!autoshot)
				 {
				 Ball bu=new Ball(myPlane.x, myPlane.y, 10);
				 bu.vx = myPlane.vx;
				 bu.vy = 40;
				 myBulletPool.add(bu);
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

            public Ball(double x, double y, double radius)
            {
                super(x, y, radius);
				traceList = new ArrayList<Ball>();
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
