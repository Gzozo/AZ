package AZ;

import java.awt.Color;
import java.util.Random;

/**
 * High Explosive Shell
 */
public class HE extends Ammo
{
    int sharpnelCount;
    
    public HE()
    {
        this(0, 0, 0, null);
    }
    
    public HE(double x, double y, double rot, Field f)
    {
        this(x, y, rot, f, 20);
    }
    
    public HE(double x, double y, double rot, Field f, int sharpnelCount)
    {
        super(x, y, 9, 2, rot, 200, f);
        setPic("he.png");
        this.sharpnelCount = sharpnelCount;
        cooldown = 120;
        c = Color.ORANGE;
        name = "HE";
        shellCount = 2;
    }
    
    /**
     * Felrobban
     *
     * @param manager Játék menedzser
     */
    @Override
    public void OnDeath(GameManager manager)
    {
        super.OnDeath(manager);
        Detonate(manager);
    }
    
    /**
     * Felrobban
     *
     * @param manager Játék menedzser
     */
    public void Detonate(GameManager manager)
    {
        XRandom r = new XRandom();
        for(int i = 0; i < sharpnelCount; i++)
        {
            Sharpnel s = new Sharpnel(x, y, r.nextDouble() * 2 * Math.PI, f);
            s.parent = parent;
            manager.AddEntity(s);
        }
        manager.PlayMusic(Const.Music.he_explode);
    }
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        return new HE(x, y, rot, f);
    }
    
    @Override
    public Ammo newInstance()
    {
        return new HE();
    }
}
