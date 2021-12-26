package AZ;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.json.JSONObject;

/**
 * Tiger E
 */
public class Tank implements GameEntity
{
    
    Image picture;
    BufferedImage original;
    String _pic;
    
    double x, y, rectWidth, rectHeight, prevX, prevY, colX, colY, colW, colH;
    double rot, prevRot;
    final double rotSpeed = 0.05, speed = 1;
    AtomicInteger gridSize;
    Field f;
    public boolean dead;
    
    final int fireCoolDown = 15;
    int cooldown = 0;
    
    GameManager manager;
    Ammo ammo;
    
    public Tank(int x, int y, int rectWidth, int rectHeight, String s, Field f, AtomicInteger grid, GameManager panel)
    {
        this.x = prevX = x;
        this.y = prevY = y;
        this.rectWidth = rectWidth;
        this.rectHeight = rectHeight;
        this.f = f;
        this.manager = panel;
        ammo = new AP();
        colW = this.rectWidth;
        colH = this.rectHeight * 5 / 6;
        colX = this.rectWidth - colW;
        colY = this.rectHeight - colH;
        gridSize = grid;
        Random r = new Random();
        rot = prevRot = r.nextInt(4) * Math.PI / 2;
        for(Entry<String, Integer> entry : Controls.commands.entrySet())
        {
            keys.put(entry.getValue(), false);
        }
        loadImage(rectWidth, rectHeight, s);
        // image=s;
    }
    
