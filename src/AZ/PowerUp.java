package AZ;

import java.awt.Color;

import org.json.JSONObject;

/**
 * Pályán lévő, felszedhető erősítő
 */
public class PowerUp extends Ammo
{
	
	Ammo ammo;
	
	public PowerUp()
	{
		this(0, 0, 0, null, Ammo.getDefaultAmmo());
	}
	
	public PowerUp(double x, double y, double rad, Field f, Ammo ammo)
	{
		super(x, y, rad, 0, 0, 12000, f);
		setPic(ammo.pic);
		type = "power";
		c = Color.GRAY;
		this.ammo = ammo;
	}
	
	@Override
	public synchronized void Tick(GameManager manager)
	{
		Tick(manager, false);
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

	/**
	 * Nem semmisíti meg a tankot, helyette módosítja hogy mit lő
	 * @param t A tank
	 */
	@Override
	public void OnContact(Tank t)
	{
		Ammo a = ammo.newInstance();
		if (a == null)
		{
			a = Ammo.getDefaultAmmo();
		}
		t.ammo = a;
		t.manager.PlayMusic(Const.Music.obtainPowerUp);
	}
	
	@Override
	public JSONObject toJSON()
	{
		JSONObject ret = super.toJSON();
		ret.put("pic", pic);
		return ret;
	}
	
	@Override
	public void setFromJSON(JSONObject set)
	{
		super.setFromJSON(set);
		setPic(set.getString("pic"));
	}
	
}
