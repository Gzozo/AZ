package AZ;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TestRandom
{
    @Test
    public void RandomTest()
    {
        long seed = 2;
        XRandom r1 = new XRandom(seed), r2 = new XRandom();
        //assertEquals(seed, r1.seed, 0.001);
        double d1 = r1.nextDouble();
        long seed2 = r1.getSeed();
        double d2 = r1.nextDouble();
        r1.setSeed(r2.getSeed());
        assertEquals(r1.nextInt(), r2.nextInt());
        
        
        r2.setSeed(seed);
        assertEquals(d1, r2.nextDouble(), 0.001);
        assertEquals(seed2, r2.getSeed());
        assertEquals(d2, r2.nextDouble(), 0.001);
    }
    
    @Test
    public void CheckSeed()
    {
        XRandom r1 = new XRandom(), r2 = new XRandom();
        r2.setSeed(r1.getSeed());
        assertEquals(r1.getSeed(), r2.getSeed());
        assertEquals(r1.nextInt(), r2.nextInt());
        r2.setSeed(r1.nextInt());
        r1.fromJSON(r2.toJSON());
        assertEquals(r1.getSeed(), r2.getSeed());
        assertEquals(r1.nextInt(), r2.nextInt());
        assertEquals(r1.getSeed(), r2.getSeed());
        assertEquals(r1.nextInt(), r2.nextInt());
        assertEquals(r1.getSeed(), r2.getSeed());
        assertEquals(r1.nextInt(), r2.nextInt());
        
    }
    
}
