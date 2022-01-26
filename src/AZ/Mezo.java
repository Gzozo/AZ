package AZ;

import java.awt.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.*;

public class Mezo
{
    int x, y;
    
    boolean[] zart = new boolean[4];
    short[] vege = new short[4];
    
    boolean elert = false;
    AtomicInteger grid;
    
    Mezo(int x, int y, AtomicInteger grid)
    {
        this.x = x;
        this.y = y;
        for(int i = 0; i < 4; i++)
        {
            zart[i] = true;
            vege[i] = 3;
        }
        this.grid = grid;
    }
    
    /**
     * Mező közepe
     *
     * @return X koordináta
     */
    int centerx()
    {
        return x * grid.intValue() + (grid.intValue() / 2);
    }
    
    /**
     * Mező közepe
     *
     * @return Y koordináta
     */
    int centery()
    {
        return y * grid.intValue() + (grid.intValue() / 2);
    }
    
    /**
     * JSON objektumba csomagolja a mezőket
     *
     * @return A JSON objektum
     */
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("x", x);
        json.put("y", y);
        json.put("0", zart[0]);
        json.put("1", zart[1]);
        json.put("2", zart[2]);
        json.put("3", zart[3]);
        return json;
    }
    
    boolean debug = Settings.DEBUG;
    
    void drawWall(Graphics g, int x1, int y1, int x2, int y2, int id)
    {
        if(vege[id] == 0 || !debug)
        {
            g.drawLine(x1, y1, x2, y2);
            return;
        }
        int midx = (x1 + x2) / 2, midy = (y1 + y2) / 2;
        if((vege[id] & 0x01) != 0)
            g.setColor(Color.YELLOW);
        g.drawLine(x1, y1, midx, midy);
        g.setColor(Color.BLACK);
        if((vege[id] & 0x02) != 0)
            g.setColor(Color.YELLOW);
        g.drawLine(midx, midy, x2, y2);
        g.setColor(Color.BLACK);
    }
    
    /**
     * Kirajzolás
     *
     * @param g Vászon
     */
    public void render(Graphics g)
    {
        // felso
        if(zart[0])
        {
            drawWall(g, x * grid.intValue(), y * grid.intValue(), x * grid.intValue() + grid.intValue(),
                    y * grid.intValue(), 0);
        }
        // bal
        if(zart[1])
        {
            drawWall(g, x * grid.intValue(), y * grid.intValue(), x * grid.intValue(),
                    y * grid.intValue() + grid.intValue(), 1);
        }
        // alsó
        if(zart[2])
        {
            drawWall(g, x * grid.intValue(), y * grid.intValue() + grid.intValue(),
                    x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), 2);
        }
        // jobb
        if(zart[3])
        {
            drawWall(g, x * grid.intValue() + grid.intValue(), y * grid.intValue(),
                    x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), 3);
            
        }
    }
    
    Graphics2D g0;
    
    /**
     * Mező téglalap ütközés észlelés
     *
     * @param rx  Téglalap x
     * @param ry  Téglalap y
     * @param rot Téglalap forgása radiánban
     * @param w   Téglalap szélessége
     * @param h   Téglalap magassága
     * @return ütközött e?
     */
    public boolean lineRectColl(double rx, double ry, double rot, double w, double h/* , Graphics g */)
    {
        // g0 = (Graphics2D) g;
        // double[][] res = { { 0, 0 }, { w, 0 }, { w, h }, { 0, h } };
        // for (int i = 0; i < 4; i++)
        // {
        if(zart[0] && lineRect(x * grid.intValue(), y * grid.intValue(), x * grid.intValue() + grid.intValue(),
                y * grid.intValue(), rx, ry, w, h, rot))
        {
            // Log.log(x + " " + y + " felso");
            return true;
        }
        if(zart[1] && lineRect(x * grid.intValue(), y * grid.intValue(), x * grid.intValue(),
                y * grid.intValue() + grid.intValue(), rx, ry, w, h, rot))
        {
            // Log.log(x + " " + y + " bal");
            return true;
        }
        if(zart[2] && lineRect(x * grid.intValue(), y * grid.intValue() + grid.intValue(),
                x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), rx, ry, w, h, rot))
        {
            // Log.log(x + " " + y + " alsó");
            return true;
        }
        // Log.log(x + " " + y + " jobb");
        return zart[3] && lineRect(x * grid.intValue() + grid.intValue(), y * grid.intValue(),
                x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), rx, ry, w, h, rot);
        // }
    }
    
    /**
     * Vonal téglalap ütközés
     *
     * @param x1 Vonal x1
     * @param y1 Vonal y1
     * @param x2 Vonal x2
     * @param y2 Vonal y2
     * @param rx Téglalap x
     * @param ry Téglalap y
     * @param rw Téglalap szélessége
     * @param rh Téglalap magassága
     * @return ütközött e?
     */
    boolean lineRect(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh, double rot)
    {
        
        // check if the line has hit any of the rectangle's sides
        // uses the Line/Line function below
        
        double centerx = rx + rw / 2, centery = ry + rh / 2;
        double[][] vertices = {{-rw / 2, -rh / 2}, {+rw / 2, -rh / 2}, {+rw / 2, +rh / 2}, {-rw / 2, +rh / 2}};
        Arrays.stream(vertices).forEach(x ->
        {
            double copy = x[0];
            x[0] = Math.cos(rot) * x[0] - x[1] * Math.sin(rot);
            x[1] = Math.cos(rot) * x[1] + Math.sin(rot) * copy;
            x[0] += centerx;
            x[1] += centery;
            
        });
        
        // AffineTransform old = g0.getTransform(); old.translate(centerx, centery);
        // g0.setColor(Color.blue);
        // g0.drawLine((int) vertices[0][0], (int) vertices[0][1], (int) vertices[1][0],
        // (int) vertices[1][1]);
        // g0.drawLine((int) vertices[1][0], (int) vertices[1][1], (int) vertices[2][0],
        // (int) vertices[2][1]);
        // g0.drawLine((int) vertices[2][0], (int) vertices[2][1], (int) vertices[3][0],
        // (int) vertices[3][1]);
        // g0.drawLine((int) vertices[3][0], (int) vertices[3][1], (int) vertices[0][0],
        // (int) vertices[0][1]);
        // g0.setTransform(old);
        
        boolean left = lineLine(x1, y1, x2, y2, vertices[0][0], vertices[0][1], vertices[3][0], vertices[3][1]);
        boolean right = lineLine(x1, y1, x2, y2, vertices[1][0], vertices[1][1], vertices[2][0], vertices[2][1]);
        boolean top = lineLine(x1, y1, x2, y2, vertices[0][0], vertices[0][1], vertices[1][0], vertices[1][1]);
        boolean bottom = lineLine(x1, y1, x2, y2, vertices[3][0], vertices[3][1], vertices[2][0], vertices[2][1]);
        
        // if ANY of the above are true, the line
        // has hit the rectangle
        return left || right || top || bottom;
    }
    
    /**
     * Vonal vonal ütközés
     *
     * @param x1 Vonal1 x1
     * @param y1 Vonal1 y1
     * @param x2 Vonal1 x2
     * @param y2 Vonal1 y2
     * @param x3 Vonal2 x1
     * @param y3 Vonal2 y1
     * @param x4 Vonal2 x2
     * @param y4 Vonal2 y2
     * @return ütközött e?
     */
    boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
    {
        
        // calculate the direction of the lines
        double uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        double uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        
        // if uA and uB are between 0-1, lines are colliding
        return uA > 0 && uA < 1 && uB > 0 && uB < 1;
    }
    
    
    int CheckWalls(double x1, double y1, double x2, double y2, double tx, double ty, double r, int ret, int mask,
                   int index)
    {
        short val = lineCircle(x1, y1, x2, y2, tx, ty, r);
        if((val & 2) != 0 && (vege[index] & (1 << (val & 1))) != 0)
        {
            if(index % 2 == 0)
            {
                /*if(x1 < tx && tx < x2)
                    ret = ret | mask;
                else
                    ret = ret | (~mask) % 4;*/
                //kell irany(+,-), koor(x,y)
                //sarok x?(32), sarok y(16), x irany(8), y irany(4)
                ret |= 32 | ((val & 1) << 3);
                
            }
            else
            {
                /*if(y1 < ty && ty < y2)
                    ret = ret | mask;
                else
                    ret = ret | (~mask) % 4;*/
                ret |= 16 | ((val & 1) << 2);
            }
        }
        else if((val & 0x01) != 0)
        {
            ret = ret | mask;
        }
        return ret;
    }
    
    /**
     * Mező kör ütközés
     *
     * @param tx Kör x
     * @param ty Kör y
     * @param r  Kör sugár
     * @return ütközött e?
     */
    public int Collision(double tx, double ty, double r /* float[] difs, */)
    {
        int ret = 0;
        if(zart[0])
        {
            ret = CheckWalls(x * grid.intValue(), y * grid.intValue(), x * grid.intValue() + grid.intValue(),
                    y * grid.intValue(), tx, ty, r, ret, 2, 0);
        }
        if(zart[1])
        {
            ret = CheckWalls(x * grid.intValue(), y * grid.intValue(), x * grid.intValue(),
                    y * grid.intValue() + grid.intValue(), tx, ty, r, ret, 1, 1);
        }
        if(zart[2])
        {
            ret = CheckWalls(x * grid.intValue(), y * grid.intValue() + grid.intValue(),
                    x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), tx, ty, r, ret, 2, 2);
        }
        if(zart[3])
        {
            ret = CheckWalls(x * grid.intValue() + grid.intValue(), y * grid.intValue(),
                    x * grid.intValue() + grid.intValue(), y * grid.intValue() + grid.intValue(), tx, ty, r, ret, 1, 3);
        }
        return ret;
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
    short lineCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r)
    {
        
        // is either end INSIDE the circle?
        // if so, return true immediately
        boolean inside1 = pointCircle(x1, y1, cx, cy, r);
        boolean inside2 = pointCircle(x2, y2, cx, cy, r);
        if(inside1 || inside2)
            return (short) ((inside1 ? 0 : 1) | 0x02);
        
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
            return 0;
        
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
        return (short) (dist(closestX, closestY, cx, cy) <= r ? 1 : 0);
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
