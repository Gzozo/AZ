package AZ;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Laser extends Ammo
{
    BufferedImage image = null;
    double speedPerTick = 10;
    boolean erase = false;
    static int defaultLifeTime = (int) (1 * Const.framerate);
    double startx, starty, startrot;
    Thread onDeath = new Thread();
    //TOOO: enum for the different states, alive, erase, dead
    boolean dead = false;
    
    
    public Laser(double x, double y, double rot, Field f)
    {
        this(x, y, rot, f, defaultLifeTime);
    }
    
    public Laser(double x, double y, double rot, Field f, int lifeTime)
    {
        super(x, y, 2, 1, rot, lifeTime, f);
        if(f != null)
        {
            int width = f.gridWidth * f.gridSize.intValue();
            int height = f.gridHeigth * f.gridSize.intValue();
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        startx = x;
        starty = y;
        startrot = rot;
        changeDir = 0;
        name = "Laser";
        type = "laser";
        c = Color.blue;
    }
    
    public Laser()
    {
        this(0, 0, 0, null);
    }
    
    Laser(double x, double y)
    {
        super(x, y, 2, 0, 0, (int) (1 * Const.framerate), null);
    }
    
    
    @Override
    public synchronized void Draw(Graphics2D g)
    {
        if(erase)
        {
            /*if(dead)
                return;
            
            
            //TODO: More and more transparent image, for continuos erase
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.XOR));
            g.drawImage(image, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            onDeath.start();
            dead = true;*/
        }
        else
        {
            super.Draw((Graphics2D) image.getGraphics());
            g.drawImage(image, 0, 0, null);
        }
    }
    
    @Override
    public synchronized void Erase(Graphics2D g)
    {
        //super.Erase(g);
        if(erase)
        {
            //g.setComposite(AlphaComposite.getInstance(AlphaComposite.XOR));
            g.setComposite((AlphaComposite.getInstance(AlphaComposite.CLEAR)));
            g.drawImage(image, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        }
        
    }
    
    @Override
    public void Step(GameManager manager)
    {
        if(erase)
        {
            /*((Graphics2D) image.getGraphics()).setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            LaserStep(manager, false);
            ((Graphics2D) image.getGraphics()).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));*/
            
            
        }
        else
        {
            LaserStep(manager, true);
        }
        //lifeTime -= ratio;
        //manager.AddEntity(new Laser(x, y));
        //Move(manager, true);
    }
    
    private void LaserStep(GameManager manager, boolean check)
    {
        double ratio = manager.ellapsedTime() / Const.frameTime;
        for(int i = 0; i < speedPerTick * ratio; i++)
        {
            Move(manager, true);
            Draw((Graphics2D) image.getGraphics());
            if(check)
                CheckPos(manager);
        }
    }
    
    @Override
    public void OnCollision(GameManager manager)
    {
        super.OnCollision(manager);
    }
    
    @Override
    public void OnDeath(GameManager manager)
    {
        /*lifeTime = defaultLifeTime;
        x = startx;
        y = starty;
        rot = startrot;
        rad = rad;
        c = Color.black;*/
        /*ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> super.OnDeath(manager), 2, TimeUnit.SECONDS);*/
        onDeath = new Thread(() -> super.OnDeath(manager));
        erase = true;
        super.OnDeath(manager);
    }
    
    @Override
    public Ammo newInstance(double x, double y, double rot, Field f)
    {
        return new Laser(x, y, rot, f);
    }
    
    @Override
    public Ammo newInstance()
    {
        return new Laser();
    }
}
