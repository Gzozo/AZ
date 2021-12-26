package AZ;

import org.json.JSONObject;

/**
 * Klienst tárolja
 */
public class Client
{
    public Tank t;
    public boolean joined = true;
    int death, kill;
    String name = "", picture = "";
    
    public Client(Tank t)
    {
        this.t = t;
        death = kill = 0;
    }
    
    public Client()
    {
        this(null);
    }
    
    public JSONObject toJson()
    {
        JSONObject ret = new JSONObject();
        ret.put(Const.kill, kill);
        ret.put(Const.death, death);
        ret.put(Const.picture, picture);
        ret.put(Const.name, name);
        return ret;
    }
    
    public void fromJSON(JSONObject set)
    {
        kill = set.getInt(Const.kill);
        death = set.getInt(Const.death);
        picture = set.getString(Const.picture);
        name = set.getString(Const.name);
    }
}
