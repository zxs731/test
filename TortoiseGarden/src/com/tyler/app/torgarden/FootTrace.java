package com.tyler.app.torgarden;


import biga.shapes2D.Circle;
import biga.utils.Constants;
import biga.utils.GeomUtils;

public class FootTrace extends Circle
{
	public int maxradiu;

	public FootTrace(double x, double y, double radius1)
	{

		super(x, y, 1);
		maxradiu = (int)radius1;
	}
	public boolean ReachLimitation()
	{
		return	this.radius >= maxradiu;
	}
	public void Perform()
	{
		this.radius += 2;
	}
	public double GetDistance(float x1, float y1)
	{
		return Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
	}

}
