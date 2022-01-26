package AZ;

import java.awt.Color;
import java.util.Random;

/**
 * MINIGUN
 */
public class Minigun extends Ammo
{
    Random r = new Random();
    double szoras = AZ.Settings.szoras;
    
    public Minigun(double x, double y, double rot, Field f)
    {
        super(x, y, 3, 2, rot, 500, f);
        setPic("minigun.png");
        cooldown = 1;
        c = Color.GRAY;
        shellCount = 50;
        name = "Minigun";
    }
    
    public Minigun()
    {
        this(0, 0, 0, null);
    }
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        double elter = r.nextDouble() * szoras - szoras / 2;
        return new Minigun(x, y, rot + elter, f);
    }
    
    @Override
    public Ammo newInstance()
    {
        return newInstance(0, 0, 0, null);
    }
    
}
