package AZ;

import java.awt.Graphics2D;

import org.json.JSONObject;

public interface GameEntity
{
	/**
	 * Rajzol
	 * @param g A vászon amire rajzol
	 */
    void Draw(Graphics2D g);

	/**
	 * Kitörli a vászonról magát
	 * @param g A vászon
	 */
    void Erase(Graphics2D g);

	/**
	 * Ez fut le megadott id?közönként
	 * @param manager A játék menedzser
	 */
    void Tick(GameManager manager);

	/**
	 * JSON objektummá alakítja a szükséges adatokat
	 * @return A JSON objektum
	 */
    JSONObject toJSON();

	/**
	 * JSON objektumból visszaállítja az értékeket
	 * @param set A JSON objektum
	 */
    void setFromJSON(JSONObject set);
	
}
