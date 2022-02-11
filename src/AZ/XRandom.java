package AZ;

import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class XRandom extends Random
{
    long seed;
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
    
    public XRandom()
    {
        this(seedUniquifier() ^ System.nanoTime());
    }
    
    public XRandom(long seed)
    {
        super(seed);
        this.seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
    }
    
    public long getSeed()
    {
        return seed ^ 0x5DEECE66DL;
    }
    
    @Override
    public synchronized void setSeed(long seed)
    {
        super.setSeed(seed);
        this.seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
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
    
    @Override
    protected int next(int bits)
    {
        seed = super.next(bits);
        setSeed(seed);
        return super.next(bits);
    }
    
    public JSONObject toJSON()
    {
        JSONObject ret = new JSONObject();
        ret.put("seed", getSeed());
        return ret;
    }
    
    public void fromJSON(JSONObject set)
    {
        setSeed(set.optLong("seed", seed));
    }
}
