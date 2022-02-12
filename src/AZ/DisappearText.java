package AZ;

import java.awt.*;

public class DisappearText extends Ammo
{
    
    String text;
    int size;
    
    public DisappearText(String text, double x, double y, int size)
    {
        this(text, x, y, size, (int) (10 * Const.framerate));
    }
    
    public DisappearText(String text, double x, double y, int size, int lifeTime)
    {
        super();
        this.x = x;
        this.y = y;
        this.size = size;
        this.text = text;
        this.lifeTime = lifeTime;
    }
    
    @Override
    public synchronized void Draw(Graphics2D g)
    {
        Font f = g.getFont();
        Font current = f.deriveFont((float) size);
        g.setFont(current);
        g.setColor(Color.black);
        g.drawString(text, (int) x, (int) y);
        g.setFont(f);
    }
    
    @Override
    public synchronized void Erase(Graphics2D g)
    {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        Draw(g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    @Override
    public synchronized void Step(GameManager manager)
    {
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
