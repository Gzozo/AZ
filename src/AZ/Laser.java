package AZ;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Laser extends Ammo
{
    BufferedImage image = null;
    
    public Laser(double x, double y, double rot, Field f)
    {
        this(x, y, rot, f, (int) (1 * Const.framerate));
    }
    
    public Laser(double x, double y, double rot, Field f, int lifeTime)
    {
        super(x, y, 2, 40, rot, lifeTime, f);
    }
    
    
    @Override
    public synchronized void Draw(Graphics2D g)
    {
        if(image == null)
        {
            g.getClipBounds();
        }
        super.Draw(g);
    }
    
    @Override
    public synchronized void Erase(Graphics2D g)
    {
        super.Erase(g);
    }
    
    @Override
    public void Step(GameManager manager)
    {
        super.Step(manager);
    }
    
    @Override
    public void OnContact(Tank t)
    {
        super.OnContact(t);
    }
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        return null;
    }
    
    @Override
    public Ammo newInstance()
    {
        return null;
    }
}
