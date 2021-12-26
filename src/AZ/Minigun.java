package AZ;

import java.awt.Color;

/**
 * MINIGUN
 */
public class Minigun extends Ammo
{
	//TODO: Random spay
	public Minigun(double x, double y, double rot, Field f)
	{
		super(x, y, 3, 2, rot, 500, f);
		setPic("minigun.png");
		cooldown = 1;
		c = Color.GRAY;
		shellCount = 50;
	}
	
	public Minigun()
	{
		this(0, 0, 0, null);
	}
	
	@Override
	public Ammo newInstance(double x, double y, double rot, Field f)
	{
		return new Minigun(x, y, rot, f);
	}
	
	@Override
	public Ammo newInstance()
	{
		return newInstance(0, 0, 0, null);
	}
	
}
