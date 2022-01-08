package AZ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONObject;

/**
 * Pálya
 */
public class Field
{
    // how many Mezo
    public int gridWidth, gridHeigth;
    public Mezo[][] mezok;
    AtomicInteger gridSize;
    public long seed;
    
    public Field(int gw, int gh, AtomicInteger gridSize)
    {
        gridWidth = gw;
        gridHeigth = gh;
        mezok = new Mezo[gridWidth][gridHeigth];
        this.gridSize = gridSize;
        for(int i = 0; i < mezok.length; i++)
        {
            for(int j = 0; j < mezok[i].length; j++)
            {
                mezok[i][j] = new Mezo(i, j, gridSize);
            }
        }
    }
    
    /**
     * Mezők betöltése
     *
     * @param m A mező
     */
    public void setField(Mezo[][] m)
    {
        mezok = m;
    }
    
    ArrayList<Mezo> elert;
    int elerni;
    
    void CheckVege()
    {
        //első index: fal
        //második: {i változás, j változás}
        //int[][] sarkok = {{1, 0}, {}};
        //TODO: Sarkok
        for(int i = 1; i < gridWidth - 1; i++)
        {
            for(int j = 1; j < gridHeigth - 1; j++)
            {
                /*for(int fal = 0; fal < 4; fal++)
                {
                    if(mezok[i][j].zart[fal])
                    {
                        mezok[i][j].vege[fal] = 0;
                        if(i - sarkok[fal][0] >= 0 && !mezok[i - sarkok[fal][0]][j].zart[fal])
                        {
                            mezok[i][j].vege[fal] |= 0x01;
                        }
                        if(i + sarkok[fal][0] < gridWidth && !mezok[i + sarkok[fal][0]][j].zart[fal])
                        {
                            mezok[i][j].vege[fal] |= 0x02;
                        }
                        mezok[i][j - 1].vege[(fal + 2) % 4] = mezok[i][j].vege[fal];
                        
                    }
                }*/
                if(mezok[i][j].zart[0] && !mezok[i][j].zart[1] && !mezok[i][j].zart[3])
                {
                    mezok[i][j].vege[0] = 0;
                    if(!mezok[i - 1][j].zart[0])
                        mezok[i][j].vege[0] |= 0x01;
                    if(!mezok[i + 1][j].zart[0])
                        mezok[i][j].vege[0] |= 0x02;
                    mezok[i][j - 1].vege[2] &= mezok[i][j].vege[0];
                    mezok[i][j].vege[0] &= mezok[i][j - 1].vege[2];
                }
                else
                    mezok[i][j - 1].vege[2] = mezok[i][j].vege[0] = 0;
                if(mezok[i][j].zart[2] && !mezok[i][j].zart[1] && !mezok[i][j].zart[3])
                {
                    //mezok[i][j].vege[2] = mezok[i][j + 1].vege[0] = (short) (!mezok[i - 1][j].zart[2] ? 1 :
                    // !mezok[i + 1][j].zart[2] ? 2 : 0);
                    mezok[i][j].vege[2] = 0;
                    if(!mezok[i - 1][j].zart[2])
                        mezok[i][j].vege[2] |= 0x01;
                    if(!mezok[i + 1][j].zart[2])
                        mezok[i][j].vege[2] |= 0x02;
                    mezok[i][j + 1].vege[0] &= mezok[i][j].vege[2];
                    mezok[i][j].vege[2] &= mezok[i][j + 1].vege[0];
                }
                else
                    mezok[i][j + 1].vege[0] = mezok[i][j].vege[2] = 0;
                if(mezok[i][j].zart[1] && !mezok[i][j].zart[0] && !mezok[i][j].zart[2])
                {
                    //mezok[i][j].vege[1] = mezok[i - 1][j].vege[3] = (short) (!mezok[i][j - 1].zart[1] ? 1 :
                    //        !mezok[i][j + 1].zart[1] ? 2 : 0);
                    mezok[i][j].vege[1] = 0;
                    if(!mezok[i][j - 1].zart[1])
                        mezok[i][j].vege[1] |= 0x01;
                    if(!mezok[i][j + 1].zart[1])
                        mezok[i][j].vege[1] |= 0x02;
                    mezok[i - 1][j].vege[3] &= mezok[i][j].vege[1];
                    mezok[i][j].vege[1] &= mezok[i - 1][j].vege[3];
                }
                else
                    mezok[i - 1][j].vege[3] = mezok[i][j].vege[1] = 0;
                if(mezok[i][j].zart[3] && !mezok[i][j].zart[0] && !mezok[i][j].zart[2])
                {
                    //mezok[i][j].vege[3] = mezok[i + 1][j].vege[1] = (short) (!mezok[i][j - 1].zart[3] ? 1 :
                    //        !mezok[i][j + 1].zart[3] ? 2 : 0);
                    mezok[i][j].vege[3] = 0;
                    if(!mezok[i][j - 1].zart[3])
                        mezok[i][j].vege[3] |= 0x01;
                    if(!mezok[i][j + 1].zart[3])
                        mezok[i][j].vege[3] |= 0x02;
                    mezok[i + 1][j].vege[1] &= mezok[i][j].vege[3];
                    mezok[i][j].vege[3] &= mezok[i + 1][j].vege[1];
                }
                else
                    mezok[i + 1][j].vege[1] = mezok[i][j].vege[3] = 0;
            }
        }
        //TODO: befejezni!!
        for(int i = 1; i < gridWidth; i++)
        {
            mezok[i][0].vege[0] = 0;
            if(mezok[i][0].zart[1] && !mezok[i][0].zart[0] && !mezok[i][0].zart[2])
            {
                mezok[i][0].vege[1] &= mezok[i - 1][0].vege[3] &= (short) (!mezok[i][1].zart[1] ? 2 : 0);
            }
            else
                mezok[i][0].vege[1] &= mezok[i - 1][0].vege[3] = 0;
            if(mezok[i][gridHeigth - 1].zart[1] && !mezok[i][gridHeigth - 1].zart[0] && !mezok[i][gridHeigth - 1].zart[2])
            {
                mezok[i][gridHeigth - 1].vege[1] &= mezok[i - 1][gridHeigth - 1].vege[3] &= (short) (!mezok[i][gridHeigth - 2].zart[1] ? 1 : 0);
            }
            else
                mezok[i][gridHeigth - 1].vege[1] &= mezok[i - 1][gridHeigth - 1].vege[3] = 0;
        }
        for(int j = 1; j < gridHeigth; j++)
        {
            mezok[0][j].vege[1] = 0;
            if(mezok[0][j].zart[0] && !mezok[0][j].zart[1] && !mezok[0][j].zart[3])
            {
                mezok[0][j].vege[0] &= mezok[0][j - 1].vege[2] &= (short) (!mezok[1][j].zart[0] ? 2 : 0);
            }
            else
                mezok[0][j].vege[0] &= mezok[0][j - 1].vege[2] = 0;
            if(mezok[gridWidth - 1][j].zart[0] && !mezok[gridWidth - 1][j].zart[1] && !mezok[gridWidth - 1][j].zart[3])
            {
                mezok[gridWidth - 1][j].vege[0] &= mezok[gridWidth - 1][j - 1].vege[2] &= (short) (!mezok[gridWidth - 2][j].zart[0] ? 1 : 0);
            }
            else
                mezok[gridWidth - 1][j].vege[0] &= mezok[gridWidth - 1][j - 1].vege[2] = 0;
        }
    }
    
