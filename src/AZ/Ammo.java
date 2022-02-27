package AZ;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.json.JSONObject;

/**
 * L�ved�k
 */
public abstract class Ammo implements GameEntity
{
    public double rot, speed;
    public double speedx, speedy, x, y, rad;
    int lifeTime;
    double prevx, prevy, prevrad;
    Field f;
    int cooldown = 60;
    Color c = Color.BLACK;
    /**
     * Draw type, default or power
     */
    String type = "default";
    /**
     * Actual name of the ammo type
     */
    String name = "placeholder";
    int shellCount = 10;
    public String pic = "";
    public BufferedImage picture = null;
    public Tank parent;
    XRandom r = new XRandom();
    
    public Ammo(double x, double y, double rad, double speed, double rot, int lifeTime, Field f)
    {
        this.speed = speed;
        setRot(rot);
        this.x = prevx = x;
        this.y = prevy = y;
        this.rad = prevrad = rad;
        this.f = f;
        this.lifeTime = lifeTime;
    }
    
    public Ammo()
    {
        this(0, 0, 0, 0, 0, 0, null);
    }
    
    public void setSeed(long seed)
    {
        r = new XRandom(seed);
    }
    
    /**
     * K�p be�ll�t�sa
     *
     * @param pic K�p file neve
     */
    public void setPic(String pic)
    {
        
        if(pic.equals(this.pic))
            return;
        this.pic = pic;
        try
        {
            if(pic.equals(""))
                picture = null;
            else
                picture = ImageIO.read(Main.class.getResource(Const.Resources + pic));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Tank untargetableTank;
    
    /**
     * Nem tal�lhatja el ezt a tankot
     *
     * @param t  A tank
     * @param ms Ennyi ideig
     */
    public void setInvincible(Tank t, int ms)
    {
        untargetableTank = t;
        Executors.newScheduledThreadPool(1).schedule(() -> untargetableTank = null, ms, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public synchronized void Draw(Graphics2D g)
    {
        Draw(g, c);
    }
    
    /**
     * Rajzol
     *
     * @param g V�szon
     * @param c Sz�n
     */
    public synchronized void Draw(Graphics2D g, Color c)
    {
        g.setColor(c);
        Erase(g);
        if(type.equals("default") || type.equals("laser"))
        {
            g.fillArc((int) (x - rad / 2), (int) (y - rad / 2), (int) rad, (int) rad, 0, 360);
        }
        else if(type.equals("power"))
        {
            g.fillRect((int) (x - rad), (int) (y - rad), (int) rad * 2, (int) rad * 2);
            if(picture != null)
                g.drawImage(picture.getScaledInstance((int) rad * 2, (int) rad * 2, Image.SCALE_DEFAULT),
                        (int) (x - rad), (int) (y - rad), null);
        }
        prevx = x;
        prevy = y;
        prevrad = rad;
    }
    
    @Override
    public synchronized void Erase(Graphics2D g)
    {
        if(this instanceof Laser)
            Log.log("Erase");
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        if(type.equals("default"))
        {
            g.fillRect((int) (prevx - prevrad / 2), (int) (prevy - prevrad / 2), (int) prevrad, (int) prevrad);
        }
        else if(type.equals("power"))
        {
            g.fillRect((int) (prevx - prevrad), (int) (prevy - prevrad), (int) prevrad * 2, (int) prevrad * 2);
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    boolean music = true;
    double changeDir = 0.1;
    
    /**
     * Tick()
     *
     * @param manager J�t�k menedzser
     * @param fal     Sz�moljon e mez? �tk�z�st?
     */
    public synchronized void Move(GameManager manager, boolean fal)
    {
        if(f == null)
            return;
        double ratio = manager.ellapsedTime() / Const.frameTime;
        //Log.log(ratio + " " + manager.ellapsedTime() + " " + System.currentTimeMillis() + " " + (manager instanceof
        // Server));
        int step = (int) Math.floor(speed / (rad / 2)) + 1;//Const given speed
        
        double one = speed * ratio / step;
        double onex = Math.cos(rot) * one;
        double oney = Math.sin(rot) * one;
        
        /*double distx = speedx * ratio, disty = speedy * ratio;
        
        int stepx = (int) (Math.floor(Math.abs(distx) / rad / 2) + 1);
        int stepy = (int) (Math.floor(Math.abs(disty) / rad / 2) + 1);
        int step = Math.max(stepx, stepy);
        double onex = distx / step, oney = disty / step;*/
        while(step > 0)
        {
            x += onex;
            y += oney;
            step--;
        
        
        
        /*x += speedx;
        y += speedy;*/
            boolean touchx = false, touchy = false;
            boolean hitwall = false;
            //if(fal && (x != prevx || y != prevy))
            if(fal)
            {
                int gridX = (int) Math.floor(x / f.gridSize.intValue());
                int gridY = (int) Math.floor(y / f.gridSize.intValue());
                for(int i = Math.max(gridX, 0); i < f.gridWidth && i < gridX + 1; i++)
                {
                    for(int j = Math.max(gridY, 0); j < f.gridHeigth && j < gridY + 1; j++)
                    {
                        int ret = f.mezok[i][j].Collision(x, y, rad / 2);
                        if(ret != 0)
                        {
                            if((ret & 48) == 0)
                            {
                                if((ret & 1) != 0)
                                {
                                    touchx = true;
                                }
                                if((ret & 2) != 0)
                                {
                                    touchy = true;
                                }
                            }
                            else
                            {
                                if((ret & 32) != 0)
                                    if((ret & 0x08) == 0)
                                        touchx = speedx > 0;
                                    else
                                        touchx = speedx < 0;
                                if((ret & 16) != 0)
                                    if((ret & 04) == 0)
                                        touchy = speedy > 0;
                                    else
                                        touchy = speedy < 0;
                            }
                            hitwall = true;
                            if(music)
                            {
                                music = false;
                                // manager.PlayMusic(Const.Music.hitWall);
                            }
                            //break kulso;
                        }
                    }
                }
            }
            //Maybe do not generate value all the time?
            if(touchy)
            {
                double dist = r.nextDouble() * changeDir - changeDir / 2;
                setRot(-rot + dist);
                onex = Math.cos(rot) * one;
                oney = Math.sin(rot) * one;
            }
            if(touchx)
            {
                double dist = r.nextDouble() * changeDir - changeDir / 2;
                setRot(Math.PI - rot + dist);
                onex = Math.cos(rot) * one;
                oney = Math.sin(rot) * one;
            }
            if(!hitwall)
                music = true;
        }
    }
    
    public void Step(GameManager manager)
    {
        Move(manager, true);
    }
    
    @Override
    public synchronized void Tick(GameManager manager)
    {
        Step(manager);
        lifeTime -= manager.ellapsedTime() / Const.frameTime;
        CheckLife(manager);
        CheckPos(manager);
    }
    
    void CheckLife(GameManager manager)
    {
        if(lifeTime <= 0)
        {
            OnDeath(manager);
        }
    }
    
    void CheckPos(GameManager manager)
    {
        if(manager.CheckTank(this))
        {
            OnCollision(manager);
        }
    }
    
    /**
     * Amikor meghal a lövedék
     *
     * @param manager Játék menedzser
     */
    public void OnDeath(GameManager manager)
    {
        manager.RemoveEntity(this);
    }
    
    public void OnCollision(GameManager manager)
    {
        //manager.RemoveEntity(this);
        OnDeath(manager);
    }
    
    /**
     * Amikor �tk�zik egy tankkal
     *
     * @param t A tank
     */
    public void OnContact(Tank t)
    {
        t.dead = true;
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject ret = new JSONObject();
        
        ret.put("x", x);
        ret.put("y", y);
        ret.put("speedx", speedx);
        ret.put("speedy", speedy);
        ret.put("rad", rad);
        ret.put("color", c.getRGB());
        ret.put("type", type);
        ret.put("random", r.toJSON());
        ret.put("speed", speed);
        ret.put("rot", rot);
        
        return ret;
    }
    
    @Override
    public void setFromJSON(JSONObject set)
    {
        x = set.getDouble("x");
        y = set.getDouble("y");
        speed = set.getDouble("speed");
        setRot(set.getDouble("rot"));
        speedx = set.getDouble("speedx");
        speedy = set.getDouble("speedy");
        rad = set.getDouble("rad");
        c = new Color(set.getInt("color"));
        type = set.getString("type");
        setPic(set.optString("pic", ""));
        r.fromJSON(set.getJSONObject("random"));
    }
    
    void setRot(double r)
    {
        rot = r;
        while(rot < 0)
            rot += Math.PI * 2;
        while(rot > Math.PI * 2)
            rot -= Math.PI * 2;
        this.speedx = speed * Math.cos(rot);
        this.speedy = speed * Math.sin(rot);
    }
    
    /**
     * K�sz�t egy �j objektumot abb�l a t�pus� l�ved�kb?l
     *
     * @param x   Koordin�ta
     * @param y   Koordin�ta
     * @param rot Tank elfordul�sa
     * @param f   Labirintus
     * @return Az �j l�ced�k
     */
    public abstract Ammo newInstance(double x, double y, double rot, Field f);
    
    /**
     * K�sz�t egy �j objektumot abb�l a t�pus� l�ved�kb?l
     *
     * @return Az �j l�ved�k
     */
    public abstract Ammo newInstance();
    
    /**
     * Visszaad egy �j alap l�ved�ket
     *
     * @return A l�ved�k
     */
    public static Ammo getDefaultAmmo()
    {
        return new AP();
    }
    
}