    /**
     * Betölti a képet a megadott mérettel
     *
     * @param rectWidth  Szélesség
     * @param rectHeight Magasság
     * @param s          Kép file neve
     */
    private void loadImage(int rectWidth, int rectHeight, String s)
    {
        if(s == "")
            return;
        try
        {
            original = ImageIO.read(Main.class.getResource(Const.Resources + s));
            _pic = s;
            ((Graphics2D) original.getGraphics()).rotate(Math.PI / 2);
            picture = original.getScaledInstance(rectWidth, rectHeight, Image.SCALE_DEFAULT);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Tank(int rectWidth, int rectHeight)
    {
        this(0, 0, rectWidth, rectHeight, "", null, new AtomicInteger(1), null);
        /*
         * x = prevX = y = prevY = 0; this.rectWidth = rectWidth; this.rectHeight =
         * rectHeight; colW = this.rectWidth; colH = this.rectHeight;
         */
    }
    
    public void resize(int width, int height)
    {
        picture = original.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        x *= width / rectWidth;
        y *= width / rectWidth;
        this.rectWidth = width;
        this.rectHeight = height;
    }
    
    public void resize(double arany)
    {
        rectWidth *= arany;
        rectHeight *= arany;
        x *= arany;
        y *= arany;
    }
    
    /**
     * Beállítja a középpontot erre
     *
     * @param centerx középpont X
     */
    public void setCenterx(int centerx)
    {
        x = centerx - (rectWidth / 2) + colX;
    }
    
    /**
     * Beállítja a középpontot erre
     *
     * @param centery középpont Y
     */
    public void setCentery(int centery)
    {
        y = centery - (rectHeight / 2) + colY;
    }
    
    /**
     * Középpont
     *
     * @return X
     */
    public double centerx()
    {
        return x - colX + (rectWidth / 2);
    }
    
    /**
     * Középpont
     *
     * @return Y
     */
    public double centery()
    {
        return y - colY + (rectHeight / 2);
    }
    
    boolean collisionDebug = false;
    
    double drawX = 0, drawY = 0, drawRot = 0;
    
    @Override
    public synchronized void Draw(Graphics2D g)
    {
        Erase(g);
        AffineTransform old = g.getTransform();
        g.rotate(rot, x - colX + rectWidth / 2, y - colY + rectHeight / 2);
        // reset composite
        g.drawImage(picture, (int) (x - colX), (int) (y - colY), null);
        // TODO: colX, colY-nal eltolom a képet, collision box a helyén
        // ( Spawn helyreállítás, center, setcenter)
        
        if(collisionDebug)
        {
            g.setColor(Color.red);
            g.drawRect((int) x, (int) y, (int) colW, (int) colH);
            g.setColor(Color.white);
        }
        
        g.setTransform(old);
        drawX = x;
        drawY = y;
        drawRot = rot;
        /*
         * double rx = x, rw = rectWidth, ry = y, rh = rectHeight;
         *
         * double centerx = rx + rw / 2, centery = ry + rh / 2; double[][] vertices = {
         * { -rw / 2, -rh / 2 }, { +rw / 2, -rh / 2 }, { +rw / 2, +rh / 2 }, { -rw / 2,
         * +rh / 2 } }; double dist = Math.sqrt(vertices[0][0] * vertices[0][0] +
         * vertices[0][1] * vertices[0][1]); Arrays.stream(vertices).forEach(x -> {
         *
         * //double rotation = -Math.asin(x[0] / dist) + rot; double signal = x[0];
         *
         * double copy = x[0]; x[0] = Math.cos(rot) * x[0] - x[1] * Math.sin(rot); x[1]
         * = Math.cos(rot) * x[1] + Math.sin(rot) * copy; x[0] += centerx; x[1] +=
         * centery;
         *
         * }); old = g.getTransform(); g.setColor(Color.blue); g.drawLine((int)
         * vertices[0][0], (int) vertices[0][1], (int) vertices[1][0], (int)
         * vertices[1][1]); g.drawLine((int) vertices[1][0], (int) vertices[1][1], (int)
         * vertices[2][0], (int) vertices[2][1]); g.drawLine((int) vertices[2][0], (int)
         * vertices[2][1], (int) vertices[3][0], (int) vertices[3][1]); g.drawLine((int)
         * vertices[3][0], (int) vertices[3][1], (int) vertices[0][0], (int)
         * vertices[0][1]); g.setTransform(old);
         */
        
    }
    
    @Override
    public void Erase(Graphics2D g)
    {
        AffineTransform old = g.getTransform();
        g.rotate(drawRot, drawX - colX + rectWidth / 2, drawY - colY + rectHeight / 2);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.drawImage(picture, (int) (drawX - colX), (int) (drawY - colY), null);
        
        if(collisionDebug)
        {
            g.drawRect((int) drawX, (int) drawY, (int) colW, (int) colH);
        }
        
        g.setTransform(old);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        
    }
    
    public int rotatedKoor(int koor, double r, boolean x)
    {
        return (int) (koor * (x ? Math.sin(r) : Math.cos(r)));
    }
    
    HashMap<Integer, Boolean> keys = new HashMap<>();
    
    
    public void processKey(KeyEvent e, boolean lenyom)
    {
        processKey(e.getKeyCode(), lenyom);
    }
    
    /**
     * Eltárolja, hogy milyen gombot nyomott le/engedett fel a felhasználó
     *
     * @param code   Billentyű kódja
     * @param lenyom Lenyomta e?
     */
    public void processKey(int code, boolean lenyom)
    {
        if(dead)
            return;
        if(Controls.commands.containsValue(code))
            keys.put(code, lenyom);
    }
    
    public void processKey(KeyEvent e)
    {
        processKey(e, true);
    }
    
    /**
     * Ide készítse el a lövedéket
     *
     * @param rad lövedék sugara
     * @return X koordináta
     */
    public double Spawnx(double rad)
    {
        // return Math.sin(-rot) * (rectHeight / 2 + rad / 2 + 1) + x + rectWidth / 2;
        return -Math.sin(rot) * (-rectHeight / 2 - rad) + centerx();
    }
    
    /**
     * Ide készítse el a lövedéket
     *
     * @param rad lövedék sugara
     * @return Y koordináta
     */
    public double Spawny(double rad)
    {
        // return Math.cos(-rot) * (rectHeight / 2 + rad / 2 + 1) + y + rectHeight / 2;
        return +Math.cos(rot) * (-rectHeight / 2 - rad) + centery();
    }
    
    /**
     * Ide készítse el a lövedéket
     *
     * @return X koordináta
     */
    public double Spawnx()
    {
        return Spawnx(ammo.rad + 2);
    }
    
    /**
     * Ide készítse el a lövedéket
     *
     * @return X koordináta
     */
    public double Spawny()
    {
        return Spawny(ammo.rad + 2);
    }
    
    @Override
    public synchronized void Tick(GameManager manager)
    {
        if(dead)
            return;
        if(keys.get(Controls.commands.get("w")))
        {
            x -= Math.sin(-rot) * speed;
            y -= Math.cos(-rot) * speed;
        }
        if(keys.get(Controls.commands.get("s")))
        {
            x += Math.sin(-rot) * speed;
            y += Math.cos(-rot) * speed;
        }
        if(keys.get(Controls.commands.get("a")))
            rot -= rotSpeed;
        if(keys.get(Controls.commands.get("d")))
            rot += rotSpeed;
        if(keys.get(Controls.commands.get("fire")) && cooldown <= 0)
        {
            // ammo.getClass().getDeclaredConstructor(null);
            Ammo a = ammo.newInstance(Spawnx(), Spawny(), rot - Math.PI / 2, f);
            a.setInvincible(this, 50);
            a.parent = this;
            manager.AddEntity(a);
            manager.PlayMusic(Const.Music.shoot);
            // System.out.println(ammo.cooldown);
            ammo.shellCount--;
            if(ammo.shellCount <= 0)
            {
                ammo = Ammo.getDefaultAmmo();
            }
            cooldown = ammo.cooldown;
        }
        else if(cooldown > 0)
        {
            cooldown--;
        }
        CheckCollision();
        prevX = x;
        prevY = y;
        prevRot = rot;
    }
    
    /**
     * Ellenőrzi hogy ütközött e fallal
     */
    public void CheckCollision(/* , Graphics g */)
    {
        // Maybe optimization?
        int gridX = (int) Math.floor(x / gridSize.intValue());
        int gridY = (int) Math.floor(y / gridSize.intValue());
        
        /*
         * if (f.mezok[gridX][gridY].lineRectColl(x, y, rot, rectWidth, rectHeight,
         * gridSize)) { x = prevX; y = prevY; rot = prevRot; }
         */
        kulso:
        for(int i = Math.max(gridX - 3, 0); i < f.gridWidth && i < gridX + 3; i++)
        {
            for(int j = Math.max(gridY - 3, 0); j < f.gridHeigth && j < gridY + 3; j++)
            {
                if(f.mezok[i][j].lineRectColl(x, y, rot, colW, colH))
                {
                    x = prevX;
                    y = prevY;
                    rot = prevRot;
                    break kulso;
                }
            }
        }
        
    }
    
    /**
     * Ellenőrzi, hogy ütközött e lövedékkel
     *
     * @param ammo A lövedék
     * @return Ütközött e?
     */
    public boolean CheckDestroy(Ammo ammo)
    {
        if(dead)
            return true;
        if(ammo.untargetableTank == this)
            return false;
        double w = rectWidth, h = rectHeight;
        double centerx = centerx(), centery = centery();
        double[][] vertices = {{-w / 2, -h / 2}, {+w / 2, -h / 2}, {+w / 2, +h / 2}, {-w / 2, +h / 2}};
        Arrays.stream(vertices).forEach(x ->
        {
            double copy = x[0];
            x[0] = Math.cos(rot) * x[0] - x[1] * Math.sin(rot);
            x[1] = Math.cos(rot) * x[1] + Math.sin(rot) * copy;
            x[0] += centerx;
            x[1] += centery;
            
        });
        boolean left = lineCircle(vertices[0][0], vertices[0][1], vertices[3][0], vertices[3][1], ammo.x, ammo.y,
                ammo.rad);
        boolean right = lineCircle(vertices[1][0], vertices[1][1], vertices[2][0], vertices[2][1], ammo.x, ammo.y,
                ammo.rad);
        boolean top = lineCircle(vertices[0][0], vertices[0][1], vertices[1][0], vertices[1][1], ammo.x, ammo.y,
                ammo.rad);
        boolean bottom = lineCircle(vertices[3][0], vertices[3][1], vertices[2][0], vertices[2][1], ammo.x, ammo.y,
                ammo.rad);
        if(left || right || top || bottom)
        {
            // System.out.println("Dead");
            ammo.OnContact(this);
            return true;
        }
        // dead = false;
        return false;
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject ret = new JSONObject();
        
        ret.put("x", x);
        ret.put("y", y);
        ret.put("rot", rot);
        ret.put("pic", _pic);
        
        return ret;
    }
    
    @Override
    public void setFromJSON(JSONObject set)
    {
        x = set.getDouble("x");
        y = set.getDouble("y");
        rot = set.getDouble("rot");
        String s = set.getString("pic");
        if(_pic == null || !_pic.equals(s))
        {
            loadImage((int) rectWidth, (int) rectHeight, s);
            _pic = s;
        }
    }
    
    /**
     * Vonal kör ütközés
     *
     * @param x1 Vonal x1
     * @param y1 Vonal y1
     * @param x2 Vonal x2
     * @param y2 Vonal y2
     * @param cx Kör x
     * @param cy Kör y
     * @param r  Kör sugár
     * @return ütközött e?
     */
    boolean lineCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r)
    {
        
        // is either end INSIDE the circle?
        // if so, return true immediately
        boolean inside1 = pointCircle(x1, y1, cx, cy, r);
        boolean inside2 = pointCircle(x2, y2, cx, cy, r);
        if(inside1 || inside2)
            return true;
        
        // get length of the line
        double distX = x1 - x2;
        double distY = y1 - y2;
        double len = Math.sqrt((distX * distX) + (distY * distY));
        
        // get dot product of the line and circle
        double dot = ((((cx - x1) * (x2 - x1)) + ((cy - y1) * (y2 - y1))) / Math.pow(len, 2));
        
        // find the closest point on the line
        double closestX = x1 + (dot * (x2 - x1));
        double closestY = y1 + (dot * (y2 - y1));
        
        // is this point actually on the line segment?
        // if so keep going, but if not, return false
        boolean onSegment = linePoint(x1, y1, x2, y2, closestX, closestY);
        if(!onSegment)
            return false;
        
        // optionally, draw a circle at the closest
        // point on the line
        /*
         * fill(255,0,0); noStroke(); ellipse(closestX, closestY, 20, 20);
         */
        
        // get distance to closest point
        /*
         * distX = closestX - cx; distY = closestY - cy; float distance = sqrt(
         * (distX*distX) + (distY*distY) );
         *
         * if (distance <= r)
         */
        return dist(closestX, closestY, cx, cy) <= r;
    }
    
    /**
     * Pont kör ütközés
     *
     * @param px Pont x
     * @param py Pont y
     * @param cx Kör x
     * @param cy Kör y
     * @param r  Kör sugár
     * @return ütközött e?
     */
    boolean pointCircle(double px, double py, double cx, double cy, double r)
    {
        
        // get distance between the point and circle's center
        // using the Pythagorean Theorem
        /*
         * float distX = px - cx; float distY = py - cy; float distance = sqrt(
         * (distX*distX) + (distY*distY) );
         */
        
        // if the distance is less than the circle's
        // radius the point is inside!
        // if (distance <= r)
        return dist(px, py, cx, cy) <= r;
    }
    
    /**
     * Vonal pont ütközés
     *
     * @param x1 Vonal x1
     * @param y1 Vonal y1
     * @param x2 Vonal x2
     * @param y2 Vonal y2
     * @param px Pont x
     * @param py Pont y
     * @return ütközött e?
     */
    boolean linePoint(double x1, double y1, double x2, double y2, double px, double py)
    {
        
        // get distance from the point to the two ends of the line
        double d1 = dist(px, py, x1, y1);
        double d2 = dist(px, py, x2, y2);
        
        // get the length of the line
        double lineLen = dist(x1, y1, x2, y2);
        
        // since floats are so minutely accurate, add
        // a little buffer zone that will give collision
        double buffer = 0.1f; // higher # = less accurate
        
        // if the two distances are equal to the line's
        // length, the point is on the line!
        // note we use the buffer here to give a range,
        // rather than one #
        return d1 + d2 >= lineLen - buffer && d1 + d2 <= lineLen + buffer;
    }
    
    /**
     * Két pont távolsága
     *
     * @param x1 Pont1 x
     * @param y1 Pont2 y
     * @param x2 Pont2 x
     * @param y2 Pont2 y
     * @return Távolság
     */
    double dist(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    
}
