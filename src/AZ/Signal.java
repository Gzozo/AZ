package AZ;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import org.json.JSONObject;

/**
 * Felhasználó jelzésére egy nagy piros kör
 */
public class Signal extends Ammo
{
    
    int r, g, b, a;
    double delta, endRad, deltaRad;
    
    public Signal(double x, double y, double startRad, double endRad, int lifeTime, Color c)
    {
        super(x, y, startRad, 0, 0, lifeTime, null);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
        a = 255;
        delta = (double) a / lifeTime;
        this.endRad = endRad;
        deltaRad = (rad - endRad) / lifeTime;
    }
    
    public Signal(int lifeTime, Color c)
    {
        this(0, 0, 1, 1, lifeTime, c);
    }
    
    public Signal(Color c)
    {
        this((int) (2 * Const.framerate), c);
    }
    
    /**
     * Kisebb kör, átlatszóbb
     *
     * @param manager
     */
    @Override
    public void Step(GameManager manager)
    {
        a = Math.max((int) (a - delta), 0);
        rad = Math.max((int) (rad - deltaRad), 0);
        c = new Color(r, g, b, a);
        
		/*if (manager.CheckTank(this))
		{
			manager.RemoveEntity(this);
		}*/
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject ret = super.toJSON();
        ret.put("endrad", endRad);
        return ret;
    }
    
    @Override
    public void setFromJSON(JSONObject set)
    {
        super.setFromJSON(set);
        endRad = set.getDouble("endrad");
        deltaRad = (rad - endRad) / lifeTime;
        
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