    /**
     * Legenerálja a pályát
     *
     * @param seed A pálya seedje
     */
    public void GenerateField(long seed)
    {
        this.seed = seed;
        elert = new ArrayList<>();
        // println("Generate");
        elerni = gridWidth * gridHeigth;
        for(int i = 0; i < gridWidth; i++)
        {
            for(int j = 0; j < gridHeigth; j++)
            {
                mezok[i][j] = new Mezo(i, j, gridSize);
            }
        }
        
        mezok[0][0].elert = true;
        elerni--;
        elert.add(mezok[0][0]);
        
        Random r = new Random(seed);
        int megTorFal = 300;
        
        while(elerni > 0 /* elert.size() > 0 */)
        {
            attor(elert.get(0).x, elert.get(0).y, r.nextInt(4), false);
            elert.add(elert.get(0));
            elert.remove(0);
        }
        while(megTorFal > 0)
        {
            Mezo m = elert.get(r.nextInt(elert.size()));
            attor(m.x, m.y, r.nextInt(4), true);
            megTorFal--;
        }
        CheckVege();
        
        
    }
    
    /**
     * Generál egy teljesen véletlenszerű pályát
     */
    public void GenerateField()
    {
        GenerateField(seedUniquifier() ^ System.nanoTime());
    }
    
    /**
     * Kirajzolja a labirintust;
     *
     * @param g Erre rajzol
     * @param d Maximális méret
     */
    public void draw(Graphics g, Dimension d)
    {
        gridSize.set(Math.min(d.width / gridWidth, d.height / gridHeigth));
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.black);
        g.drawRect(0, 0, d.width - 1, d.height - 1);
        for(int i = 0; i < gridWidth; i++)
        {
            for(int j = 0; j < gridHeigth; j++)
            {
                mezok[i][j].render(g);
            }
        }
    }
    
    /**
     * Kiszámolja a maximális mező távolságot
     *
     * @param d Maximális méret a vásznon
     */
    public void setGridSize(Dimension d)
    {
        gridSize.set(Math.min(d.width / gridWidth, d.height / gridHeigth));
    }
    
    private static long seedUniquifier()
    {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for(; ; )
        {
            long current = seedUniquifier.get();
            long next = current * 1181783497276652981L;
            if(seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }
    
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
    
    /**
     * Áttör egy falat, azaz két szomszédos mező falát eltörli
     *
     * @param x     Mező koordinátája
     * @param y     Mező koordinátája
     * @param dir   Irány, amerre kiütjük a falat
     * @param plusz Üssünk ki már elért mezők között is falat?
     */
    private void attor(int x, int y, int dir, boolean plusz)
    {
        int nx = x, ny = y;
        if(dir == 0)
            ny--;
        else if(dir == 1)
            nx--;
        else if(dir == 2)
            ny++;
        else if(dir == 3)
            nx++;
        else
            return;
        
        if(0 <= nx && nx < gridWidth && 0 <= ny && ny < gridHeigth)
        {
            if(mezok[nx][ny].elert == false)
            {
                mezok[nx][ny].elert = true;
                mezok[x][y].zart[dir] = false;
                mezok[nx][ny].zart[dir ^ 2] = false;
                elerni--;
                elert.add(mezok[nx][ny]);
            }
            else if(plusz)
            {
                mezok[nx][ny].elert = true;
                mezok[x][y].zart[dir] = false;
                mezok[nx][ny].zart[dir ^ 2] = false;
            }
        }
    }
    
    /**
     * JSON objektumba csomagolja a mezőket
     *
     * @return A JSON objektum
     */
    public JSONObject toJson()
    {
        JSONObject ret = new JSONObject();
        
        for(int i = 0; i < gridWidth; i++)
        {
            JSONObject oszlop = new JSONObject();
            for(int j = 0; j < gridHeigth; j++)
            {
                oszlop.put(j + "", mezok[i][j].toJSON());
            }
            ret.put(i + "", oszlop);
        }
        
        return ret;
    }
    
}
