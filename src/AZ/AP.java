package AZ;

/**
 * Armor Piercing shell
 */
public class AP extends Ammo
{
    
    public AP(double x, double y, double rot, Field f)
    {
        super(x, y, 5, 2.5, rot, 600, f);
        setPic("");
        name = "AP";
        shellCount = 1000;
    }
    
    public AP()
    {
        this(0, 0, 0, null);
    }
    
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        return new AP(x, y, rot, f);
    }
    
    @Override
    public Ammo newInstance()
    {
        return new AP();
    }
    
}
