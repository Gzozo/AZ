package AZ;

import java.awt.Color;

/**
 * Sharpnel Shell
 */
public class Sharpnel extends Ammo
{
    
    public Sharpnel(double x, double y, double rot, Field f)
    {
        super(x, y, 3, 2, rot, 500, f);
        setPic("sharpnel.png");
        cooldown = 80;
        c = Color.GRAY;
        name = "Sharpnel";
        shellCount = 3;
    }
    
    public Sharpnel()
    {
        this(0, 0, 0, null);
    }
    
    @Override
    public synchronized void Step(GameManager manager)
    {
        Move(manager, false);
    }
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        return new Sharpnel(x, y, rot, f);
    }
    
    @Override
    public Ammo newInstance()
    {
        return new Sharpnel();
    }
}