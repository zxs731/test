package com.tyler.app.android_ball_physics;

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



public class Android_ball_physicsActivity extends Activity
{

	BallPhysics physics;
	Android_ball_physicsActivity mainActivity;
	Date starttime;
	int score;
	String statusmsg="";
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


        setContentView(R.layout.activity_android_ball_physics);

		physics = new BallPhysics(this);
        LayoutParams full = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        addContentView(physics, full);
        mainActivity = this;

        //new thread
        new Thread(new Runnable(){

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					AppHelper.enableSensor(mainActivity, physics, SensorType.ACCELEROMETER, SensorRate.GAME);
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

    public class BallPhysics extends View implements ITicker, SensorEventListener
    {
        private ArrayList<Ball> pool;
        private int maxBalls = 40;
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

        public BallPhysics(Context context)
        {
            super(context);
            init(context);
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
			statusmsg=getResources().getString(
				R.string.timekeeping);
            pool = new ArrayList<Ball>();
		    needremove = new ArrayList<Ball>();	

            Timer timer = new Timer(45, this);
            timer.start();

        }

        @Override
        public void tick()
        {

            update();
            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas)
        {

			//  int id = colorId;
        	
            for (Ball b : pool)
            {

				rgbColor(colors[b.color1]);
			/*	RadialGradient  mRadialGradient = new RadialGradient(b.x, b.y, b.radius+2, new int[] {  
						paint.getColor(),paint.getColor(),paint.getColor(),paint.getColor(),paint.getColor(),paint.getColor() , paint.getColor() ,paint.getColor() ,Color.BLACK}, null,  
		                    Shader.TileMode.MIRROR);  
				paint.setShader(mRadialGradient);
				*/
                b.draw(canvas, paint);
                paint.setShader(null);

                //change to circle color
				rgbColor(colors[b.color2]);
				
				float bcx=b.x;
				float bcy=b.y;
				//draw circle
                canvas.drawCircle(b.x, b.y, (float)(b.radius * Constants.oneByGoldenRatio), paint);
	/*			
				//draw Oval begin
				float c=(float)(b.radius * (Constants.oneByGoldenRatio/2+0.5));
				
				LinearGradient mLinearGradient = new LinearGradient(b.x, b.y-c,b.x ,b.y+(b.radius-c), new int[] {  
	            		Color.WHITE,paint.getColor()}, null,  
	                    Shader.TileMode.MIRROR);  
				paint.setShader(mLinearGradient);
			//	biga.shapes2D. Rectangle rt=new biga.shapes2D. Rectangle ( );
				
                canvas.drawOval(new RectF(b.x-c, b.y-c, b.x+c, b.y+(b.radius-c)), paint);
				*/
               //canvas.drawRect(new RectF(b.x-c, b.y-c, b.x+c, b.y+(b.radius-c)), paint);
                paint.setShader(null);
                //draw circle end
				paint.setColor(Color.BLACK);
				//canvas.drawText(b.color1+","+b.color2, b.x, b.y, paint);
            }
            if (colorId>0)
            {
            //draw score
        	java.util.Date now = new java.util.Date(System.currentTimeMillis());
        	float timeUsing = ((float) (now.getTime() - starttime
					.getTime()) / 1000);
        	timeUsing = (float) (Math.round(timeUsing * 10)) / 10;
			String msg = statusmsg. replace("{0}",
					timeUsing + "")
					.replace("{1}", score+"")
					.replace("{2}", startremove+"")
					.replace("{3}",pool.size()+"")
					.replace("{4}",maxBalls+"");
			//paint.setTextAlign(Align.CENTER);
			paint.setTextSize(18); 
			paint.setColor(Color.WHITE);
			canvas.drawText(msg, 35, 15, paint);
			//彩球顺序
			for(int ii=0;ii<20;ii++)
			{
				
			int x=ii*20+40;
			Ball nb=new Ball(x,45,10);
				rgbColor(colors[(colorId+1+ii)%colors.length]);
                nb.draw(canvas, paint);
				rgbColor(colors[(colorId+2+ii)%colors.length]);
				canvas.drawCircle(nb.x, nb.y, (float)(nb.radius * Constants.oneByGoldenRatio), paint);
		
            }
			}
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
			biga.Point p0=new biga.Point();
			p0.x=200;
			p0.y=350;
			biga.Point py=new biga.Point();
			biga.Point px=new biga.Point();
			biga.Point pz=new biga.Point();
			py.x=p0.x;
			py.y=p0.y+gy*10*(-1);
			px.x=p0.x+gx*10;
			px.y=p0.y;
			float part=(float)(gz*10/Math.sqrt(2));
			pz.x=p0.x+part*(-1);
			pz.y=p0.y+part;
			paint.setStrokeWidth(5);
			canvas.drawLine(p0.x,p0.y,py.x,py.y,paint);
			canvas.drawLine(p0.x,p0.y,px.x,px.y,paint);
			canvas.drawLine(p0.x,p0.y,pz.x,pz.y,paint);
			paint.setStrokeWidth(0);
			canvas.drawText("y: "+gy+" (m/s2)", py.x+20, py.y, paint);
			canvas.drawText("x: "+gx+" (m/s2)", px.x, px.y-20, paint);
			canvas.drawText("z: "+gz+" (m/s2)", pz.x+20, pz.y+20, paint);
		
		}
			
      

        private void update()
        {
			boolean issame;
			if (startremove)
		     	needremove.clear();
            double fx = 0, fy = 0, dax, day, dx, dy, dist,  maxdist, dmax, mag1;
			// int total= pool.size() ;
            for (Ball a : pool)
            {

                if (a.dragging) continue;
				issame = false;

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

                for (Ball b : pool)
                {
					issame = false;
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
								issame = (a.color1 == b.color1) ;//&& a.color2 == b.color2);
								
							    if (issame)
								{
									needremove.add(b);
									needremove.add(a);
									break;
								}
							}
                            mag1 = dmax * collisionDamping / maxdist;
                            fx += dx * mag1;
                            fy += dy * mag1;
                        }

                    }
                }
				if (issame)
				{
					continue;
				}
                a.vx += fx;
                a.vy += fy;

                a.x += a.vx;
                a.y += a.vy;

            }
			if (startremove)
				dispear(needremove, pool);
        }
		private void dispear(ArrayList<Ball> balls, ArrayList<Ball> pool)
		{
			for (Ball b : balls)
			{
				score++;//increase score
				pool.remove(b);
				maxBalls++;
			}
		
		}
        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
			int touchcount= event.getPointerCount();
			for(int i=0;i<touchcount;i++)
			{
			startremove = false;
            if (pool.size() > maxBalls)
            {
                pool.remove(0);
                score--;//score -1
                colorId++;
				startremove = true;
				maxBalls=40;
            }
      
			Ball ball=	 new Ball(event.getX(i), event.getY(i), 25 + Math.random() * 100);
			if (colorId==0) {
				starttime=new java.util.Date(System.currentTimeMillis());
			}
			int id=colorId;
			int colorid1= id % colors.length;//(int) (Math.random() * colors.length); 
			id++;
		//	id++;
			int colorid2= id % colors.length;//(int) (Math.random() * colors.length); 
			colorId++;
			/*
			 while(colorid1==colorid2){
			 colorid2=(int) (Math.random() * colors.length); 
			 }
			 */
			ball.color1 = colorid1;
			ball.color2 = colorid2;

            pool.add(ball);
			//	if(pool.size()==maxBalls+1)
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
			gx=event.values[0];
			gy=event.values[1];
			gz=event.values[2];
			final float alpha = 0.5f;

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
        }
    }
}
