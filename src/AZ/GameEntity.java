package AZ;

import java.awt.Graphics2D;

import org.json.JSONObject;

public interface GameEntity
{
	/**
	 * Rajzol
	 * @param g A v�szon amire rajzol
	 */
    void Draw(Graphics2D g);

	/**
	 * Kit�rli a v�szonr�l mag�t
	 * @param g A v�szon
	 */
    void Erase(Graphics2D g);

	/**
	 * Ez fut le megadott id?k�z�nk�nt
	 * @param manager A j�t�k menedzser
	 */
    void Tick(GameManager manager);

	/**
	 * JSON objektumm� alak�tja a sz�ks�ges adatokat
	 * @return A JSON objektum
	 */
    JSONObject toJSON();

	/**
	 * JSON objektumb�l vissza�ll�tja az �rt�keket
	 * @param set A JSON objektum
	 */
    void setFromJSON(JSONObject set);
	
}
