package AZ;

import java.util.List;

public interface GameManager
{
    /**
     * Elt�vol�tja a GameEntity-t
     *
     * @param entity A GameEntity
     */
    void RemoveEntity(GameEntity entity);
    
    /**
     * �tk�z�st vizsg�l az �ssze tankkal
     *
     * @param ammo A l�ved�k ami �tk�zik
     * @return �tk�z�tt e?
     */
    boolean CheckTank(Ammo ammo);
    
    /**
     * Hozz�ad egy GameEntity-t
     *
     * @param entity A GameEntity
     */
    void AddEntity(GameEntity entity);
    
    /**
     * Halott kliens �rtes�t�se, hogy meghalt
     *
     * @param t A kliens tankja
     */
    void deadTank(Tank t);
    
    /**
     * Zene lej�tsz�sa
     *
     * @param f Zene neve
     */
    void PlayMusic(String f);
    
    /**
     * Visszaadja a j�t�kosok list�j�t
     *
     * @return A j�t�kosok list�ja
     */
    List<Tank> getPlayers();
    
    long ellapsedTime();
}
